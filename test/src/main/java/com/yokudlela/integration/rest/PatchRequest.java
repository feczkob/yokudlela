package com.yokudlela.integration.rest;

import com.yokudlela.integration.BaseFeature;
import io.cucumber.java.en.When;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;

public class PatchRequest extends BaseFeature {

    // ! TODO
    @When("patch {string}")
    public void patch(String urlPath, String docString) throws IOException, InterruptedException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, URISyntaxException {
        patch(urlPath, DEFAULT_RESPONSE_ID, docString);
    }

    // ! TODO
    @When("patch {string} into {string}")
    public void patch(String urlPath, String responseId, String docString) throws IOException, InterruptedException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, URISyntaxException {
        patchJsonRequest((responseId == null || responseId.isEmpty()) ? DEFAULT_RESPONSE_ID : responseId, urlPath, docString);
    }

    // ! TODO
    private void patchJsonRequest(String id, String urlPostfix, String body) throws IOException, InterruptedException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, URISyntaxException {
        urlPostfix = parse(urlPostfix);
        URI uri = new URI(ROOT_URI_VALUE + urlPostfix);
        body = parse(body);

        requestLogString("PATCH", uri, urlPostfix, body);

        httpRequest = setRequestHeaders()
                .uri(uri)
                .method("PATCH", HttpRequest.BodyPublishers.ofString(body))
                .build();
        extractResponse(id);
    }
}
