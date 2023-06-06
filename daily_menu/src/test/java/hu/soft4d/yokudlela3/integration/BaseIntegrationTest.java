package hu.soft4d.yokudlela3.integration;

import static hu.soft4d.yokudlela3.integration.IntegrationTestConstant.DAILYMENU_ADMIN;
import static hu.soft4d.yokudlela3.integration.IntegrationTestConstant.DAILYMENU_ADMIN_PASSWORD;
import static io.restassured.RestAssured.given;

import hu.soft4d.yokudlela3.application.annotation.MethodLogging;
import hu.soft4d.yokudlela3.integration.config.testcontainers.TestContainerConfig;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
@MethodLogging
@QuarkusIntegrationTest
@QuarkusTestResource(TestContainerConfig.class)
public class BaseIntegrationTest {

    // DEVELOPER NOTE: This will automatically start test app and related containers, even if all integration tests are disabled
    @Test
    public void runTestEcosystem() {

    }

    protected RequestSpecification getAdminAuthRequest() {
        return given()
                .auth()
                .oauth2(IntegrationTestHelper.getAccessToken(DAILYMENU_ADMIN, DAILYMENU_ADMIN_PASSWORD));
    }

}
