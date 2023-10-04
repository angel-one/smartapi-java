package com.angelbroking.smartapi;

import com.angelbroking.smartapi.http.SmartAPIResponseHandler;
import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import okhttp3.Response;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.assertj.core.api.Fail.fail;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SmartApiResponseHandlerTest {

    @Mock
    SmartAPIResponseHandler smartAPIResponseHandler;

    @Test
    public void test_returns_jsonObject_when_response_header_contains_json_and_jsonObject_has_status_or_success_fields() {
        Response response = mock(Response.class);
        when(response.header("Content-Type")).thenReturn("application/json");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", "success");

        try {
            JSONObject result = smartAPIResponseHandler.handle(response, jsonObject.toString());
            assertEquals(jsonObject, result);
        } catch (Exception e) {
            fail("Exception should not be thrown");
        } catch (SmartAPIException e) {
            throw new RuntimeException(e);
        }
    }
}
