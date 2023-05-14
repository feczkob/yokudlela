package com.yokudlela.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import static com.yokudlela.integration.HeaderNames.BEARER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@Slf4j
public class NetworkFunctions extends BaseFeature {

    private static final String GRANT_TYPE = "grant_type";
    private static final String PASSWORD = "password";
    private static final String CLIENT_ID = "client_id";
    private static final String USERNAME = "username";
    private static final String ACCESS_TOKEN = "access_token";

    @Given("set header param {string} to {string}")
    public void setHeader(String key, String value) {
        headers.put(key, value);
    }

    @Given("user {string} identified by {string} is logged in")
    public void login(String userName, String password) throws URISyntaxException, IOException, InterruptedException {
        String req = String.format("%s=%s&%s=%s&%s=%s&%s=%s",
                GRANT_TYPE, URLEncoder.encode(PASSWORD, StandardCharsets.UTF_8),
                CLIENT_ID, URLEncoder.encode(KEYCLOAK_CLIENT_VALUE, StandardCharsets.UTF_8),
                USERNAME, URLEncoder.encode(userName, StandardCharsets.UTF_8),
                PASSWORD, URLEncoder.encode(password, StandardCharsets.UTF_8));

        httpRequest = setRequestHeaders()
                .uri(new URI(KEYCLOAK_URI_VALUE))
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED)
                .POST(HttpRequest.BodyPublishers.ofString(req))
                .build();
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        JsonNode jsonNode = objectMapper.readTree(httpResponse.body());
        headers.put(HttpHeaders.AUTHORIZATION, BEARER + jsonNode.get(ACCESS_TOKEN).asText().trim());
    }

    @Then("exists in the response {string}")
    public void responseHasField(String propertyName) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        responseHasField(DEFAULT_RESPONSE_ID, propertyName);
    }

    @Then("exists in the {string} response {string}")
    public void responseHasField(String responseId, String propertyName) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        log.info(response.toString());
        String value = BeanUtils.getProperty(response.get(responseId), propertyName);
        assertNotNull(value);
    }

    // ! TODO
    @Then("exists in the response {string} with string value {string}")
    public void responseHasFieldWithStringValue(String propertyName, String expectedValue) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        responseHasFieldWithStringValue(DEFAULT_RESPONSE_ID, propertyName, expectedValue);
    }

    // ! TODO
    @Then("exists in the {string} response {string} with string value {string}")
    public void responseHasFieldWithStringValue(String responseId, String propertyName, String expectedValue) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        expectedValue = parse(expectedValue);

        if (propertyName.contains(".[x]")) {
            String[] values = BeanUtils.getArrayProperty(response.get(responseId), propertyName.split("..[x]")[0]);
            for (String value : values) {
                assertEquals(expectedValue, value);
            }
            assertFalse(false);

        } else {
            String value = BeanUtils.getProperty(response.get(responseId), propertyName);
            if (expectedValue.startsWith("\\")) {
                assertEquals(value.matches(expectedValue.substring(1)), Boolean.TRUE);
            } else
                assertEquals(expectedValue, value);
        }
    }

    // ! TODO
    @Then("exists in the {string} response")
    public void response(String responseId, String docString) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, JsonProcessingException {
        HashMap<String, Object> dest = parseJsonStringToMap(docString);

        if (responseId.isEmpty()) {
            responseId = DEFAULT_RESPONSE_ID;
        }
        HashMap source = (HashMap) response.get(responseId);

        compareMaps(dest, source);
    }

    @Then("exists in the {string} response with external {string}")
    public void responseExternal(String responseId, String filePath) throws IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        String payload = parseJsonFileToString(filePath);
        response(responseId, payload);
    }

    @Then("response status {int}")
    public void responseState(Integer httpStatusCode) {
        assertEquals(httpStatusCode, statusCode);
    }

    // ! TODO
    private void compareMaps(HashMap<String, Object> dest, HashMap<String, Object> source) {
        dest.forEach((key, value) -> {
            if (value instanceof HashMap) {
                compareMaps((HashMap) value, (HashMap) source.get(key));
            } else {
                assertEquals(value == null ?
                                null : value.toString(),
                        beanUtilWrapper(source, key));
            }
        });
    }

    // ! TODO
    private String beanUtilWrapper(Object source, String propertyName) {
        try {
            return BeanUtils.getProperty(source, propertyName);
        } catch (Exception e) {
            return null;
        }

    }

    @After
    public void clear() {
        headers.clear();
        response.clear();
        statusCode = 0;
    }
}
