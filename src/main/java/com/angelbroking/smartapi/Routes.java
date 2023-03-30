package com.angelbroking.smartapi;

import java.util.HashMap;
import java.util.Map;

import com.angelbroking.smartapi.utils.Constants;

/**
 * Generates end-points for all smart api calls.
 * <p>
 * Here all the routes are translated into a Java Map.
 */

public class Routes {

	public Map<String, String> routes;

	// Initialize all routes,
	@SuppressWarnings("serial")
	public Routes() {
		routes = new HashMap<String, String>() {
			{
				put("api.token", "/rest/auth/angelbroking/jwt/v1/generateTokens");
				put("api.user.profile", "/rest/secure/angelbroking/user/v1/getProfile");
				put("api.refresh", "/rest/auth/angelbroking/jwt/v1/generateTokens");
				put("api.user.logout", "/rest/secure/angelbroking/user/v1/logout");
				put("api.order.place", "/rest/secure/angelbroking/order/v1/placeOrder");
				put("api.order.modify", "/rest/secure/angelbroking/order/v1/modifyOrder");
				put("api.order.cancel", "/rest/secure/angelbroking/order/v1/cancelOrder");
				put("api.order.book", "/rest/secure/angelbroking/order/v1/getOrderBook");
				put("api.order.trade.book", "/rest/secure/angelbroking/order/v1/getTradeBook");
				put("api.order.rms.data", "/rest/secure/angelbroking/user/v1/getRMS");
				put("api.order.rms.holding", "/rest/secure/angelbroking/portfolio/v1/getHolding");
				put("api.order.rms.position", "/rest/secure/angelbroking/order/v1/getPosition");
				put("api.order.rms.position.convert", "/rest/secure/angelbroking/order/v1/convertPosition");
				put("api.ltp.data", "/rest/secure/angelbroking/order/v1/getLtpData");
				put("api.gtt.create", "/gtt-service/rest/secure/angelbroking/gtt/v1/createRule");
				put("api.gtt.modify", "/gtt-service/rest/secure/angelbroking/gtt/v1/modifyRule");
				put("api.gtt.cancel", "/gtt-service/rest/secure/angelbroking/gtt/v1/cancelRule");
				put("api.gtt.details", "/rest/secure/angelbroking/gtt/v1/ruleDetails");
				put("api.gtt.list", "/rest/secure/angelbroking/gtt/v1/ruleList");
				put("api.candle.data", "/rest/secure/angelbroking/historical/v1/getCandleData");

			}
		};
	}

	public String get(String key) {
		return Constants.ROOT_URL + routes.get(key);
	}

	public String getLoginUrl() {
		return Constants.LOGIN_URL;
	}

	public String getWsuri() {
		return Constants.WSURI;
	}

	public String getSWsuri() {
		return Constants.SWSURI;
	}

	public String getSmartStreamWSURI() {
		return Constants.SMARTSTREAM_WSURI;
	}
}
