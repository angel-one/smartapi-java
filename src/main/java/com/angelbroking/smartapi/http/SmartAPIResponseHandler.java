package com.angelbroking.smartapi.http;

import com.angelbroking.smartapi.http.response.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import okhttp3.Response;
import org.json.JSONException;

import java.util.List;
import java.util.Map;

import static com.angelbroking.smartapi.utils.Constants.JSON_EXCEPTION_ERROR_MSG;
import static com.angelbroking.smartapi.utils.Constants.JSON_EXCEPTION_OCCURRED;

/**
 * Response handler for handling all the responses.
 */
@Slf4j
public class SmartAPIResponseHandler {


    /**
     * Parses the response body as a JSON object and returns it.
     *
     * @param response the HTTP response from the server
     * @param body     the body of the response as a string
     * @return the JSON object parsed from the response body
     * @throws JSONException if the response indicates an error
     */
    public HttpResponse handle(Response response, String body) throws JSONException {
        try {
            Headers responseHeaders = response.headers();
            Map<String, List<String>> headersMap = responseHeaders.toMultimap();
            HttpResponse httpResponse = new HttpResponse();
            httpResponse.setStatusCode(response.code());
            httpResponse.setHeaders(headersMap);
            httpResponse.setBody(body);
            return httpResponse;
        } catch (JSONException ex) {
            log.error("{} {}", JSON_EXCEPTION_OCCURRED, ex.getMessage());
            throw new JSONException(String.format("%s: %s", JSON_EXCEPTION_ERROR_MSG, ex.getMessage()));
        }
    }
}
