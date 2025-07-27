package com.angelbroking.smartapi.sample;

import com.angelbroking.smartapi.SmartConnect;
import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import com.angelbroking.smartapi.models.User;
// import lombok.extern.slf4j.Slf4j; // Removed Lombok import
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// @Slf4j // Removed Lombok annotation
public class APITest {
	private static final Logger log = LoggerFactory.getLogger(APITest.class);

	public static void main(String[] args) throws SmartAPIException {
		try {

			SmartConnect smartConnect = new SmartConnect("<api_key>"); // PROVIDE YOUR API KEY HERE

			/*
			 * OPTIONAL - ACCESS_TOKEN AND REFRESH TOKEN SmartConnect smartConnect = new
			 * SmartConnect("<api_key>", "<YOUR_ACCESS_TOKEN>", "<YOUR_REFRESH_TOKEN>");
			 */

			/*
			 * Set session expiry callback. smartConnect.setSessionExpiryHook(new
			 * SessionExpiryHook() {
			 *
			 * @Override public void sessionExpired() {
			 * log.info("session expired"); } });
			 *
			 * User user = smartConnect.generateSession("<clientId>", "<password>");
			 * smartConnect.setAccessToken(user.getAccessToken());
			 * smartConnect.setUserId(user.getUserId());
			 *
			 * /* token re-generate
			 */
			/*
			 * TokenSet tokenSet = smartConnect.renewAccessToken(user.getAccessToken(),
			 * user.getRefreshToken());
			 * smartConnect.setAccessToken(tokenSet.getAccessToken());
			 */

			Examples examples = new Examples();
			/* log.info("getProfile"); */
			examples.getProfile(smartConnect);

			/* log.info("placeOrder"); */
			examples.placeOrder(smartConnect);

			/* log.info("modifyOrder"); */
			examples.modifyOrder(smartConnect);

			/* log.info("cancelOrder"); */
			examples.cancelOrder(smartConnect);

			/* log.info("getOrder"); */
			examples.getOrder(smartConnect);

			/* log.info("getLTP"); */
			examples.getLTP(smartConnect);

			/* log.info("getTrades"); */
			examples.getTrades(smartConnect);

			/* log.info("getRMS"); */
			examples.getRMS(smartConnect);

			/* log.info("getHolding"); */
			examples.getHolding(smartConnect);

			/* log.info("getAllHolding"); */
			examples.getAllHolding(smartConnect);

			/* log.info("getPosition"); */
			examples.getPosition(smartConnect);

			/* log.info("convertPosition"); */
			examples.convertPosition(smartConnect);

			/* log.info("createRule"); */
			examples.createRule(smartConnect);

			/* log.info("ModifyRule"); */
			examples.modifyRule(smartConnect);

			/* log.info("cancelRule"); */
			examples.cancelRule(smartConnect);

			/* log.info("Rule Details"); */
			examples.ruleDetails(smartConnect);

			/* log.info("Rule List"); */
			examples.ruleList(smartConnect);

			/* log.info("Historic candle Data"); */
			examples.getCandleData(smartConnect);


			/* log.info("Search script api"); */
			examples.getSearchScrip(smartConnect);

			/* log.info("Market Data"); */
			examples.getMarketData(smartConnect);


			/* log.info("logout"); */
			examples.logout(smartConnect);


			/* log.info("Margin Details"); */
			examples.getMarginDetails(smartConnect);

			/* log.info("Individual Order"); */
			examples.getIndividualOrder(smartConnect, "1000051");

			examples.estimateCharges(smartConnect);

			examples.verifyDis(smartConnect);

			examples.generateTPIN(smartConnect);

			examples.getTranStatus(smartConnect);

			examples.optionGreek(smartConnect);

			examples.gainersLosers(smartConnect);

			examples.putCallRatio(smartConnect);

			examples.oIBuildup(smartConnect);


			/* SmartAPITicker */
			String clientId = "<clientId>";
			User user = smartConnect.generateSession("<clientId>", "<password>", "<totp>");
			String feedToken = user.getFeedToken();
			String strWatchListScript = "nse_cm|2885&nse_cm|1594&nse_cm|11536&mcx_fo|221658";
			String task = "mw";

			examples.tickerUsage(clientId, feedToken, strWatchListScript, task);

			/*
			 * String jwtToken = user.getAccessToken(); String apiKey = "smartapi_key";
			 * String actionType = "subscribe"; String feedType = "order_feed";
			 *
			 * examples.smartWebSocketUsage(clientId, jwtToken, apiKey, actionType,
			 * feedType);
			 *
			 */

			/* Order Websocket */
			String userClientId = "<clientId>";
			User userGenerateSession = smartConnect.generateSession("<clientId>", "<password>", "<totp>");
			smartConnect.setAccessToken(userGenerateSession.getAccessToken());
			smartConnect.setUserId(userGenerateSession.getUserId());
			String accessToken = userGenerateSession.getAccessToken();

			examples.orderUpdateUsage(accessToken);
		} catch (Exception e) {
			log.error("Exception: {}" , e.getMessage());
			e.printStackTrace();
		}

	}
}