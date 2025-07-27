package com.angelbroking.smartapi;

import java.util.HashMap;
import java.util.Map;

/**
 * Generates end-points for all smart api calls.
 *
 * Here all the routes are translated into a Java Map.
 *
 */

public class Routes {

	public Map<String, String> routes;
	private static String _rootUrl = "https://apiconnect.angelone.in";
	private static String _loginUrl = _rootUrl+"/rest/auth/angelbroking/user/v1/loginByPassword";
	private static String _wsuri = "wss://wsfeeds.angelbroking.com/NestHtml5Mobile/socket/stream";
	private static String _smartStreamWSURI = "wss://smartapisocket.angelone.in/smart-stream";
	private static String _swsuri = "wss://smartapisocket.angelbroking.com/websocket";
	private static String _orderUpdateUri = "wss://tns.angelone.in/smart-order-update";

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
				put("api.order.rms.AllHolding", "/rest/secure/angelbroking/portfolio/v1/getAllHolding");
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
				put("api.oi.data", "/rest/secure/angelbroking/historical/v1/getOIData");
				put("api.search.script.data", "/rest/secure/angelbroking/order/v1/searchScrip");
				put("api.market.data", "/rest/secure/angelbroking/market/v1/quote");
				put("api.margin.batch", "/rest/secure/angelbroking/margin/v1/batch");
				put("api.individual.order", "/rest/secure/angelbroking/order/v1/details/");
				put("api.estimateCharges", "/rest/secure/angelbroking/brokerage/v1/estimateCharges");
				put("api.verifyDis", "/rest/secure/angelbroking/edis/v1/verifyDis");
				put("api.generateTPIN", "/rest/secure/angelbroking/edis/v1/generateTPIN");
				put("api.getTranStatus", "/rest/secure/angelbroking/edis/v1/getTranStatus");
				put("api.optionGreek", "/rest/secure/angelbroking/marketData/v1/optionGreek");
				put("api.gainersLosers", "/rest/secure/angelbroking/marketData/v1/gainersLosers");
				put("api.putCallRatio", "/rest/secure/angelbroking/marketData/v1/putCallRatio");
				put("api.nseIntraday", "/rest/secure/angelbroking/marketData/v1/nseIntraday");
				put("api.bseIntraday", "/rest/secure/angelbroking/marketData/v1/bseIntraday");
				put("api.oIBuildup", "/rest/secure/angelbroking/marketData/v1/OIBuildup");
				// Add WebSocket connect key, but its value will be fetched differently
			}
		};
	}

	public String get(String key) {
		return _rootUrl + routes.get(key);
	}

	// New method to get WebSocket URLs directly
	public String getWsUrl(String key) {
		if ("ws.connect".equals(key)) {
			return _smartStreamWSURI; // Or _swsuri, or _wsuri depending on which one is current
		}
		// Add other WebSocket URL keys here if needed
		return null;
	}

	public String getLoginUrl() {
		return _loginUrl;
	}

	public String getWsuri() {
		return _wsuri;
	}

	public String getSWsuri() {
		return _swsuri;
	}
	
	public String getSmartStreamWSURI() {
		return _smartStreamWSURI;
	}

	public String getOrderUpdateUri() {
		return _orderUpdateUri;
	}
}
