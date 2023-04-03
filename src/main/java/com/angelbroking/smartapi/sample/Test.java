package com.angelbroking.smartapi.sample;

import java.lang.invoke.MethodHandles;
import java.util.HashSet;
import java.util.Set;
import com.angelbroking.smartapi.SmartConnect;
import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import com.angelbroking.smartapi.models.TokenSet;
import com.angelbroking.smartapi.models.User;
import com.angelbroking.smartapi.smartstream.SmartStreamListenerImplTest;
import com.angelbroking.smartapi.smartstream.models.ExchangeType;
import com.angelbroking.smartapi.smartstream.models.SmartStreamSubsMode;
import com.angelbroking.smartapi.smartstream.models.TokenID;
import com.angelbroking.smartapi.smartstream.ticker.SmartStreamTicker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Test {
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());
	public static void main(String[] args) throws SmartAPIException {
		try {


			// Initialize SmartAPI
			String apiKey = "<apiKey>"; // PROVIDE YOUR API KEY HERE
			String clientId = "<clientId>"; // PROVIDE YOUR Client ID HERE
			String clientPin = "<clientPin>"; // PROVIDE YOUR Client PIN HERE
			String tOTP = "<tOTP>"; // PROVIDE THE CODE DISPLAYED ON YOUR AUTHENTICATOR APP - https://smartapi.angelbroking.com/enable-totp

			SmartConnect smartConnect = new SmartConnect(apiKey);

			// OPTIONAL - ACCESS_TOKEN AND REFRESH TOKEN
			/*
			 * SmartConnect smartConnect = new martConnect("<api_key>", "<YOUR_ACCESS_TOKEN>", "<YOUR_REFRESH_TOKEN>");
			 */

			// Set session expiry callback.
			/*
			 * smartConnect.setSessionExpiryHook(new SessionExpiryHook() {
			 * @Override
			 * public void sessionExpired() {
			 * logger.info("session expired");
			 * }
			 * });
			 */

			// Generate User Session
			User user = smartConnect.generateSession(clientId, clientPin, tOTP);
			smartConnect.setAccessToken(user.getAccessToken());
			smartConnect.setUserId(user.getUserId());

			// SmartStreamTicker
			String feedToken = user.getFeedToken();
			SmartStreamTicker ticker = new SmartStreamTicker(clientId, feedToken, new SmartStreamListenerImplTest());
			ticker.connect();
			ticker.subscribe(SmartStreamSubsMode.QUOTE, getTokens());
//			Thread.currentThread().join();

			logger.info("DONE");
			// token re-generate
			TokenSet tokenSet = smartConnect.renewAccessToken(user.getAccessToken(),
					user.getRefreshToken());
			smartConnect.setAccessToken(tokenSet.getAccessToken());

			Examples examples = new Examples();
			logger.info("getProfile");
			examples.getProfile(smartConnect);

//			  logger.info("placeOrder");
//			  examples.placeOrder(smartConnect);
//
//			  logger.info("modifyOrder");
//			  examples.modifyOrder(smartConnect);

			logger.info("cancelOrder");
			examples.cancelOrder(smartConnect);

			logger.info("getOrder");
			examples.getOrder(smartConnect);

			logger.info("getLTP");
			examples.getLTP(smartConnect);

			logger.info("getTrades");
			examples.getTrades(smartConnect);

			logger.info("getRMS");
			examples.getRMS(smartConnect);

			logger.info("getHolding");
			examples.getHolding(smartConnect);

			logger.info("getPosition");
			examples.getPosition(smartConnect);

			logger.info("convertPosition");
			examples.convertPosition(smartConnect);

			logger.info("createRule");
			examples.createRule(smartConnect);

			logger.info("ModifyRule");
			examples.modifyRule(smartConnect);

			logger.info("cancelRule");
			examples.cancelRule(smartConnect);

			logger.info("Rule Details");
			examples.ruleDetails(smartConnect);

			logger.info("Rule List");
			examples.ruleList(smartConnect);

			logger.info("Historic candle Data");
			examples.getCandleData(smartConnect);

			logger.info("logout");
			examples.logout(smartConnect);

		} catch (Exception e) {
			logger.info("Exception: " + e.getMessage());
			e.printStackTrace();
		}

	}

	private static Set<TokenID> getTokens() {
		// find out the required token from
		// https://margincalculator.angelbroking.com/OpenAPI_File/files/OpenAPIScripMaster.json
		Set<TokenID> tokenSet = new HashSet<>();
		tokenSet.add(new TokenID(ExchangeType.NSE_CM, "26009")); // NIFTY BANK
		tokenSet.add(new TokenID(ExchangeType.NSE_CM, "1594")); // NSE Infosys
		tokenSet.add(new TokenID(ExchangeType.NCX_FO, "GUARGUM5")); // GUAREX (NCDEX)
		return tokenSet;
	}
}
