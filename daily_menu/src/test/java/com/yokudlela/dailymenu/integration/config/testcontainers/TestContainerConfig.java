package com.yokudlela.dailymenu.integration.config.testcontainers;

import com.yokudlela.dailymenu.application.RoleConstant;
import com.yokudlela.dailymenu.controller.ControllerConstant;
import com.yokudlela.dailymenu.integration.IntegrationTestConstant;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class TestContainerConfig implements QuarkusTestResourceLifecycleManager {

    private static final int MARIADB_PORT = 3306;
    private static final int KEYCLOAK_HTTP_PORT = 8080;
    private static final int KEYCLOAK_SECURE_HTTP_PORT = 8443;
    private static final String KEYCLOAK_REALM_CONFIG = "realm-export.json";
    private static final String KEYCLOAK_REALM_ADMIN_USER = "admin";
    private static final String KEYCLOAK_REALM_ADMIN_PASSWORD = "yokudlela";
    private static final String KEYCLOAK_CLIENT = "account";
    private static final String LOCATION_HEADER = "Location";

    private static String KEYCLOAK_SERVER_URL;

    private MariaDBContainer sqlContainer = new MariaDBContainer<>("mariadb:10.7.3")
            .withDatabaseName(IntegrationTestConstant.TEST)
            .withUsername(IntegrationTestConstant.TEST)
            .withPassword(IntegrationTestConstant.TEST)
            .withExposedPorts(MARIADB_PORT)
            .withReuse(false);

    private KeycloakContainer keycloakContainer = new KeycloakContainer()
            .withRealmImportFile(KEYCLOAK_REALM_CONFIG)
            .withAdminUsername(KEYCLOAK_REALM_ADMIN_USER)
            .withAdminPassword(KEYCLOAK_REALM_ADMIN_PASSWORD)
            .withExposedPorts(KEYCLOAK_HTTP_PORT, KEYCLOAK_SECURE_HTTP_PORT)
            .waitingFor(Wait.forLogMessage(".*Keycloak.*started.*", 1))
            .withReuse(false);


    @Override
    public Map<String, String> start() {
        sqlContainer.start();
        log.info("JDBC URL from container: " + sqlContainer.getJdbcUrl());

        keycloakContainer.start();
        log.info(keycloakContainer.getLogs());

        KEYCLOAK_SERVER_URL = "http://" + keycloakContainer.getHost() + ":" + keycloakContainer.getMappedPort(KEYCLOAK_HTTP_PORT);
        log.info("Creating test users... ");
        createTestUser(IntegrationTestConstant.DAILYMENU_ADMIN, IntegrationTestConstant.DAILYMENU_ADMIN_FIRSTNAME, IntegrationTestConstant.DAILYMENU_ADMIN_LASTNAME,
                        IntegrationTestConstant.DAILYMENU_ADMIN_PASSWORD,
                        List.of(RoleConstant.ADMIN_ROLE, RoleConstant.DISH_CLAIM_REQUEST_ROLE,
                                RoleConstant.DISH_CLAIM_RESPONSE_ROLE, RoleConstant.DISH_PAID_ROLE));
        createTestUser(IntegrationTestConstant.DAILYMENU_MANAGER, IntegrationTestConstant.DAILYMENU_MANAGER_FIRSTNAME, IntegrationTestConstant.DAILYMENU_MANAGER_LASTNAME,
                        IntegrationTestConstant.DAILYMENU_MANAGER_PASSWORD,
                        List.of(RoleConstant.MANAGER_ROLE, RoleConstant.DISH_CLAIM_REQUEST_ROLE,
                                RoleConstant.DISH_PAID_ROLE));

        return Map.of(
                "quarkus.profile", IntegrationTestConstant.PROFILE,
                "quarkus.datasource.jdbc.url", sqlContainer.getJdbcUrl(),
                "quarkus.datasource.username", sqlContainer.getUsername(),
                "quarkus.datasource.password", sqlContainer.getPassword(),
                IntegrationTestConstant.QUARKUS_OIDC_AUTH_SERVER_URL, KEYCLOAK_SERVER_URL + "/realms/" + IntegrationTestConstant.KEYCLOAK_REALM);
    }

    @Override
    public void stop() {
        // sqlContainer.stop();

        // given().auth().oauth2(getAdminAccessToken())
        //         .when()
        //         .delete(KEYCLOAK_SERVER_URL + "/admin/realms/" + KEYCLOAK_REALM)
        //         .then()
        //         .statusCode(204);
        // keycloakContainer.stop();
    }


    private void createTestUser(String emailAsUsername, String firstName, String lastName, String password,
            List<String> roles) {
        final Keycloak keycloakAdminClient = keycloakContainer.getKeycloakAdminClient();

        final UserRepresentation user = new UserRepresentation();
        user.setUsername(emailAsUsername);
        user.setEmail(emailAsUsername);
        user.setEmailVerified(true);
        user.setEnabled(true);
        user.setFirstName(firstName);
        user.setLastName(lastName);

        final CredentialRepresentation userRepresentation = new CredentialRepresentation();
        userRepresentation.setType(CredentialRepresentation.PASSWORD);
        userRepresentation.setValue(password);
        user.setCredentials(List.of(userRepresentation));

        final Map<String, List<String>> userClientRoles = new HashMap<>();
        userClientRoles.put(KEYCLOAK_CLIENT, roles);
        user.setClientRoles(userClientRoles);

        final RealmResource realm = keycloakAdminClient.realm(IntegrationTestConstant.KEYCLOAK_REALM);
        final Response userCreationResponse = realm.users().create(user);
        if (!Response.Status.Family.SUCCESSFUL.equals(userCreationResponse.getStatusInfo().getFamily())) {
            return;
        }

        final String location = userCreationResponse.getStringHeaders().get(LOCATION_HEADER).get(0);
        final String userId = location.substring(location.lastIndexOf(ControllerConstant.SLASH) + 1);

        // Get the client resource
        final ClientRepresentation client = realm.clients().findByClientId(KEYCLOAK_CLIENT).get(0);
        final String clientId = client.getId();
        final List<RoleRepresentation> roleRepresentationList = new ArrayList<>();

        roles.forEach(role -> {
            RoleResource rr = realm.clients().get(clientId).roles().get(role);
            if (null != rr) {
                roleRepresentationList.add(rr.toRepresentation());
            }
        });

        // Assign the client role to the user
        realm.users().get(userId).roles().clientLevel(clientId).add(roleRepresentationList);
    }

    private String getAdminAccessToken() {
        final Keycloak keycloakAdminClient = keycloakContainer.getKeycloakAdminClient();
        keycloakAdminClient.tokenManager().grantToken();
        keycloakAdminClient.tokenManager().refreshToken();
        AccessTokenResponse tokenResponse = keycloakAdminClient.tokenManager().getAccessToken();

        return tokenResponse.getToken();
    }
}
