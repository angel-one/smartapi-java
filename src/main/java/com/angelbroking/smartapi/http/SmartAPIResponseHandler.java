package com.angelbroking.smartapi.http;

import com.angelbroking.smartapi.SmartConnect;
import com.angelbroking.smartapi.http.exceptions.*;
import com.angelbroking.smartapi.utils.Constants;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static com.angelbroking.smartapi.utils.Constants.MESSAGE;

/**
 * Response handler for handling all the responses.
 */
public class SmartAPIResponseHandler {



    public JSONObject handle(Response response, String body) throws IOException, SmartAPIException, JSONException {
        if (response.header("Content-Type").contains("json")) {
            JSONObject jsonObject = new JSONObject(body);

            if (!jsonObject.has("status") || jsonObject.has("success")) {
                if (jsonObject.has("errorcode")) {
                    throw dealWithException(jsonObject, jsonObject.getString("errorcode"));
                } else if (jsonObject.has("errorCode")) {

                    throw dealWithException(jsonObject, jsonObject.getString("errorCode"));
                }
            }
            return jsonObject;
        } else {
            throw new DataSmartAPIException(String.format("Unexpected content type received from server: %s %s", response.header("Content-Type"), response.body().string()), "AG8001");
        }
    }

    private SmartAPIException dealWithException(JSONObject jsonObject, String code) throws JSONException {

        switch (code) {
            // if there is a token exception, generate a signal to logout the user.
            case "AG8003":
            case "AB8050":
            case "AB8051":
            case "AB1010":
                if (SmartConnect.getSessionExpiryHook() != null) {
                    SmartConnect.getSessionExpiryHook().sessionExpired();
                }
                return new TokenException(jsonObject.getString(MESSAGE), code);

            case "AG8001":
            case "AG8002":
                return new DataSmartAPIException(jsonObject.getString(MESSAGE), code);

            case "AB1004":
            case "AB2000":
                return new GeneralException(jsonObject.getString(MESSAGE), code);

            case "AB1003":
            case "AB1005":
            case "AB1012":
            case "AB1002":
                return new InputException(jsonObject.getString(MESSAGE), code);

            case "AB1008":
            case "AB1009":
            case "AB1013":
            case "AB1014":
            case "AB1015":
            case "AB1016":
            case "AB1017":
                return new OrderException(jsonObject.getString(MESSAGE), code);

            case "NetworkException":
                return new NetworkException(jsonObject.getString(MESSAGE), code);

            case "AB1000":
            case "AB1001":
            case "AB1011":
                return new PermissionException(jsonObject.getString(MESSAGE), code);

            default:
                return new SmartAPIException(jsonObject.getString("data not found"));
        }
    }

}
