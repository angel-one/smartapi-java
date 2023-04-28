package com.angelbroking.smartapi.http;

import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import com.angelbroking.smartapi.http.response.HttpResponse;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class SmartAPIResponseHandlerTest {

    private final SmartAPIResponseHandler handler = new SmartAPIResponseHandler();

    @Test
    public void testHandleSuccess() throws JSONException, IOException, SmartAPIException {
        JSONObject responseJson = new JSONObject();
        responseJson.put("status", "success");

        ResponseBody responseBody = ResponseBody.create(MediaType.parse("application/json"), responseJson.toString());
        Response response = new Response.Builder()
                .request(new Request.Builder().url("http://example.com").build())
                .header("Content-Type", "application/json")
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("")
                .body(responseBody)
                .build();

        HttpResponse result = handler.handle(response, responseJson.toString());

        assertEquals(responseJson.toString(), result.getBody());
    }

    @Test
    public void testHandleFailure() throws JSONException, IOException, SmartAPIException {
        JSONObject responseJson = new JSONObject();
        responseJson.put("status", "failure");

        ResponseBody responseBody = ResponseBody.create(MediaType.parse("application/json"), responseJson.toString());
        Response response = new Response.Builder()
                .request(new Request.Builder().url("http://example.com").build())
                .header("Content-Type", "application/json")
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("")
                .body(responseBody)
                .build();

        HttpResponse result = handler.handle(response, responseJson.toString());

        assertEquals(responseJson.toString(), result.getBody());
    }


}
