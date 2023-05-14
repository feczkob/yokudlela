package com.yokudlela.integration.rest;

import com.yokudlela.integration.BaseFeature;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;

@Slf4j
public class PostRequest extends BaseFeature {

    @When("post {string}")
    public void post(String urlPath, String docString) throws IOException, InterruptedException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, URISyntaxException {
        post(urlPath, DEFAULT_RESPONSE_ID, docString);
    }

    @When("post {string} into {string}")
    public void post(String urlPath, String responseId, String docString) throws IOException, InterruptedException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, URISyntaxException {
        postJsonRequest((responseId == null || responseId.isEmpty()) ? DEFAULT_RESPONSE_ID : responseId, urlPath, docString);
    }

    @When("post {string} with external {string}")
    public void postWithExternal(String urlPath, String filePath) throws IOException, InterruptedException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, URISyntaxException {
        String payload = parseJsonFileToString(filePath);
        post(urlPath, DEFAULT_RESPONSE_ID, payload);
    }

    @When("post {string} with external {string} into {string}")
    public void postWithExternal(String urlPath, String filePath, String responseId) throws IOException, InterruptedException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, URISyntaxException {
        String payload = parseJsonFileToString(filePath);
        post(urlPath, responseId, payload);
    }

    private void postJsonRequest(String id, String urlPostfix, String body) throws IOException, InterruptedException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, URISyntaxException {
        urlPostfix = parse(urlPostfix);
        URI uri = new URI(ROOT_URI_VALUE + urlPostfix);
        body = parse(body);

        requestLogString("POST", uri, urlPostfix, body);

        httpRequest = setRequestHeaders()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        extractResponse(id);
    }
}
