package com.yokudlela.dailymenu.integration;

import static io.restassured.RestAssured.given;

import com.yokudlela.dailymenu.integration.config.testcontainers.TestContainerConfig;
import com.yokudlela.dailymenu.application.annotation.MethodLogging;

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
                .oauth2(IntegrationTestHelper.getAccessToken(IntegrationTestConstant.DAILYMENU_ADMIN, IntegrationTestConstant.DAILYMENU_ADMIN_PASSWORD));
    }

}
