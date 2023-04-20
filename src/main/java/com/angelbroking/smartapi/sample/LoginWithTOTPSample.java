package com.angelbroking.smartapi.sample;

import com.angelbroking.smartapi.SmartConnect;
import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import com.angelbroking.smartapi.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class LoginWithTOTPSample {
	private static final Logger logger = LoggerFactory.getLogger(LoginWithTOTPSample.class);

	public static void main(String[] args) throws SmartAPIException, IOException {
		String clientID = System.getProperty("clientID");
		String clientPass = System.getProperty("clientPass");
		String apiKey = System.getProperty("apiKey");
		String totp = System.getProperty("totp");
		SmartConnect smartConnect = new SmartConnect(apiKey);
		User user = smartConnect.generateSession(clientID, clientPass, totp);
		String feedToken = user.getFeedToken();
		logger.info(feedToken);
	}
}
