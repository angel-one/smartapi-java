package com.angelbroking.smartapi.http;

import com.angelbroking.smartapi.SmartConnect;
import com.angelbroking.smartapi.http.exceptions.DataSmartAPIException;
import com.angelbroking.smartapi.http.exceptions.GeneralException;
import com.angelbroking.smartapi.http.exceptions.InputException;
import com.angelbroking.smartapi.http.exceptions.NetworkException;
import com.angelbroking.smartapi.http.exceptions.OrderException;
import com.angelbroking.smartapi.http.exceptions.PermissionException;
import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import com.angelbroking.smartapi.http.exceptions.TokenException;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static com.angelbroking.smartapi.utils.Constants.CONTENT_TYPE;
import static com.angelbroking.smartapi.utils.Constants.MESSAGE;

/**
 * Response handler for handling all the responses.
 */
public class SmartAPIResponseHandler {



    public JSONObject handle(Response response, String body) throws IOException, SmartAPIException, JSONException {

        if (response.header(CONTENT_TYPE) != null && response.header(CONTENT_TYPE).contains("json")) {
            JSONObject jsonObject = new JSONObject(body);

            if (jsonObject.has("status") || !jsonObject.has("success")) {
                return jsonObject;
            }

            if (jsonObject.has("errorcode")) {
                throw dealWithException(jsonObject, jsonObject.getString("errorcode"));
            } else if (jsonObject.has("errorCode")) {
                throw dealWithException(jsonObject, jsonObject.getString("errorCode"));
            }
            return jsonObject;
        } else {
            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                throw new DataSmartAPIException(String.format("Unexpected content type received from server: %s %s", response.header(CONTENT_TYPE), responseBody.string()), "AG8001");
            } else {
                throw new DataSmartAPIException("Response body is null", "AG8001");
            }
        }
    }

    private SmartAPIException dealWithException(JSONObject jsonObject, String code) throws JSONException {

        switch (code) {
            // if there is a token exception, generate a signal to log out the user.
            case "AB1010":
                if (SmartConnect.getSessionExpiryHook() != null) {
                    SmartConnect.getSessionExpiryHook().sessionExpired();
                }
                return new TokenException(jsonObject.getString(MESSAGE), code);
            case "AG8002":
                return new DataSmartAPIException(jsonObject.getString(MESSAGE), code);
            case "AB2000":
                return new GeneralException(jsonObject.getString(MESSAGE), code);
            case "AB1002":
                return new InputException(jsonObject.getString(MESSAGE), code);
            case "AB1017":
                return new OrderException(jsonObject.getString(MESSAGE), code);
            case "NetworkException":
                return new NetworkException(jsonObject.getString(MESSAGE), code);
            case "AB1011":
                return new PermissionException(jsonObject.getString(MESSAGE), code);
            default:
                return new SmartAPIException(jsonObject.getString("data not found"));
        }
    }

}
