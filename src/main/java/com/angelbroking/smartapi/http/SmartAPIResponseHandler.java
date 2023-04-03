package com.angelbroking.smartapi.http;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.angelbroking.smartapi.SmartConnect;
import com.angelbroking.smartapi.http.exceptions.DataException;
import com.angelbroking.smartapi.http.exceptions.GeneralException;
import com.angelbroking.smartapi.http.exceptions.InputException;
import com.angelbroking.smartapi.http.exceptions.NetworkException;
import com.angelbroking.smartapi.http.exceptions.OrderException;
import com.angelbroking.smartapi.http.exceptions.PermissionException;
import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import com.angelbroking.smartapi.http.exceptions.TokenException;

import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Response handler for handling all the responses.
 */
public class SmartAPIResponseHandler {

	private static final Logger logger = LoggerFactory.getLogger(SmartConnect.class);

	public JSONObject handle(Response response, String body) throws IOException, SmartAPIException, JSONException {
		logger.info("***************************");
		if (response.header("Content-Type").contains("json")) {
			JSONObject jsonObject = new JSONObject(body);
			
//			if (jsonObject.optString("data") == null || jsonObject.optString("data") == "") {
			if (!jsonObject.has("status") || jsonObject.has("success")) {	
				if (jsonObject.has("errorcode")) {
					throw dealWithException(jsonObject, jsonObject.getString("errorcode"));
				} else if (jsonObject.has("errorCode")) {
					
					throw dealWithException(jsonObject, jsonObject.getString("errorCode"));
				}
			}
			//logger.info(jsonObject);
			return jsonObject;
		} else {
			throw new DataException("Unexpected content type received from server: " + response.header("Content-Type")
					+ " " + response.body().string(), "AG8001");
		}
	}

	private SmartAPIException dealWithException(JSONObject jsonObject, String code) throws JSONException {

		switch (code) {
		// if there is a token exception, generate a signal to logout the user.
		case "AG8003":
		case "AB8050":
		case "AB8051":
		case "AB1010":
			if (SmartConnect.sessionExpiryHook != null) {
				SmartConnect.sessionExpiryHook.sessionExpired();
			}
			return new TokenException(jsonObject.getString("message"), code);

		case "AG8001":
		case "AG8002":
			return new DataException(jsonObject.getString("message"), code);

		case "AB1004":
		case "AB2000":
			return new GeneralException(jsonObject.getString("message"), code);

		case "AB1003":
		case "AB1005":
		case "AB1012":
		case "AB1002":
			return new InputException(jsonObject.getString("message"), code);

		case "AB1008":
		case "AB1009":
		case "AB1013":
		case "AB1014":
		case "AB1015":
		case "AB1016":
		case "AB1017":
			return new OrderException(jsonObject.getString("message"), code);

		case "NetworkException":
			return new NetworkException(jsonObject.getString("message"), code);

		case "AB1000":
		case "AB1001":
		case "AB1011":
			return new PermissionException(jsonObject.getString("message"), code);

		default:
			return new SmartAPIException(jsonObject.getString("data not found"));
		}
	}

}
