package com.angelbroking.smartapi.http;

import com.angelbroking.smartapi.http.exceptions.DataException;
import com.angelbroking.smartapi.http.exceptions.GeneralException;
import com.angelbroking.smartapi.http.exceptions.InputException;
import com.angelbroking.smartapi.http.exceptions.NetworkException;
import com.angelbroking.smartapi.http.exceptions.OrderException;
import com.angelbroking.smartapi.http.exceptions.PermissionException;
import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import com.angelbroking.smartapi.http.exceptions.TokenException;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SmartAPIResponseHandlerTest {

    @Test
    public void testHandleMethod_Success() throws IOException, SmartAPIException, JSONException {
        // Create mock Response object
        Response response = Mockito.mock(Response.class);
        Mockito.when(response.header("Content-Type")).thenReturn("application/json");

        // Create sample JSON body
        String body = "{\"status\":\"SUCCESS\", \"data\": {\"key\":\"value\"}}";

        // Call the method and assert the output
        JSONObject jsonObject = new SmartAPIResponseHandler().handle(response, body);
        assertNotNull(jsonObject);
        assertEquals("value", jsonObject.getJSONObject("data").getString("key"));
    }

    @Test(expected = DataException.class)
    public void testHandleMethod_InvalidContentType() throws IOException, SmartAPIException, JSONException {
        // Create mock Response object
        Response response = Mockito.mock(Response.class);
        Mockito.when(response.header("Content-Type")).thenReturn("application/json");

        // Create sample JSON body
        String body = "{\"errorcode\":\"AG8002\", \"message\":\"Invalid Data\"}";
        // Call the method and expect DataException
        new SmartAPIResponseHandler().handle(response, body);
    }
    @Test(expected = TokenException.class)
    public void testHandleMethod_TokenException() throws IOException, SmartAPIException, JSONException {
        // Create mock Response object
        Response response = Mockito.mock(Response.class);
        Mockito.when(response.header("Content-Type")).thenReturn("application/json");

        // Create sample JSON body with error code
        String body = "{\"errorcode\":\"AB1010\", \"message\":\"Invalid token\"}";

        // Call the method and expect TokenException
        new SmartAPIResponseHandler().handle(response, body);
    }
    @Test(expected = GeneralException.class)
    public void testHandleMethod_GeneralException() throws IOException, SmartAPIException, JSONException {
        // Create mock Response object
        Response response = Mockito.mock(Response.class);
        Mockito.when(response.header("Content-Type")).thenReturn("application/json");

        // Create sample JSON body with error code
        String body = "{\"errorcode\":\"AB2000\", \"message\":\"General exception occurred\"}";

        // Call the method and expect GeneralException
        new SmartAPIResponseHandler().handle(response, body);
    }
    @Test(expected = PermissionException.class)
    public void testHandleMethod_PermissionException() throws IOException, SmartAPIException, JSONException {
        // Create mock Response object
        Response response = Mockito.mock(Response.class);
        Mockito.when(response.header("Content-Type")).thenReturn("application/json");

        // Create sample JSON body with error code
        String body = "{\"errorcode\":\"AB1011\", \"message\":\"Permission Exception occurred\"}";

        // Call the method and expect PermissionException
        new SmartAPIResponseHandler().handle(response, body);
    }

    @Test(expected = SmartAPIException.class)
    public void testHandleMethod_SmartAPIException() throws IOException, SmartAPIException, JSONException {
        // Create mock Response object
        Response response = Mockito.mock(Response.class);
        Mockito.when(response.header("Content-Type")).thenReturn("application/json");

        // Create sample JSON body with error code
        String body = "{\"errorcode\":\"SmartAPIException\", \"data not found\":\"SmartAPI exception occurred\"}";

        // Call the method and expect SmartAPIException
        new SmartAPIResponseHandler().handle(response, body);
    }

    @Test(expected = InputException.class)
    public void testHandleMethod_InputException() throws IOException, SmartAPIException, JSONException {
        // Create mock Response object
        Response response = Mockito.mock(Response.class);
        Mockito.when(response.header("Content-Type")).thenReturn("application/json");

        // Create sample JSON body with error code
        String body = "{\"errorcode\":\"AB1002\", \"message\":\"Input exception occurred\"}";

        // Call the method and expect InputException
        new SmartAPIResponseHandler().handle(response, body);
    }

    @Test(expected = OrderException.class)
    public void testHandleMethod_OrderException() throws IOException, SmartAPIException, JSONException {
        // Create mock Response object
        Response response = Mockito.mock(Response.class);
        Mockito.when(response.header("Content-Type")).thenReturn("application/json");

        // Create sample JSON body with error code
        String body = "{\"errorcode\":\"AB1017\", \"message\":\"Order exception occurred\"}";

        // Call the method and expect OrderException
        new SmartAPIResponseHandler().handle(response, body);
    }
    @Test(expected = NetworkException.class)
    public void testHandleMethod_NetworkException() throws IOException, SmartAPIException, JSONException {
        // Create mock Response object
        Response response = Mockito.mock(Response.class);
        Mockito.when(response.header("Content-Type")).thenReturn("application/json");

        // Create sample JSON body with error code
        String body = "{\"errorCode\":\"NetworkException\", \"message\":\"Network exception occurred\"}";

        // Call the method and expect NetworkException
        new SmartAPIResponseHandler().handle(response, body);
    }


}
