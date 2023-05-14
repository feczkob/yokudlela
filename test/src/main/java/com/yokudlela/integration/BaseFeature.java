package com.yokudlela.integration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Slf4j
public class BaseFeature {

    private static final String TEST_USER = "test";
    private static final String RANDOM_PARAM = "${random(";
    private static final String CHAR_PARAM = "${char(";
    private static final String RESPONSE_PARAM = "${response(";
    private static final String ARRAY = "array";
    private static final String ROOT_URI = "root-uri";
    private static final String KEYCLOAK_URI = "keycloak-uri";
    private static final String KEYCLOAK_CLIENT = "keycloak-client";
    private static final String TEST_FILES_PATH = "test-files-path";
    private static final Random random = new Random();
    protected static final String DEFAULT_RESPONSE_ID = "def";
    protected static final String ROOT_URI_VALUE;
    protected static final String KEYCLOAK_URI_VALUE;
    protected static final String KEYCLOAK_CLIENT_VALUE;
    private static final String TEST_FILES_PATH_VALUE;
    protected static final Map<String, String> headers = new HashMap<>();
    protected static final Map<String, Object> response = new HashMap<>();
    // * some fields are needed to be static because new class is instantiated
    // * for every step definition
    protected static String logRequest = "";
    protected static Integer statusCode;
    protected final CookieManager cookieManager;
    protected final ObjectMapper objectMapper;
    protected final HttpClient httpClient;
    protected HttpRequest httpRequest;

    static {
        ROOT_URI_VALUE = System.getProperty(ROOT_URI);
        KEYCLOAK_URI_VALUE = System.getProperty(KEYCLOAK_URI);
        KEYCLOAK_CLIENT_VALUE = System.getProperty(KEYCLOAK_CLIENT);
        TEST_FILES_PATH_VALUE = System.getProperty(TEST_FILES_PATH);
    }

