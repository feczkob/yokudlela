package com.yokudlela.integration

import com.github.dockerjava.api.DockerClient
import com.yokudlela.integration.Constants.Companion.HTTP_LOCALHOST
import com.yokudlela.integration.Constants.Companion.KEYCLOAK_AUTH_SERVER_URL
import com.yokudlela.integration.Constants.Companion.KEYCLOAK_CLIENT_VALUE
import com.yokudlela.integration.Constants.Companion.KEYCLOAK_REALM
import com.yokudlela.integration.Constants.Companion.KEYCLOAK_TOKEN_URL
import com.yokudlela.integration.config.ContainerConfig.Companion.KEYCLOAK_REALM_ADMIN_PASSWORD
import com.yokudlela.integration.config.ContainerConfig.Companion.KEYCLOAK_REALM_ADMIN_USER
import com.yokudlela.integration.config.ContainerConfig.Companion.keycloakContainerJboss
import com.yokudlela.integration.config.ContainerConfig.Companion.postgresContainer
import com.yokudlela.integration.config.ContainerConfig.Companion.rabbitContainer
import com.yokudlela.integration.config.ContainerConfig.Companion.redisContainer
import com.yokudlela.integration.config.KitchenIntegrationTestConfig
import com.yokudlela.kitchen.controller.model.request.LocationRequest
import com.yokudlela.kitchen.controller.model.request.OrderItemRequest
import com.yokudlela.kitchen.controller.model.request.OrderRequest
import com.yokudlela.kitchen.controller.model.response.LocationResponse
import com.yokudlela.kitchen.controller.model.response.OrderItemResponse
import com.yokudlela.kitchen.controller.model.response.OrderResponse
import com.yokudlela.integration.junit.model.User
import io.cucumber.spring.CucumberContextConfiguration
import org.junit.jupiter.api.BeforeAll
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.resource.RealmResource
import org.keycloak.representations.idm.ClientRepresentation
import org.keycloak.representations.idm.CredentialRepresentation
import org.keycloak.representations.idm.RoleRepresentation
import org.keycloak.representations.idm.UserRepresentation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.json.JacksonJsonParser
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import org.testcontainers.DockerClientFactory
import org.testcontainers.containers.GenericContainer
import java.util.*
import javax.ws.rs.core.Response
import kotlin.test.assertEquals
import kotlin.test.assertTrue


// * load a Spring application context before running Cucumber scenarios
@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(locations = ["classpath:application-test.yaml"])
@ContextConfiguration(classes = [KitchenIntegrationTestConfig::class])
class KitchenIntegrationBaseTest {

    @Autowired
    protected lateinit var restTemplate: RestTemplate

    @LocalServerPort
    protected var port: Int = 0

    protected var locationId = 0
    protected var orderId = 0
    protected var orderItemId = 0
    protected var rootUri = ""
    protected val kitchenAdmin = User(KEYCLOAK_KITCHEN_ADMIN, KEYCLOAK_KITCHEN_ADMIN_PASSWORD)
    protected val dailyMenuAdmin = User(KEYCLOAK_DAILY_MENU_ADMIN, KEYCLOAK_DAILY_MENU_ADMIN_PASSWORD)

