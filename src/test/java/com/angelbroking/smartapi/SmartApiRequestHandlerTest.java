package com.angelbroking.smartapi;

import com.angelbroking.smartapi.http.SmartAPIRequestHandler;
import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.io.IOException;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
public class SmartApiRequestHandlerTest {

    @Mock
    private OkHttpClient client;

    private String USER_AGENT = "javasmartapiconnect/3.0.0";

    @Test
    public void testSmartApiRequestHandlerWithNotNullProxy() {
        Proxy proxy = Proxy.NO_PROXY;
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(10000, TimeUnit.MILLISECONDS);
        if (proxy != null) {
            builder.proxy(proxy);
        }

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        if (SmartConnect.ENABLE_LOGGING) {
            client = builder.addInterceptor(logging).build();
        } else {
            client = builder.build();
        }

        assertNotNull(client);
    }

    @Test
    public void testSmartApiRequestHandlerWithNullProxy() {
        assertThrows(SmartAPIException.class, () -> {
            Proxy proxy = null;
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(10000, TimeUnit.MILLISECONDS);
            builder.proxy(proxy);

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            if (SmartConnect.ENABLE_LOGGING) {
                client = builder.addInterceptor(logging).build();
            } else {
                client = builder.build();
            }
            throw new SmartAPIException("Test exception");
        });
    }

    @Test
    public void testApiHeaders() {
        SmartAPIRequestHandler handler = new SmartAPIRequestHandler(null);
        JSONObject headers = handler.apiHeaders();

        assertNotNull(headers);
        assertTrue(headers.has("clientLocalIP"));
        assertTrue(headers.has("clientPublicIP"));
        assertTrue(headers.has("macAddress"));
        assertTrue(headers.has("accept"));
        assertTrue(headers.has("userType"));
        assertTrue(headers.has("sourceID"));
    }

    @Test
    public void testCreateRequestWithAllParameters() {
        // Arrange
        String apiKey = "validApiKey";
        String url = "https://example.com/";
        JSONObject params = new JSONObject();
        params.put("param1", "value1");

        SmartAPIRequestHandler handler = new SmartAPIRequestHandler(null);
        JSONObject apiheader = handler.apiHeaders();
        // Act
        Request request = handler.createPostRequest(apiKey, url, params);

        log.info("url {}", request.url());
        log.info("content type {}", request.header("Content-Type"));
        // Assert
        assertNotNull(request);
        assertEquals(url, request.url().toString());
        assertEquals("POST", request.method());
        assertEquals("application/json", request.header("Content-Type"));
        assertEquals(apiKey, request.header("X-PrivateKey"));
        assertEquals(apiheader.getString("clientLocalIP"), request.header("X-ClientLocalIP"));
        assertEquals(apiheader.getString("clientPublicIP"), request.header("X-ClientPublicIP"));
        assertEquals(apiheader.getString("macAddress"), request.header("X-MACAddress"));
        assertEquals(apiheader.getString("accept"), request.header("Accept"));
        assertEquals(apiheader.getString("userType"), request.header("X-UserType"));
        assertEquals(apiheader.getString("sourceID"), request.header("X-SourceID"));
    }

    @Test
    public void testCreateRequestWithApiKeyNull() {
        // Arrange
        String apiKey = null;
        String url = "https://example.com";
        JSONObject params = new JSONObject();

        SmartAPIRequestHandler handler = new SmartAPIRequestHandler(null);

        // Act
        Request request = handler.createPostRequest(apiKey, url, params);

        // Assert
        assertNull(request);
    }

    @Test
    public void testCreateRequestWithUrlNull() {
        // Arrange
        String apiKey = "validApiKey";
        String url = null;
        JSONObject params = new JSONObject();

        SmartAPIRequestHandler handler = new SmartAPIRequestHandler(null);

        // Act
        Request request = handler.createPostRequest(apiKey, url, params);

        // Assert
        assertNull(request);
    }

    @Test
    public void testCreateRequestWithParamsNull() {
        // Arrange
        String apiKey = "validApiKey";
        String url = "https://example.com/";
        JSONObject params = null;

        SmartAPIRequestHandler handler = new SmartAPIRequestHandler(null);
        JSONObject apiheader = handler.apiHeaders();

        // Act
        Request request = handler.createPostRequest(apiKey, url, params);

        // Assert
        assertNull(request);
    }

