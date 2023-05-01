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
import com.angelbroking.smartapi.http.response.ResponseDTO;
import com.angelbroking.smartapi.models.SmartConnectParams;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import okhttp3.Response;
import org.json.JSONException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
    public HttpResponse handle(Response response, String body) throws IOException, SmartAPIException {
        try {
            ResponseDTO responseDTO = new ObjectMapper().readValue(body, ResponseDTO.class);
            if (responseDTO.getErrorCode() != "" || !responseDTO.getErrorCode().isEmpty()) {
                throw dealWithException(responseDTO.getMessage(), responseDTO.getErrorCode());
            }
            Headers responseHeaders = response.headers();
            Map<String, List<String>> headersMap = responseHeaders.toMultimap();
            HttpResponse httpResponse = new HttpResponse();
            httpResponse.setStatusCode(response.code());
            httpResponse.setHeaders(headersMap);
            httpResponse.setBody(body);
            return httpResponse;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new SmartAPIException(String.format("%s", ex.getMessage()));
        }
    }

    private SmartAPIException dealWithException(String message, String code) throws JSONException {

        switch (code) {
            // if there is a token exception, generate a signal to logout the user.
            case "AB1010":
                if (SmartConnectParams.getSessionExpiryHook() != null) {
                    SmartConnectParams.setSessionExpiryHook(() -> log.info("Session expired"));
                }
                return new TokenException(message, code);

            case "AG8002":
                return new DataException(message, code);

            case "AB2000":
                return new GeneralException(message, code);


            case "AB1002":
                return new InputException(message, code);


            case "AB1017":
                return new OrderException(message, code);

            case "NetworkException":
                return new NetworkException(message, code);

            case "AB1011":
                return new PermissionException(message, code);

            default:
                return new SmartAPIException(message);
        }
    }
}
