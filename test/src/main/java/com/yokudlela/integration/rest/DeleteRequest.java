package com.yokudlela.integration.rest;

import com.yokudlela.integration.BaseFeature;
import io.cucumber.java.en.When;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;

public class DeleteRequest extends BaseFeature {

    @When("delete {string}")
    public void delete(String urlPath) throws IOException, URISyntaxException, InterruptedException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        delete(urlPath, DEFAULT_RESPONSE_ID);
    }

    @When("delete {string} into {string}")
    public void delete(String urlPath, String responseId) throws IOException, InterruptedException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, URISyntaxException {
        deleteJsonRequest((responseId == null || responseId.isEmpty()) ? DEFAULT_RESPONSE_ID : responseId, urlPath);
    }

    private void deleteJsonRequest(String id, String urlPostfix) throws IOException, InterruptedException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, URISyntaxException {
        urlPostfix = parse(urlPostfix);
        URI uri = new URI(ROOT_URI_VALUE + urlPostfix);

        requestLogString("DELETE", uri, urlPostfix, null);

        httpRequest = setRequestHeaders()
                .uri(uri)
                .DELETE()
                .build();

        extractResponse(id);
    }
}