    public BaseFeature() {
        cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

        objectMapper = new ObjectMapper();

        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        objectMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, false);

        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        objectMapper.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);

        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.findAndRegisterModules();

        httpClient = HttpClient.newBuilder()
                .cookieHandler(cookieManager)
                .build();
        try {
            httpRequest = HttpRequest.newBuilder()
                    .uri(new URI(ROOT_URI_VALUE))
                    .build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    // ! TODO
    protected HashMap<String, Object> parseJsonStringToMap(String pResponse) throws JsonProcessingException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        pResponse = resolveResponseParams(pResponse).trim();

        HashMap<String, Object> tmp = null;
        if (pResponse.startsWith("{")) {
            tmp = objectMapper.readValue(pResponse, HashMap.class);
            Iterator<String> it = tmp.keySet().iterator();
            String tmpKey;
            Object tmpValue;
            while (it.hasNext()) {
                tmpKey = it.next();
                tmpValue = tmp.get(tmpKey);
                if (tmpValue instanceof String
                        && ((String) tmpValue).startsWith("{")
                        && ((String) tmpValue).endsWith("}")) {
                    tmp.put(tmpKey, parseJsonStringToMap((String) tmpValue));
                }
            }
        } else if (pResponse.startsWith("[")) {
            List list = objectMapper.readValue(pResponse, List.class);
            tmp = new HashMap<>();
            tmp.put(ARRAY, list);
        }
        return tmp;
    }

    protected String parseJsonFileToString(String fileNameWithPath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(String.format("%s%s", TEST_FILES_PATH_VALUE, fileNameWithPath))));
    }

    // ! TODO
    public String parse(String docString) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        String res = resolveResponseParams(docString);
        res = resolveRandomParams(res);
        res = resolveUUIDParams(res);
        return resolveCharacter(res);
    }

    // ! TODO
    private String resolveUUIDParams(String docString) {
        return docString.replace("\\$\\{uuid}", UUID.randomUUID().toString());
    }

    // ! TODO
    private String resolveRandomParams(String docString) {
        String doc;
        while (docString.contains(RANDOM_PARAM)) {
            int idx0 = docString.indexOf(RANDOM_PARAM);
            int idx1 = docString.indexOf(",", idx0);
            int idx2 = docString.indexOf(")", idx1 + 1);

            int idx3 = docString.indexOf("}", idx1);
            int min = Integer.parseInt(docString.substring(idx0 + RANDOM_PARAM.length(), idx1).trim());
            int max = Integer.parseInt(docString.substring(idx1 + 1, idx2).trim());

            if(max < min) {
                throw new RuntimeException("The max value cannot be less than the min value!");
            }

            int d = random.nextInt() * (max - min) + min;
            doc = docString.substring(0, idx0) + d + docString.substring(idx3 + 1);
            docString = doc;
        }
        return docString;
    }

    // ! TODO
    private String resolveCharacter(String docString) {
        String doc;
        while (docString.contains(CHAR_PARAM)) {
            int idx0 = docString.indexOf(CHAR_PARAM);
            int idx1 = docString.indexOf(",", idx0);
            int idx2 = docString.indexOf(")", idx1 + 1);

            int idx3 = docString.indexOf("}", idx1);
            String scr = docString.substring(idx0 + CHAR_PARAM.length(), idx1);
            int charIndex = Integer.parseInt(docString.substring(idx1 + 1, idx2));
            doc = docString.substring(0, idx0) + scr.charAt(charIndex) + docString.substring(idx3 + 1);
            docString = doc;
        }
        return docString;
    }

    // ! TODO
    private String resolveResponseParams(String docString) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        String doc;
        while (docString.contains(RESPONSE_PARAM)) {
            int idx0 = docString.indexOf(RESPONSE_PARAM);
            int idx1 = docString.indexOf(")", idx0);
            int idx2 = docString.indexOf("}", idx1);
            String responseKey = docString.substring(idx0 + RESPONSE_PARAM.length(), idx1);
            String value = BeanUtils.getProperty(response.get(responseKey), docString.substring(idx1 + 2, idx2));
            doc = docString.substring(0, idx0).concat(value).concat(docString.substring(idx2 + 1));
            docString = doc;
        }
        return docString;
    }

    protected void extractResponse(String id) throws IOException, InterruptedException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        statusCode = httpResponse.statusCode();

        if (!httpResponse.body().isEmpty()) {
            this.putResponse(id, httpResponse.body());
        }
    }

    // ! TODO
    protected void putResponse(String id, String data) throws JsonProcessingException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Map<String, Object> p = parseJsonStringToMap(data);
        if (p != null && (p.size() == 1 && p.get(ARRAY) != null)) {
            response.put(id, p.get(ARRAY));
        } else
            response.put(id, parseJsonStringToMap(data));
    }

    protected void requestLogString(String method, URI uri, String urlPostfix, String body) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        String res = "curl --request " + method + " "
                .concat("--url '" + uri.toString() + urlPostfix + "'");
        Iterator<String> it = headers.keySet().iterator();
        String key;
        while (it.hasNext()) {
            key = it.next();
            res = res.concat(" --header '" + key + ": " + parse(headers.get(key)) + "'");
        }

        res = res.concat((body == null || body.isEmpty()) ? "" : " --data '" + body + "'");

        logRequest = res;
        log.info(logRequest);
    }

    protected HttpRequest.Builder setRequestHeaders() {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        headers.forEach((k, v) -> {
            try {
                requestBuilder.setHeader(k, parse(v));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        return requestBuilder.setHeader(HttpHeaders.USER_AGENT, "Java HttpClient Bot")
                .setHeader(HeaderNames.USER, headers.get(HeaderNames.USER) == null ? TEST_USER : headers.get(HeaderNames.USER))
                .setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
    }

    // ! TODO
    protected String generateRandomString() {
        // * letter 'a'
        int leftLimit = 97;
        // * letter 'z'
        int rightLimit = 122;
        int targetStringLength = 5;
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (random.nextInt() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString();
    }
}



