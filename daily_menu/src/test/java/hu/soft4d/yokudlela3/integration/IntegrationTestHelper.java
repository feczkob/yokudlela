package hu.soft4d.yokudlela3.integration;

import static io.restassured.RestAssured.given;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.ConfigProvider;

@Slf4j
@UtilityClass
public class IntegrationTestHelper {

    public static final String QUERY_PARAM_PAGE = "page";
    public static final String QUERY_PARAM_SIZE = "size";
    public static final String PATH_PARAM_NAME = "name";
    public static final String PATH_PARAM_DAY = "day";
    public static final String PATH_PARAM_FROM_DAY = "fromDay";
    public static final String PATH_PARAM_TO_DAY = "toDay";
    public static final String PATH_PARAM_ACTIVE = "active";
    private  static final String CLIENT_ID = "account";


    public void logStart(String testClass, String testMethod) {
        log.info("BEGIN TEST - {}::{}", testClass, testMethod);
    }

    public void logEnd(String testClass, String testMethod) {
        log.info("END TEST - {}::{}", testClass, testMethod);
    }

    public String getAccessToken(String username, String password) {
        String authServerUrl =
                ConfigProvider.getConfig().getValue(IntegrationTestConstant.QUARKUS_OIDC_AUTH_SERVER_URL, String.class);

        return given()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .formParam("grant_type", "password")
                .formParam("username", username)
                .formParam("password", password)
                .formParam("client_id", CLIENT_ID)
                .when()
                .post(authServerUrl + "/protocol/openid-connect/token")
                .prettyPeek()
                .then()
                .statusCode(200)
                .extract()
                .path("access_token");
    }

}