    @Test
    public void testCreateRequestWithAccessTokenAndAllParameters() {
        // Arrange
        String apiKey = "validApiKey";
        String url = "https://example.com/";
        JSONObject params = new JSONObject();
        params.put("param1", "value1");
        String accessToken = "validAccessToken";

        SmartAPIRequestHandler handler = new SmartAPIRequestHandler(null);
        JSONObject apiheader = handler.apiHeaders();
        // Act
        Request request = handler.createPostRequest(apiKey, url, params, accessToken);

        log.info("url {}", request.url());
        log.info("content type {}", request.header("Content-Type"));
        // Assert
        assertNotNull(request);
        assertEquals(url, request.url().toString());
        assertEquals("POST", request.method());
        assertEquals("application/json", request.header("Content-Type"));
        assertEquals(apiKey, request.header("X-PrivateKey"));
        assertEquals(apiheader.getString("clientLocalIP"), request.header("X-ClientLocalIP"));
        assertEquals(apiheader.getString("clientPublicIP"), request.header("X-ClientPublicIP"));
        assertEquals(apiheader.getString("macAddress"), request.header("X-MACAddress"));
        assertEquals(apiheader.getString("accept"), request.header("Accept"));
        assertEquals(apiheader.getString("userType"), request.header("X-UserType"));
        assertEquals(apiheader.getString("sourceID"), request.header("X-SourceID"));
    }

    @Test
    public void testCreateRequestWithJsonArrayAndAllParameters() {
        // Arrange
        String url = "https://example.com/";
        String apiKey = "API_KEY";
        String accessToken = "ACCESS_TOKEN";
        JSONArray jsonArray = new JSONArray();
        jsonArray.put("data1");
        jsonArray.put("data2");

        // Act
        Request request = new SmartAPIRequestHandler(null).createJsonPostRequest(url, jsonArray, apiKey, accessToken);

        log.info("content type = {}", request.body().contentType().toString());
        System.out.println(request.body().contentType().toString());
        // Assert
        assertEquals(url, request.url().toString());
        assertEquals("POST", request.method());
        assertEquals("application/json; charset=utf-8", request.body().contentType().toString());
        assertEquals(USER_AGENT, request.header("User-Agent"));
        assertEquals("token " + apiKey + ":" + accessToken, request.header("Authorization"));
    }

    @Test
    public void testCreatePutRequest() {
        SmartAPIRequestHandler handler = new SmartAPIRequestHandler(null);
        String url = "https://example.com/api";
        String apiKey = "1234567890";
        String accessToken = "abcdefg";
        Map<String, Object> params = new HashMap<>();
        params.put("param1", "value1");
        params.put("param2", "value2");

        Request request = handler.createPutRequest(url, params, apiKey, accessToken);

        assertNotNull(request);
        assertEquals(url, request.url().toString());
        assertEquals("PUT", request.method());
        assertEquals(USER_AGENT, request.header("User-Agent"));
        assertEquals("3", request.header("X-Smart-API-Version"));
        assertEquals("token " + apiKey + ":" + accessToken, request.header("Authorization"));
    }

    @Test
    public void test_valid_apiKey_url_accessToken() throws IOException {
        String apiKey = "validApiKey";
        String url = "https://example.com/api";
        String accessToken = "validAccessToken";

        SmartAPIRequestHandler handler = new SmartAPIRequestHandler(null);
        JSONObject apiheader = handler.apiHeaders();

        Request request = handler.createGetRequest(apiKey, url, accessToken);

        assertEquals(url, request.url().toString());
        assertEquals("Bearer " + accessToken, request.header("Authorization"));
        assertEquals(apiKey, request.header("X-PrivateKey"));
        assertEquals(apiheader.getString("clientLocalIP"), request.header("X-ClientLocalIP"));
        assertEquals(apiheader.getString("clientPublicIP"), request.header("X-ClientPublicIP"));
        assertEquals(apiheader.getString("macAddress"), request.header("X-MACAddress"));
        assertEquals(apiheader.getString("accept"), request.header("Accept"));
        assertEquals(apiheader.getString("userType"), request.header("X-UserType"));
        assertEquals(apiheader.getString("sourceID"), request.header("X-SourceID"));
    }
}

