package com.yokudlela.kitchen.cucumber

import com.yokudlela.kitchen.Constants.Companion.HTTP_LOCALHOST
import com.yokudlela.kitchen.Constants.Companion.KEYCLOAK_AUTH_SERVER_URL
import com.yokudlela.kitchen.Constants.Companion.KEYCLOAK_CLIENT
import com.yokudlela.kitchen.Constants.Companion.KEYCLOAK_CLIENT_VALUE
import com.yokudlela.kitchen.Constants.Companion.KEYCLOAK_TOKEN_URL
import com.yokudlela.kitchen.Constants.Companion.KEYCLOAK_URI
import com.yokudlela.kitchen.Constants.Companion.ROOT_URI
import com.yokudlela.kitchen.Constants.Companion.TEST_FILES_PATH
import com.yokudlela.kitchen.Constants.Companion.TEST_FILES_PATH_VALUE
import io.cucumber.core.options.Constants.GLUE_PROPERTY_NAME
import io.cucumber.core.options.Constants.PLUGIN_PUBLISH_QUIET_PROPERTY_NAME
import io.cucumber.java.Before
import org.junit.platform.suite.api.ConfigurationParameter
import org.junit.platform.suite.api.IncludeEngines
import org.junit.platform.suite.api.SelectClasspathResource
import org.junit.platform.suite.api.Suite
import org.springframework.boot.test.web.server.LocalServerPort

@Suite
@SelectClasspathResource("features")
@ConfigurationParameter(
    key = GLUE_PROPERTY_NAME,
    value = "classpath:com.yokudlela.integration, classpath:com.yokudlela.kitchen"
)
@ConfigurationParameter(key = PLUGIN_PUBLISH_QUIET_PROPERTY_NAME, value = "true")
@IncludeEngines("cucumber")
class CucumberTest {

    @LocalServerPort
    private var port: Int = 0

    // ! must use cucumber annotation
    @Before
    fun setSystemProperties() {
        System.setProperty(ROOT_URI, "$HTTP_LOCALHOST:$port")
        System.setProperty(KEYCLOAK_URI, System.getProperty(KEYCLOAK_AUTH_SERVER_URL) + KEYCLOAK_TOKEN_URL)
        System.setProperty(KEYCLOAK_CLIENT, KEYCLOAK_CLIENT_VALUE)
        System.setProperty(TEST_FILES_PATH, TEST_FILES_PATH_VALUE)
    }
}