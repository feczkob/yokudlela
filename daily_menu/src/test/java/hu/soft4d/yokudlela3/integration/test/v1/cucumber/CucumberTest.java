package hu.soft4d.yokudlela3.integration.test.v1.cucumber;

import hu.soft4d.yokudlela3.integration.BaseIntegrationTest;
import hu.soft4d.yokudlela3.integration.IntegrationTestConstant;

import org.eclipse.microprofile.config.ConfigProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

//@ConfigurationParameter(key = PLUGIN_PUBLISH_QUIET_PROPERTY_NAME, value = "true")
//@IncludeEngines("cucumber")
public class CucumberTest extends BaseIntegrationTest {
    // IMPORTANT NOTE: class name must be start or end "Test" pre or postfix (in case of using those annotations above)

    public static final String ROOT_URI = "root-uri";
    public static final String KEYCLOAK_URI = "keycloak-uri";
    public static final String KEYCLOAK_CLIENT = "keycloak-client";
    public static final String KEYCLOAK_CLIENT_VALUE = "account";
    public static final String TEST_FILES_PATH = "test-files-path";
    public static final String TEST_FILES_PATH_VALUE = "src/test/resources/test_objects/";


    @BeforeAll
    public static void setSystemProperties() {
        String port =
                ConfigProvider.getConfig().getValue("quarkus.http.test-port", String.class);
        System.setProperty(ROOT_URI, IntegrationTestConstant.HOST + ":" + port);
        String authServerUrl =
                ConfigProvider.getConfig().getValue(
                        IntegrationTestConstant.QUARKUS_OIDC_AUTH_SERVER_URL ,
                        String.class)
                         + "/protocol/openid-connect/token";
        System.setProperty(KEYCLOAK_URI, authServerUrl);
        System.setProperty(KEYCLOAK_CLIENT, KEYCLOAK_CLIENT_VALUE);
        System.setProperty(TEST_FILES_PATH, TEST_FILES_PATH_VALUE);
    }

    @Test
    public void run() {
        io.cucumber.core.cli.Main.run(new String[]{"--glue", "hu.soft4d.integration", "classpath:features"});
    }

}
