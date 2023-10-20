package com.angelbroking.smartapi.http;

import com.angelbroking.smartapi.http.SmartAPIResponseHandler;
import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import okhttp3.Response;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.IOException;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SmartApiResponseHandlerTest {



    @Test
    public void testSmartApiResponseHandlerResponse() {

        Response response = mock(Response.class);
        when(response.header("Content-Type")).thenReturn("application/json");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", "success");

        try {
            SmartAPIResponseHandler smartAPIResponseHandler = new SmartAPIResponseHandler();
            JSONObject result = smartAPIResponseHandler.handle(response, jsonObject.toString());
            assertNotNull(result);
        } catch (SmartAPIException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    public void testHandlerForOkStatusCode() throws SmartAPIException, IOException {

        // Arrange
        Response response = mock(Response.class);
        when(response.code()).thenReturn(200);
        String body = "{\"status\": true, \"data\": [{\"exchange\": \"NSE\", \"tradingsymbol\": \"INFY\", \"symboltoken\": \"12345\"}]}";

        SmartAPIResponseHandler handler = new SmartAPIResponseHandler();

        // Act
        String result = handler.handler(response, body);

        // Assert
        assertEquals("Search successful. Found 1 trading symbols for the given query:\n1. exchange: NSE, tradingsymbol: INFY, symboltoken: 12345\n", result);
    }

    @Test
    public void testHandlerMethodForBadRequestStatusResponse() throws SmartAPIException, IOException {

        // Arrange
        Response response = mock(Response.class);
        when(response.code()).thenReturn(400);

        SmartAPIResponseHandler handler = new SmartAPIResponseHandler();

        // Act
        String result = handler.handler(response, "");

        // Assert
        assertEquals("Bad request. Please provide valid input", result);
    }

    @Test
    public void testHandlerMethodForIllegalArgumentException() {

        // Arrange
        Response response = mock(Response.class);
        when(response.code()).thenReturn(500);

        SmartAPIResponseHandler handler = new SmartAPIResponseHandler();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            handler.handler(response, "");
        });

    }
}
