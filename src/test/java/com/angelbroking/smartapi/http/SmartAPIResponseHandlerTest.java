package com.angelbroking.smartapi.http;

import com.angelbroking.smartapi.http.exceptions.DataException;
import com.angelbroking.smartapi.http.exceptions.GeneralException;
import com.angelbroking.smartapi.http.exceptions.InputException;
import com.angelbroking.smartapi.http.exceptions.NetworkException;
import com.angelbroking.smartapi.http.exceptions.OrderException;
import com.angelbroking.smartapi.http.exceptions.PermissionException;
import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import com.angelbroking.smartapi.http.exceptions.TokenException;
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
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SmartAPIResponseHandlerTest {

    private final SmartAPIResponseHandler handler = new SmartAPIResponseHandler();

    @Test
    public void testHandleSuccess() throws JSONException, IOException, SmartAPIException {
        JSONObject responseJson = new JSONObject();
        responseJson.put("status", true);
        responseJson.put("errorcode", "");

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
    public void testHandlePermissionExceptionFailure() throws JSONException, IOException, SmartAPIException {
        JSONObject responseJson = new JSONObject();
        responseJson.put("status", false);
        responseJson.put("errorcode", "AB1011");
        responseJson.put("message", "Permission exception occurred");

        ResponseBody responseBody = ResponseBody.create(MediaType.parse("application/json"), responseJson.toString());
        Response response = new Response.Builder()
                .request(new Request.Builder().url("http://example.com").build())
                .header("Content-Type", "application/json")
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("")
                .body(responseBody)
                .build();
        assertThrows(PermissionException.class, () -> handler.handle(response, responseJson.toString()));
    }
  @Test
    public void testHandleDataExceptionFailure() throws JSONException, IOException, SmartAPIException {
        JSONObject responseJson = new JSONObject();
        responseJson.put("status", false);
        responseJson.put("errorcode", "AG8002");
        responseJson.put("message", "Data Exception occurred");

        ResponseBody responseBody = ResponseBody.create(MediaType.parse("application/json"), responseJson.toString());
        Response response = new Response.Builder()
                .request(new Request.Builder().url("http://example.com").build())
                .header("Content-Type", "application/json")
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("")
                .body(responseBody)
                .build();
        assertThrows(DataException.class, () -> handler.handle(response, responseJson.toString()));
    }
  @Test
    public void testGeneralExceptionFailure() throws JSONException, IOException, SmartAPIException {
        JSONObject responseJson = new JSONObject();
        responseJson.put("status", false);
        responseJson.put("errorcode", "AB2000");
        responseJson.put("message", "General Exception occurred");

        ResponseBody responseBody = ResponseBody.create(MediaType.parse("application/json"), responseJson.toString());
        Response response = new Response.Builder()
                .request(new Request.Builder().url("http://example.com").build())
                .header("Content-Type", "application/json")
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("")
                .body(responseBody)
                .build();
        assertThrows(GeneralException.class, () -> handler.handle(response, responseJson.toString()));
    }
  @Test
    public void testHandleInputExceptionFailure() throws JSONException, IOException, SmartAPIException {
        JSONObject responseJson = new JSONObject();
        responseJson.put("status", false);
        responseJson.put("errorcode", "AB1002");
        responseJson.put("message", "Input Exception occurred");

        ResponseBody responseBody = ResponseBody.create(MediaType.parse("application/json"), responseJson.toString());
        Response response = new Response.Builder()
                .request(new Request.Builder().url("http://example.com").build())
                .header("Content-Type", "application/json")
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("")
                .body(responseBody)
                .build();
        assertThrows(InputException.class, () -> handler.handle(response, responseJson.toString()));
    }
  @Test
    public void testHandleOrderExceptionFailure() throws JSONException, IOException, SmartAPIException {
        JSONObject responseJson = new JSONObject();
        responseJson.put("status", false);
        responseJson.put("errorcode", "AB1017");
        responseJson.put("message", "Order Exception occurred");

        ResponseBody responseBody = ResponseBody.create(MediaType.parse("application/json"), responseJson.toString());
        Response response = new Response.Builder()
                .request(new Request.Builder().url("http://example.com").build())
                .header("Content-Type", "application/json")
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("")
                .body(responseBody)
                .build();
        assertThrows(OrderException.class, () -> handler.handle(response, responseJson.toString()));
    }
@Test
    public void testHandleTokenExceptionFailure() throws JSONException, IOException, SmartAPIException {
        JSONObject responseJson = new JSONObject();
        responseJson.put("status", false);
        responseJson.put("errorcode", "AB1010");
        responseJson.put("message", "Token Exception occurred");

        ResponseBody responseBody = ResponseBody.create(MediaType.parse("application/json"), responseJson.toString());
        Response response = new Response.Builder()
                .request(new Request.Builder().url("http://example.com").build())
                .header("Content-Type", "application/json")
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("")
                .body(responseBody)
                .build();
        assertThrows(TokenException.class, () -> handler.handle(response, responseJson.toString()));
    }
@Test
    public void testHandleNetworkExceptionFailure() throws JSONException, IOException, SmartAPIException {
        JSONObject responseJson = new JSONObject();
        responseJson.put("status", false);
        responseJson.put("errorcode", "NetworkException");
        responseJson.put("message", "Network Exception occurred");

        ResponseBody responseBody = ResponseBody.create(MediaType.parse("application/json"), responseJson.toString());
        Response response = new Response.Builder()
                .request(new Request.Builder().url("http://example.com").build())
                .header("Content-Type", "application/json")
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("")
                .body(responseBody)
                .build();
        assertThrows(NetworkException.class, () -> handler.handle(response, responseJson.toString()));
    }

@Test
    public void testHandleSmartAPIExceptionFailure() throws JSONException, IOException, SmartAPIException {
        JSONObject responseJson = new JSONObject();
        responseJson.put("status", false);
        responseJson.put("errorcode", "default");
        responseJson.put("message", "SmartAPI Exception occurred");

        ResponseBody responseBody = ResponseBody.create(MediaType.parse("application/json"), responseJson.toString());
        Response response = new Response.Builder()
                .request(new Request.Builder().url("http://example.com").build())
                .header("Content-Type", "application/json")
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("")
                .body(responseBody)
                .build();
        assertThrows(SmartAPIException.class, () -> handler.handle(response, responseJson.toString()));
    }


}
