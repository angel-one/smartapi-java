package com.angelbroking.smartapi.http;


import com.angelbroking.smartapi.models.ApiHeaders;
import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.Proxy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SmartAPIRequestHandlerTest {

    private static MockWebServer server;
    private static SmartAPIRequestHandler requestHandler;

    @BeforeAll
    public static void setUp() throws IOException {
        // Start a mock web server
        server = new MockWebServer();
        server.start();

        requestHandler = new SmartAPIRequestHandler(Proxy.NO_PROXY,10000);
    }

    @AfterAll
    public static void tearDown() throws IOException {
        // Shutdown the mock web server
        server.shutdown();
    }


    @Test
    void testGetRequest() throws IOException, SmartAPIException, JSONException, InterruptedException {
        // create a mock web server
        MockWebServer server = new MockWebServer();

        // set the response for the server
        JSONObject response = new JSONObject();
        response.put("key1", "value1");
        response.put("key2", "value2");
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .addHeader("X-API-KEY", "test-api-key")
                .setBody(response.toString());
        server.enqueue(mockResponse);


        // send a GET request to the server
        String apiKey = "test-api-key";
        String url = server.url("/test-url").toString();
        String accessToken = "test-access-token";
        JSONObject responseJson = requestHandler.getRequest(apiKey, url, accessToken);
        // verify the response
        RecordedRequest recordedRequest = server.takeRequest();
        assertEquals("GET", recordedRequest.getMethod());
        assertEquals("/test-url", recordedRequest.getPath());
        assertEquals("Bearer test-access-token", recordedRequest.getHeader("Authorization"));
        assertEquals(response.toString(), responseJson.toString());
    }

    @Test
     void testApiHeaders() throws JSONException {
        ApiHeaders headers = requestHandler.apiHeaders();
        assertNotNull(headers);
        assertEquals("USER",headers.getUserType());
        assertEquals("WEB", headers.getSourceID());
        assertNotNull(headers.getHeaderClientLocalIP() );
        assertNotNull(headers.getHeaderClientPublicIP());
        assertNotNull(headers.getMacAddress());
        assertEquals("application/json", headers.getAccept());
    }

    @Test
    void testPostRequest() throws JSONException, SmartAPIException, InterruptedException {
        // set the response for the server
        JSONObject response = new JSONObject();
        response.put("success", true);
        response.put("status", "message");
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .setBody(response.toString());
        server.enqueue(mockResponse);

        // send a POST request to the server
        String apiKey = "test-api-key";
        String url = server.url("/test-url").toString();
        JSONObject params = new JSONObject().put("param1", "value1");
        JSONObject responseJson = requestHandler.postRequest(apiKey, url, params);

        // verify the response
        assertEquals(response.toString(), responseJson.toString());
        assertNotNull(responseJson);
        assertTrue(responseJson.getBoolean("success"));
        assertEquals("message", responseJson.getString("status"));

        // verify the request
        RecordedRequest request  = server.takeRequest();
        assertEquals("POST", request.getMethod());
        assertEquals("/test-url", request.getPath());
        assertEquals("application/json; charset=utf-8", request.getHeader("Content-Type"));
        assertEquals(params.toString(), request.getBody().readUtf8());
    }
}

