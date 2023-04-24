package com.angelbroking.smartapi.sample;

import com.angelbroking.smartapi.SmartConnect;
import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import com.angelbroking.smartapi.models.User;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class LoginWithTOTPSample {

	public static void main(String[] args) throws SmartAPIException, IOException {
		String clientID = System.getProperty("clientID");
		String clientPass = System.getProperty("clientPass");
		String apiKey = System.getProperty("apiKey");
		String totp = System.getProperty("totp");
		SmartConnect smartConnect = new SmartConnect(apiKey);
		User user = smartConnect.generateSession(clientID, clientPass, totp);
		String feedToken = user.getFeedToken();
		log.info(feedToken);
	}
}