    companion object {

        // * @JvmStatic is needed because
        // * Kotlin: Using protected members which are not @JvmStatic in the superclass companion is unsupported yet
        @JvmStatic
        protected val LOCATION = "location"

        @JvmStatic
        protected val ORDER = "order"

        @JvmStatic
        protected val ORDER_ITEM = "order-item"

        @JvmStatic
        protected val MODIFY = "modify"

        @JvmStatic
        protected val ALL = "all"

        private const val LOCATION_HEADER = "Location"
        private const val SLASH = "/"

        private lateinit var realm: RealmResource
        private var KEYCLOAK_AUTH_URI = ""

        private const val KEYCLOAK_KITCHEN_TEST_ADMIN = "tadm@yokudlela.hu"
        private const val KEYCLOAK_KITCHEN_TEST_ADMIN_FIRST_NAME = "Test"
        private const val KEYCLOAK_KITCHEN_TEST_ADMIN_LAST_NAME = "Admin"
        private const val KEYCLOAK_KITCHEN_TEST_ADMIN_PASSWORD = "test"
        private val KEYCLOAK_KITCHEN_TEST_ADMIN_ROLES = listOf("kitchen-admin", "dailymenu-dish-claim-request")

        private const val KEYCLOAK_KITCHEN_ADMIN = "kadm@yokudlela.hu"
        private const val KEYCLOAK_KITCHEN_ADMIN_FIRST_NAME = "Kitchen"
        private const val KEYCLOAK_KITCHEN_ADMIN_LAST_NAME = "Admin"
        private const val KEYCLOAK_KITCHEN_ADMIN_PASSWORD = "kitchen"
        private val KEYCLOAK_KITCHEN_ADMIN_ROLES = listOf("kitchen-admin")

        private const val KEYCLOAK_DAILY_MENU_ADMIN = "dmadm@yokudlela.hu"
        private const val KEYCLOAK_DAILY_MENU_ADMIN_FIRST_NAME = "Daily Menu"
        private const val KEYCLOAK_DAILY_MENU_ADMIN_LAST_NAME = "Admin"
        private const val KEYCLOAK_DAILY_MENU_ADMIN_PASSWORD = "daily-menu"
        private val KEYCLOAK_DAILY_MENU_ADMIN_ROLES = listOf("dailymenu-dish-claim-request")

        @JvmStatic
        @BeforeAll
        fun setupContainers() {
            // * do not stop containers between different Test classes
            // * https://www.testcontainers.org/test_framework_integration/manual_lifecycle_control/#singleton-containers
            // * https://stackoverflow.com/questions/64528638/springboottest-testcontainers-container-start-up-mapped-port-can-only-be-obt
            removeExitedAndStartNew(postgresContainer)
            removeExitedAndStartNew(rabbitContainer)
            removeExitedAndStartNew(redisContainer)
//            removeExitedAndStartNew(keycloakContainerDasniko)
            removeExitedAndStartNew(keycloakContainerJboss)

            // * property config during run-time
            // * https://stackoverflow.com/questions/74110777/dynamicpropertysource-not-being-invoked-kotlin-spring-boot-and-testcontainers
            System.setProperty("spring.datasource.url", postgresContainer.jdbcUrl)

            System.setProperty("spring.rabbitmq.host", rabbitContainer.host)
            System.setProperty("spring.rabbitmq.port", rabbitContainer.amqpPort.toString())

            System.setProperty("spring.redis.host", redisContainer.host)
            System.setProperty("spring.redis.port", redisContainer.firstMappedPort.toString())

//            KEYCLOAK_AUTH_URI = keycloakContainerDasniko.authServerUrl
            KEYCLOAK_AUTH_URI = "http://${keycloakContainerJboss.host}:${keycloakContainerJboss.getMappedPort(8080)}/auth/"
            System.setProperty(KEYCLOAK_AUTH_SERVER_URL, KEYCLOAK_AUTH_URI)

//            realm = keycloakContainerDasniko.keycloakAdminClient
//                .realm(KEYCLOAK_REALM)
            realm = Keycloak.getInstance(
                KEYCLOAK_AUTH_URI,
                "master",
                KEYCLOAK_REALM_ADMIN_USER,
                KEYCLOAK_REALM_ADMIN_PASSWORD,
                "admin-cli"
            ).realm(KEYCLOAK_REALM)

            createTestUser(
                KEYCLOAK_KITCHEN_TEST_ADMIN, KEYCLOAK_KITCHEN_TEST_ADMIN_FIRST_NAME,
                KEYCLOAK_KITCHEN_TEST_ADMIN_LAST_NAME, KEYCLOAK_KITCHEN_TEST_ADMIN_PASSWORD,
                KEYCLOAK_KITCHEN_TEST_ADMIN_ROLES
            )

            createTestUser(
                KEYCLOAK_KITCHEN_ADMIN, KEYCLOAK_KITCHEN_ADMIN_FIRST_NAME,
                KEYCLOAK_KITCHEN_ADMIN_LAST_NAME, KEYCLOAK_KITCHEN_ADMIN_PASSWORD,
                KEYCLOAK_KITCHEN_ADMIN_ROLES
            )

            createTestUser(
                KEYCLOAK_DAILY_MENU_ADMIN, KEYCLOAK_DAILY_MENU_ADMIN_FIRST_NAME,
                KEYCLOAK_DAILY_MENU_ADMIN_LAST_NAME, KEYCLOAK_DAILY_MENU_ADMIN_PASSWORD,
                KEYCLOAK_DAILY_MENU_ADMIN_ROLES
            )
        }

        private fun removeExitedAndStartNew(container: GenericContainer<*>) {
            // * removing exited test containers
            val dockerClient: DockerClient = DockerClientFactory.lazyClient()
            val dockerContainers = dockerClient.listContainersCmd()
                .withShowAll(true)
                .exec()
                .filter {
                    it.image.equals(container.dockerImageName) &&
                            it.labels.containsKey("org.testcontainers") &&
                            it.state.equals("exited")
                }
            dockerContainers.forEach { dockerClient.removeContainerCmd(it.id).exec() }

            // * starting new
            container.start()

            // * if testcontainers.reuse.enable is set to true in ~/.testcontainers.properties,
            // * then when turning off the PC, the test containers will exit (255)
            // * when powering up the PC, they wouldn't be removed by default, hence line 90 does it
            // * if reuse is not enabled, then this first section has no effect, since the test containers
            // * are removed after the tests
        }

        // * work of Karesz
        private fun createTestUser(
            emailAsUsername: String,
            firstName: String,
            lastName: String,
            password: String,
            roles: List<String>
        ) {
            if (realm.users().search(emailAsUsername).firstOrNull() != null) {
                return
            }

            val user = UserRepresentation()
            user.username = emailAsUsername
            user.email = emailAsUsername
            user.isEmailVerified = true
            user.isEnabled = true
            user.firstName = firstName
            user.lastName = lastName

            val credential = CredentialRepresentation()
            credential.type = CredentialRepresentation.PASSWORD
            credential.value = password
            user.credentials = listOf(credential)

            val userClientRoles: MutableMap<String, List<String>> = HashMap()
            userClientRoles[KEYCLOAK_CLIENT_VALUE] = roles
            user.clientRoles = userClientRoles

            val userCreationResponse: Response = realm.users().create(user)
            if (Response.Status.Family.SUCCESSFUL != userCreationResponse.statusInfo.family) {
                return
            }

            val location: String = userCreationResponse.stringHeaders[LOCATION_HEADER]?.get(0) ?: ""
            val userId: String = location.substring(location.lastIndexOf(SLASH) + 1)

            val client: ClientRepresentation = realm.clients().findByClientId(KEYCLOAK_CLIENT_VALUE)[0]
            val clientId: String = client.id
            val roleRepresentationList: MutableList<RoleRepresentation> = ArrayList()
            roles.forEach { role ->
                val rr = realm.clients()[clientId].roles()[role]
                if (null != rr) {
                    roleRepresentationList.add(rr.toRepresentation())
                }
            }

            realm.users()[userId].roles().clientLevel(clientId).add(roleRepresentationList)
        }

    }

