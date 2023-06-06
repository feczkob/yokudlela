package com.yokudlela.integration.config

import dasniko.testcontainers.keycloak.KeycloakContainer
import org.testcontainers.containers.BindMode
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.RabbitMQContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.MountableFile
import java.time.Duration

class ContainerConfig {

    companion object {

        private const val YOKUDLELA = "yokudlela"
        private const val KITCHEN = "kitchen"
        private const val RABBIT_CONFIG = "rabbit/10-defaults.conf"
        private const val RABBIT_DEFINITIONS_FILE = "rabbit/definitions.json"
        private const val RABBIT_DEFINITIONS_CONTAINER = "/etc/definitions.json"
        private const val KEYCLOAK_REALM_DEF = "realm-export.json"
        const val KEYCLOAK_REALM_ADMIN_USER = "admin"
        const val KEYCLOAK_REALM_ADMIN_PASSWORD = "yokudlela"
        private const val POSTGRES_IMAGE = "postgres:latest"
        private const val RABBIT_IMAGE = "rabbitmq:3.10.5-management-alpine"
        private const val REDIS_IMAGE = "redis:latest"

        // * container - container communication
        // * https://stackoverflow.com/questions/71854493/testcontainers-communication-between-containers-mapped-outside-port

        val postgresContainer = PostgreSQLContainer<Nothing>(POSTGRES_IMAGE)
            .apply {
                withDatabaseName(YOKUDLELA)
                withUsername(KITCHEN)
                withPassword(KITCHEN)
                // * for reuse between consecutive test runs LOCALLY, set
                // * testcontainers.reuse.enable to true in ~/.testcontainers.properties
                // * https://stackoverflow.com/questions/62425598/how-to-reuse-testcontainers-between-multiple-springboottests
                // * https://www.testcontainers.org/features/reuse/
                withReuse(true)
            }

        // ! image must be the same as in the docker-compose.yaml
        val rabbitContainer: RabbitMQContainer = RabbitMQContainer(RABBIT_IMAGE)
            .apply {
                withExposedPorts(5672, 15672)
                withRabbitMQConfig(MountableFile.forClasspathResource(RABBIT_CONFIG))
                withClasspathResourceMapping(RABBIT_DEFINITIONS_FILE, RABBIT_DEFINITIONS_CONTAINER, BindMode.READ_ONLY)
                withReuse(true)
            }

        val redisContainer = GenericContainer<Nothing>(REDIS_IMAGE)
            .apply {
                withExposedPorts(6379)
                withReuse(true)
            }

        // * https://www.baeldung.com/spring-boot-keycloak-integration-testing
        val keycloakContainerDasniko: KeycloakContainer = KeycloakContainer()
            .apply {
                withRealmImportFile(KEYCLOAK_REALM_DEF)
                withAdminUsername(KEYCLOAK_REALM_ADMIN_USER)
                withAdminPassword(KEYCLOAK_REALM_ADMIN_PASSWORD)
                waitingFor(Wait.forLogMessage(".*Keycloak.*started.*", 1))
                withReuse(true)
            }

        val keycloakContainerJboss = GenericContainer<Nothing>("jboss/keycloak")
            .apply {
                withExposedPorts(8080)
                withEnv(mapOf("KEYCLOAK_IMPORT" to "/tmp/$KEYCLOAK_REALM_DEF",
                "KEYCLOAK_USER" to KEYCLOAK_REALM_ADMIN_USER,
                "KEYCLOAK_PASSWORD" to KEYCLOAK_REALM_ADMIN_PASSWORD,
                "DB_VENDOR" to "h2"))
                withCopyFileToContainer(MountableFile.forClasspathResource(KEYCLOAK_REALM_DEF), "/tmp/$KEYCLOAK_REALM_DEF")
                waitingFor(Wait.forLogMessage(".*Keycloak.*started.*", 1))
                withStartupTimeout(Duration.ofMinutes(5))
                withReuse(true)
            }
    }
}