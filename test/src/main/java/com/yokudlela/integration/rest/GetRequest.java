package com.yokudlela.integration.rest;

import com.yokudlela.integration.BaseFeature;
import io.cucumber.java.en.When;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;

public class GetRequest extends BaseFeature {

    @When("get {string}")
    public void get(String urlPath) throws IOException, InterruptedException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, URISyntaxException {
        get(urlPath, DEFAULT_RESPONSE_ID);
    }

    @When("get {string} into {string}")
    public void get(String urlPath, String responseId) throws IOException, InterruptedException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, URISyntaxException {
        getJsonRequest((responseId == null || responseId.isEmpty()) ? DEFAULT_RESPONSE_ID : responseId, urlPath);
    }

    private void getJsonRequest(String id, String urlPostfix) throws IOException, InterruptedException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, URISyntaxException {
        urlPostfix = parse(urlPostfix);
        URI uri = new URI(ROOT_URI_VALUE + urlPostfix);

        requestLogString("GET", uri, urlPostfix, null);

        httpRequest = setRequestHeaders()
                .uri(uri)
                .GET()
                .build();

        extractResponse(id);
    }
}