    private fun obtainToken(user: User, authURI: String): String {
        val formData: MultiValueMap<String, String> = LinkedMultiValueMap()
        formData["grant_type"] = Collections.singletonList("password")
        formData["client_id"] = Collections.singletonList(KEYCLOAK_CLIENT_VALUE)
        formData["username"] = Collections.singletonList(user.username)
        formData["password"] = Collections.singletonList(user.password)
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_FORM_URLENCODED
        val httpEntity = HttpEntity(formData, httpHeaders)
        val response = restTemplate.postForEntity(authURI, httpEntity, String::class.java)
            .body

        val jacksonJsonParser = JacksonJsonParser()
        return jacksonJsonParser.parseMap(response)["access_token"].toString()
    }

    protected fun aLocation(): LocationRequest {
        return LocationRequest(
            "name",
            "details"
        )
    }

    protected fun anOrder(): OrderRequest {
        return OrderRequest(
            listOf(anOrderItem(), anOrderItem()), locationId, "details"
        )
    }

    protected fun anOrderItem(): OrderItemRequest {
        return OrderItemRequest(
            orderId, 1, 10, "description"
        )
    }

    protected fun createLocation(): LocationResponse {
        val response = sendPostAuth<LocationResponse>(
            "$HTTP_LOCALHOST:${port}/$LOCATION",
            aLocation(),
            User(KEYCLOAK_KITCHEN_TEST_ADMIN, KEYCLOAK_KITCHEN_TEST_ADMIN_PASSWORD)
        )
        locationId = response.body?.id!!
        return response.body!!
    }

