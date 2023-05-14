package com.yokudlela.integration.rest;

import com.yokudlela.integration.BaseFeature;
import io.cucumber.java.en.When;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;

public class PutRequest extends BaseFeature {

    @When("put {string}")
    public void put(String urlPath, String docString) throws IOException, InterruptedException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, URISyntaxException {
        put(urlPath, DEFAULT_RESPONSE_ID, docString);
    }

    @When("put {string} into {string}")
    public void put(String urlPath, String responseId, String docString) throws IOException, InterruptedException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, URISyntaxException {
        putJsonRequest((responseId == null || responseId.isEmpty()) ? DEFAULT_RESPONSE_ID : responseId, urlPath, docString);
    }

    @When("put {string} with external {string}")
    public void putWithExternal(String urlPath, String filePath) throws IOException, URISyntaxException, InterruptedException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        String payload = parseJsonFileToString(filePath);
        put(urlPath, DEFAULT_RESPONSE_ID, payload);
    }

    @When("put {string} with external {string} into {string}")
    public void putWithExternal(String urlPath, String filePath, String responseId) throws IOException, InterruptedException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, URISyntaxException {
        String payload = parseJsonFileToString(filePath);
        put(urlPath, responseId, payload);
    }

    private void putJsonRequest(String id, String urlPostfix, String body) throws IOException, InterruptedException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, URISyntaxException {
        urlPostfix = parse(urlPostfix);
        URI uri = new URI(ROOT_URI_VALUE + urlPostfix);
        body = parse(body);
        requestLogString("PUT", uri, urlPostfix, body);

        httpRequest = setRequestHeaders()
                .uri(uri)
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .build();
        extractResponse(id);
    }
}
