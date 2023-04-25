package com.angelbroking.smartapi.sample;

import com.angelbroking.smartapi.SmartConnect;
import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import com.angelbroking.smartapi.models.User;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.Proxy;

import static com.angelbroking.smartapi.utils.Constants.TIME_OUT_IN_MILLIS;

@Slf4j
public class LoginWithTOTPSample {

	public static void main(String[] args) throws SmartAPIException, IOException {
		String clientID = System.getProperty("clientID");
		String clientPass = System.getProperty("clientPass");
		String apiKey = System.getProperty("apiKey");
		String totp = System.getProperty("totp");
		Proxy proxy = Proxy.NO_PROXY;
		SmartConnect smartConnect = new SmartConnect(apiKey,proxy,TIME_OUT_IN_MILLIS);
		User user = smartConnect.generateSession(clientID, clientPass, totp);
		String feedToken = user.getFeedToken();
		log.info(feedToken);
	}
}