    protected fun createOrder(): OrderResponse {
        createLocation()
        val response = sendPostAuth<OrderResponse>(
            "$HTTP_LOCALHOST:${port}/$ORDER",
            anOrder(),
            User(KEYCLOAK_KITCHEN_TEST_ADMIN, KEYCLOAK_KITCHEN_TEST_ADMIN_PASSWORD)
        )
        orderId = response.body?.id!!
        return response.body!!
    }

    protected fun createOrderItem(): OrderItemResponse {
        createOrder()
        val response = sendPostAuth<OrderItemResponse>(
            "$HTTP_LOCALHOST:${port}/$ORDER_ITEM",
            anOrderItem(),
            User(KEYCLOAK_KITCHEN_TEST_ADMIN, KEYCLOAK_KITCHEN_TEST_ADMIN_PASSWORD)
        )
        orderItemId = response.body?.id!!
        return response.body!!
    }

    protected final inline fun <reified K : Any> sendPostAuth(uri: String, body: Any, user: User): ResponseEntity<K> {
        val httpEntity = HttpEntity(body, prepareHeaders(user))
        return restTemplate.postForEntity(uri, httpEntity, K::class.java)
    }

    protected final inline fun <reified K : Any> sendPostNoAuth(uri: String, body: Any): ResponseEntity<K> {
        return restTemplate.postForEntity(uri, body, K::class.java)
    }

    protected final inline fun <reified K : Any> sendGetAuth(uri: String, user: User): ResponseEntity<K> {
        val httpEntity = HttpEntity<Void>(prepareHeaders(user))
        return restTemplate.exchange(uri, HttpMethod.GET, httpEntity, K::class.java)
    }

    protected final inline fun <reified K : Any> sendGetNoAuth(uri: String): ResponseEntity<K> {
        return restTemplate.getForEntity(uri, K::class.java)
    }

    protected final inline fun <reified K : Any> sendPutAuth(uri: String, body: Any, user: User): ResponseEntity<K> {
        val httpEntity = HttpEntity(body, prepareHeaders(user))
        return restTemplate.exchange(uri, HttpMethod.PUT, httpEntity, K::class.java)
    }

    protected final inline fun <reified K : Any> sendPutNoAuth(uri: String, body: Any): ResponseEntity<K> {
        return restTemplate.exchange(uri, HttpMethod.PUT, HttpEntity(body), K::class.java)
    }

    protected final inline fun <reified K : Any> sendDeleteAuth(uri: String, user: User): ResponseEntity<K> {
        val httpEntity = HttpEntity<Void>(prepareHeaders(user))
        return restTemplate.exchange(uri, HttpMethod.DELETE, httpEntity, K::class.java)
    }

    protected final inline fun <reified K : Any> sendDeleteNoAuth(uri: String): ResponseEntity<K> {
        return restTemplate.exchange(uri, HttpMethod.DELETE, HttpEntity.EMPTY, K::class.java)
    }

    @PublishedApi
    internal fun prepareHeaders(user: User): HttpHeaders {
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        httpHeaders.setBearerAuth(obtainToken(user,
            KEYCLOAK_AUTH_URI + KEYCLOAK_TOKEN_URL))
        return httpHeaders
    }

    protected infix fun <T> T.isEqualTo(other: T) {
        assertEquals(this, other)
    }

    protected infix fun String.doesContain(other: String) {
        assertTrue(this.contains(other))
    }
}
