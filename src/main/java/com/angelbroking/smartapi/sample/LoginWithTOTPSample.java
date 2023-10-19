//package com.angelbroking.smartapi.sample;
//
//import com.angelbroking.smartapi.SmartConnect;
//import com.angelbroking.smartapi.models.User;
//
//public class LoginWithTOTPSample {
//
//	public static void main(String[] args) {
//		String clientID = System.getProperty("clientID");
//		String clientPass = System.getProperty("clientPass");
//		String apiKey = System.getProperty("apiKey");
//		String totp = System.getProperty("totp");
//		SmartConnect smartConnect = new SmartConnect(apiKey);
//		User user = smartConnect.generateSession(clientID, clientPass, totp);
//		String feedToken = user.getFeedToken();
//		System.out.println(feedToken);
//	}
//}
