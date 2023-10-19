	//package com.angelbroking.smartapi.sample;
	//
	//import com.angelbroking.smartapi.SmartConnect;
	//import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
	//import com.angelbroking.smartapi.models.User;
	//
	//public class Test {
	//
	//	public static void main(String[] args) throws SmartAPIException {
	//		try {
	//
	//			SmartConnect smartConnect = new SmartConnect("<api_key>"); // PROVIDE YOUR API KEY HERE
	//
	//			/*
	//			 * OPTIONAL - ACCESS_TOKEN AND REFRESH TOKEN SmartConnect smartConnect = new
	//			 * SmartConnect("<api_key>", "<YOUR_ACCESS_TOKEN>", "<YOUR_REFRESH_TOKEN>");
	//			 */
	//
	//			/*
	//			 * Set session expiry callback. smartConnect.setSessionExpiryHook(new
	//			 * SessionExpiryHook() {
	//			 *
	//			 * @Override public void sessionExpired() {
	//			 * System.out.println("session expired"); } });
	//			 *
	//			 * User user = smartConnect.generateSession("<clientId>", "<password>");
	//			 * smartConnect.setAccessToken(user.getAccessToken());
	//			 * smartConnect.setUserId(user.getUserId());
	//			 *
	//			 * /* token re-generate
	//			 */
	//			/*
	//			 * TokenSet tokenSet = smartConnect.renewAccessToken(user.getAccessToken(),
	//			 * user.getRefreshToken());
	//			 * smartConnect.setAccessToken(tokenSet.getAccessToken());
	//			 */
	//
	//			Examples examples = new Examples();
	//			/* System.out.println("getProfile"); */
	//			examples.getProfile(smartConnect);
	//
	//			/* System.out.println("placeOrder"); */
	//			examples.placeOrder(smartConnect);
	//
	//			/* System.out.println("modifyOrder"); */
	//			examples.modifyOrder(smartConnect);
	//
	//			/* System.out.println("cancelOrder"); */
	//			examples.cancelOrder(smartConnect);
	//
	//			/* System.out.println("getOrder"); */
	//			examples.getOrder(smartConnect);
	//
	//			/* System.out.println("getLTP"); */
	//			examples.getLTP(smartConnect);
	//
	//			/* System.out.println("getTrades"); */
	//			examples.getTrades(smartConnect);
	//
	//			/* System.out.println("getRMS"); */
	//			examples.getRMS(smartConnect);
	//
	//			/* System.out.println("getHolding"); */
	//			examples.getHolding(smartConnect);
	//
	//			/* System.out.println("getPosition"); */
	//			examples.getPosition(smartConnect);
	//
	//			/* System.out.println("convertPosition"); */
	//			examples.convertPosition(smartConnect);
	//
	//			/* System.out.println("createRule"); */
	//			examples.createRule(smartConnect);
	//
	//			/* System.out.println("ModifyRule"); */
	//			examples.modifyRule(smartConnect);
	//
	//			/* System.out.println("cancelRule"); */
	//			examples.cancelRule(smartConnect);
	//
	//			/* System.out.println("Rule Details"); */
	//			examples.ruleDetails(smartConnect);
	//
	//			/* System.out.println("Rule List"); */
	//			examples.ruleList(smartConnect);
	//
	//			/* System.out.println("Historic candle Data"); */
	//			examples.getCandleData(smartConnect);
	//
	//
	//			/* System.out.println("Search script api"); */
	//			examples.getSearchScrip(smartConnect);
	//
	//			/* System.out.println("Market Data"); */
	//			examples.getMarketData(smartConnect);
	//
	//
	//			/* System.out.println("logout"); */
	//			examples.logout(smartConnect);
	//
	//			/* SmartAPITicker */
	//			String clientId = "<clientId>";
	//			User user = smartConnect.generateSession("<clientId>", "<password>", "<totp>");
	//			String feedToken = user.getFeedToken();
	//			String strWatchListScript = "nse_cm|2885&nse_cm|1594&nse_cm|11536&mcx_fo|221658";
	//			String task = "mw";
	//
	//			examples.tickerUsage(clientId, feedToken, strWatchListScript, task);
	//
	//			/*
	//			 * String jwtToken = user.getAccessToken(); String apiKey = "smartapi_key";
	//			 * String actionType = "subscribe"; String feedType = "order_feed";
	//			 *
	//			 * examples.smartWebSocketUsage(clientId, jwtToken, apiKey, actionType,
	//			 * feedType);
	//			 *
	//			 */
	//
	//		} catch (Exception e) {
	//			System.out.println("Exception: " + e.getMessage());
	//			e.printStackTrace();
	//		}
	//
	//	}
	//}