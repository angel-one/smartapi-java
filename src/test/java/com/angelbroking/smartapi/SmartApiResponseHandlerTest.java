package com.angelbroking.smartapi;

import com.angelbroking.smartapi.http.SmartAPIResponseHandler;
import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import okhttp3.Response;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.IOException;


import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SmartApiResponseHandlerTest {



    @Test
    public void test_returns_jsonObject_when_response_header_contains_json_and_jsonObject_has_status_or_success_fields() {
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


}
