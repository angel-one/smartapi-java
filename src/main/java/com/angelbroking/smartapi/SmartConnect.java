package com.angelbroking.smartapi;

import java.io.IOException;
import java.net.Proxy;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.angelbroking.smartapi.http.SessionExpiryHook;
import com.angelbroking.smartapi.http.SmartAPIRequestHandler;
import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import com.angelbroking.smartapi.models.Gtt;
import com.angelbroking.smartapi.models.GttParams;
import com.angelbroking.smartapi.models.Order;
import com.angelbroking.smartapi.models.OrderParams;
import com.angelbroking.smartapi.models.TokenSet;
import com.angelbroking.smartapi.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmartConnect {

	private static final Logger logger = LoggerFactory.getLogger(SmartConnect.class);
	public static SessionExpiryHook sessionExpiryHook = null;
	public static boolean ENABLE_LOGGING = false;
	private Proxy proxy = null;
	private String apiKey;
	private String accessToken;
	private String refreshToken;
	private Routes routes = new Routes();
	private String userId;
	private SmartAPIRequestHandler smartAPIRequestHandler;

	public SmartConnect() {

	}

	public SmartConnect(String apiKey) {
		this.apiKey = apiKey;
	}

	public SmartConnect(String apiKey, String accessToken, String refreshToken) {
		this.apiKey = apiKey;
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	/**
	 * Registers callback for session error.
	 *
	 * @param hook can be set to get callback when session is expired.
	 */
	public void setSessionExpiryHook(SessionExpiryHook hook) {
		sessionExpiryHook = hook;
	}

	/**
	 * Returns apiKey of the App.
	 *
	 * @return String apiKey is returned.
	 * @throws NullPointerException if _apiKey is not found.
	 */
	public String getApiKey() throws NullPointerException {
		if (apiKey != null)
			return apiKey;
		else
			throw new NullPointerException();
	}

	/**
	 * Returns accessToken.
	 *
	 * @return String access_token is returned.
	 * @throws NullPointerException if accessToken is null.
	 */
	public String getAccessToken() throws NullPointerException {
		if (accessToken != null)
			return accessToken;
		else
			throw new NullPointerException();
	}

	/**
	 * Returns userId.
	 *
	 * @return String userId is returned.
	 * @throws NullPointerException if userId is null.
	 */
	public String getUserId() throws NullPointerException {
		if (userId != null) {
			return userId;
		} else {
			throw new NullPointerException();
		}
	}

	/**
	 * Set userId.
	 *
	 * @param id is user_id.
	 */
	public void setUserId(String id) {
		userId = id;
	}

	/**
	 * Returns publicToken.
	 *
	 * @throws NullPointerException if publicToken is null.
	 * @return String public token is returned.
	 */
	public String getPublicToken() throws NullPointerException {
		if (refreshToken != null) {
			return refreshToken;
		} else {
			throw new NullPointerException();
		}
	}

	/**
	 * Set the accessToken received after a successful authentication.
	 *
	 * @param accessToken is the access token received after sending request token
	 *                    and api secret.
	 */
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	/**
	 * Set publicToken.
	 *
	 * @param refreshToken is the public token received after sending request token
	 *                    and api secret.
	 */
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	/**
	 * Retrieves login url
	 *
	 * @return String loginUrl is returned.
	 */
	public String getLoginURL() throws NullPointerException {
		String baseUrl = routes.getLoginUrl();
		return baseUrl;
	}

	/**
	 * Do the token exchange with the `request_token` obtained after the login flow,
	 * and retrieve the `access_token` required for all subsequent requests.
	 *
	 * @param clientCode received from login process.
	 * @return User is the user model which contains user and session details.
	 *
	 */
	public User generateSession(String clientCode, String password, String totp) {
		try {
			smartAPIRequestHandler = new SmartAPIRequestHandler(proxy);

			// Create JSON params object needed to be sent to api.
			JSONObject params = new JSONObject();
			params.put("clientcode", clientCode);
			params.put("password", password);
			params.put("totp", totp);

			JSONObject loginResultObject = smartAPIRequestHandler.postRequest(this.apiKey, routes.getLoginUrl(),
					params);
			logger.info("genrate: "+String.valueOf(loginResultObject));
			String jwtToken = loginResultObject.getJSONObject("data").getString("jwtToken");
			String refreshToken = loginResultObject.getJSONObject("data").getString("refreshToken");
			String feedToken = loginResultObject.getJSONObject("data").getString("feedToken");
			String url = routes.get("api.user.profile");
			User user = new User().parseResponse(smartAPIRequestHandler.getRequest(this.apiKey, url, jwtToken));
			user.setAccessToken(jwtToken);
			user.setRefreshToken(refreshToken);
			user.setFeedToken(feedToken);

			return user;
		} catch (Exception | SmartAPIException e) {
			logger.info(e.getMessage());
			return null;
		}

	}

	/**
	 * Get a new access token using refresh token.
	 *
	 * @param refreshToken is the refresh token obtained after generateSession.
	 * @param accessToken    is unique for each app.
	 * @return TokenSet contains user id, refresh token, api secret.
	 *
	 */
	public TokenSet renewAccessToken(String accessToken, String refreshToken) {
		try {
			String hashableText = this.apiKey + refreshToken + accessToken;
			String sha256hex = sha256Hex(hashableText);

			JSONObject params = new JSONObject();
			params.put("refreshToken", refreshToken);
			params.put("checksum", sha256hex);
			String url = routes.get("api.refresh");
			JSONObject response = smartAPIRequestHandler.postRequest(this.apiKey, url, params, accessToken);
			System.out.println("jsonObject: "+response);
			accessToken = response.getJSONObject("data").getString("jwtToken");
			refreshToken = response.getJSONObject("data").getString("refreshToken");

			TokenSet tokenSet = new TokenSet();
			tokenSet.setUserId(userId);
			tokenSet.setAccessToken(accessToken);
			tokenSet.setRefreshToken(refreshToken);

			return tokenSet;
		} catch (Exception | SmartAPIException e) {
			logger.info(e.getMessage());
			return null;
		}
	}

	/**
	 * Hex encodes sha256 output for android support.
	 *
	 * @return Hex encoded String.
	 * @param str is the String that has to be encrypted.
	 */
	public String sha256Hex(String str) {
		byte[] a = DigestUtils.sha256(str);
		StringBuilder sb = new StringBuilder(a.length * 2);
		for (byte b : a)
			sb.append(String.format("%02x", b));
		return sb.toString();
	}

	/**
	 * Get the profile details of the use.
	 *
	 * @return Profile is a POJO which contains profile related data.
	 *
	 */
	public User getProfile() {
		try {
			String url = routes.get("api.user.profile");
			User user = new User().parseResponse(smartAPIRequestHandler.getRequest(this.apiKey, url, accessToken));
			return user;
		} catch (Exception | SmartAPIException e) {
			logger.info(e.getMessage());
			return null;
		}
	}

	/**
	 * Places an order.
	 *
	 * @param orderParams is Order params.
	 * @param variety     variety="regular". Order variety can be bo, co, amo,
	 *                    regular.
	 * @return Order contains only orderId.
	 *
	 */
	public Order placeOrder(OrderParams orderParams, String variety) {

		try {
			String url = routes.get("api.order.place");

			JSONObject params = new JSONObject();

			if (orderParams.exchange != null)
				params.put("exchange", orderParams.exchange);
			if (orderParams.tradingSymbol != null)
				params.put("tradingsymbol", orderParams.tradingSymbol);
			if (orderParams.transactionType != null)
				params.put("transactiontype", orderParams.transactionType);
			if (orderParams.quantity != null)
				params.put("quantity", orderParams.quantity);
			if (orderParams.price != null)
				params.put("price", orderParams.price);
			if (orderParams.productType != null)
				params.put("producttype", orderParams.productType);
			if (orderParams.orderType != null)
				params.put("ordertype", orderParams.orderType);
			if (orderParams.duration != null)
				params.put("duration", orderParams.duration);
			if (orderParams.price != null)
				params.put("price", orderParams.price);
			if (orderParams.symbolToken != null)
				params.put("symboltoken", orderParams.symbolToken);
			if (orderParams.squareOff != null)
				params.put("squareoff", orderParams.squareOff);
			if (orderParams.stopLoss != null)
				params.put("stoploss", orderParams.stopLoss);
			if (orderParams.triggerPrice != null)
				params.put("triggerprice", orderParams.triggerPrice);

			params.put("variety", variety);

			JSONObject jsonObject = smartAPIRequestHandler.postRequest(this.apiKey, url, params, accessToken);
			System.out.println("jsonObject: "+jsonObject);
			Order order = new Order();
			order.orderId = jsonObject.getJSONObject("data").getString("orderid");
			logger.info(String.valueOf(order));
			return order;
		} catch (Exception | SmartAPIException e) {
			logger.info(e.getMessage());
			return null;
		}
	}

	/**
	 * Modifies an open order.
	 *
	 * @param orderParams is Order params.
	 * @param variety     variety="regular". Order variety can be bo, co, amo,
	 *                    regular.
	 * @param orderId     order id of the order being modified.
	 * @return Order object contains only orderId.
	 *
	 */
	public Order modifyOrder(String orderId, OrderParams orderParams, String variety) {
		try {
			String url = routes.get("api.order.modify");

			JSONObject params = new JSONObject();

			if (orderParams.exchange != null)
				params.put("exchange", orderParams.exchange);
			if (orderParams.tradingSymbol != null)
				params.put("tradingsymbol", orderParams.tradingSymbol);
			if (orderParams.symbolToken != null)
				params.put("symboltoken", orderParams.symbolToken);
			if (orderParams.quantity != null)
				params.put("quantity", orderParams.quantity);
			if (orderParams.price != null)
				params.put("price", orderParams.price);
			if (orderParams.productType != null)
				params.put("producttype", orderParams.productType);
			if (orderParams.orderType != null)
				params.put("ordertype", orderParams.orderType);
			if (orderParams.duration != null)
				params.put("duration", orderParams.duration);

			params.put("variety", variety);
			params.put("orderid", orderId);

			JSONObject jsonObject = smartAPIRequestHandler.postRequest(this.apiKey, url, params, accessToken);
			System.out.println("JsonObject: "+jsonObject);
			Order order = new Order();
			order.orderId = jsonObject.getJSONObject("data").getString("orderid");
			return order;
		} catch (Exception | SmartAPIException e) {
			logger.info(e.getMessage());
			return null;
		}
	}

	/**
	 * Cancels an order.
	 *
	 * @param orderId order id of the order to be cancelled.
	 * @param variety [variety="regular"]. Order variety can be bo, co, amo,
	 *                regular.
	 * @return Order object contains only orderId.
	 *
	 */
	public Order cancelOrder(String orderId, String variety) {
		try {
			String url = routes.get("api.order.cancel");
			JSONObject params = new JSONObject();
			params.put("variety", variety);
			params.put("orderid", orderId);

			JSONObject jsonObject = smartAPIRequestHandler.postRequest(this.apiKey, url, params, accessToken);
			System.out.println("jsonObject: "+jsonObject);

			Order order = new Order();
			order.orderId = jsonObject.getJSONObject("data").getString("orderid");
			return order;
		} catch (Exception | SmartAPIException e) {
			logger.info(e.getMessage());
			return null;
		}
	}

	/**
	 * Returns list of different stages an order has gone through.
	 *
	 * @return List of multiple stages an order has gone through in the system.
	 * @throws SmartAPIException is thrown for all Smart API trade related errors.
	 * @param clientId is the clientId which is obtained from orderbook.
	 *
	 */
	@SuppressWarnings({})
	public JSONObject getOrderHistory(String clientId) {
		try {
			String url = routes.get("api.order.book");
			JSONObject response = smartAPIRequestHandler.getRequest(this.apiKey, url, accessToken);
			logger.info("response: "+String.valueOf(response));
			return response;
		} catch (Exception | SmartAPIException e) {
			logger.info("Exception#: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retrieves last price. User can either pass exchange with tradingsymbol or
	 * instrument token only. For example {NSE:NIFTY 50, BSE:SENSEX} or {256265,
	 * 265}.
	 *
	 * @return Map of String and LTPQuote.
	 *
	 */
	public JSONObject getLTP(String exchange, String tradingSymbol, String symboltoken) {
		try {
			JSONObject params = new JSONObject();
			params.put("exchange", exchange);
			params.put("tradingsymbol", tradingSymbol);
			params.put("symboltoken", symboltoken);

			String url = routes.get("api.ltp.data");
			JSONObject response = smartAPIRequestHandler.postRequest(this.apiKey, url, params, accessToken);
			System.out.println("jsonObject: "+response);
			return response.getJSONObject("data");
		} catch (Exception | SmartAPIException e) {
			logger.info(e.getMessage());
			return null;
		}
	}

	/**
	 * Retrieves list of trades executed.
	 *
	 * @return List of trades.
	 */
	public JSONObject getTrades() {
		try {
			String url = routes.get("api.order.trade.book");
			JSONObject response = smartAPIRequestHandler.getRequest(this.apiKey, url, accessToken);
			System.out.println("jsonObject: "+response);
			return response;
		} catch (Exception | SmartAPIException e) {
			logger.info(e.getMessage());
			return null;
		}
	}

	/**
	 * Retrieves RMS.
	 *
	 * @return Object of RMS.
	 * @throws SmartAPIException is thrown for all Smart API trade related errors.
	 * @throws JSONException     is thrown when there is exception while parsing
	 *                           response.
	 * @throws IOException       is thrown when there is connection error.
	 */
	public JSONObject getRMS() {
		try {
			String url = routes.get("api.order.rms.data");
			JSONObject response = smartAPIRequestHandler.getRequest(this.apiKey, url, accessToken);
			System.out.println("jsonObject: "+response);
			return response.getJSONObject("data");
		} catch (Exception | SmartAPIException e) {
			logger.info(e.getMessage());
			return null;
		}
	}

	/**
	 * Retrieves Holding.
	 *
	 * @return Object of Holding.
	 *
	 */
	public JSONObject getHolding() {
		try {
			String url = routes.get("api.order.rms.holding");
			JSONObject response = smartAPIRequestHandler.getRequest(this.apiKey, url, accessToken);
			System.out.println("jsonObject: "+response);
			return response;
		} catch (Exception | SmartAPIException e) {
			logger.info(e.getMessage());
			return null;
		}
	}

	/**
	 * Retrieves position.
	 *
	 * @return Object of position.
	 *
	 */
	public JSONObject getPosition() {
		try {
			String url = routes.get("api.order.rms.position");
			JSONObject response = smartAPIRequestHandler.getRequest(this.apiKey, url, accessToken);
			System.out.println("jsonObject: "+response);
			return response;
		} catch (Exception | SmartAPIException e) {
			logger.info(e.getMessage());
			return null;
		}
	}

	/**
	 * Retrieves conversion.
	 *
	 * @return Object of conversion.
	 * @throws SmartAPIException is thrown for all Smart API trade related errors.
	 * @throws JSONException     is thrown when there is exception while parsing
	 *                           response.
	 * @throws IOException       is thrown when there is connection error.
	 */
	public JSONObject convertPosition(JSONObject params) {
		try {
			String url = routes.get("api.order.rms.position.convert");
			JSONObject response = smartAPIRequestHandler.postRequest(this.apiKey, url, params, accessToken);
			return response;
		} catch (Exception | SmartAPIException e) {
			logger.info(e.getMessage());
			return null;
		}
	}

	/**
	 * Create a Gtt Rule.
	 *
	 * @param gttParams is gtt Params.
	 * @return Gtt contains only orderId.
	 *
	 */

	public Gtt gttCreateRule(GttParams gttParams) {
		try {
			String url = routes.get("api.gtt.create");

			JSONObject params = new JSONObject();

			if (gttParams.tradingSymbol != null)
				params.put("tradingsymbol", gttParams.tradingSymbol);
			if (gttParams.symbolToken != null)
				params.put("symboltoken", gttParams.symbolToken);
			if (gttParams.exchange != null)
				params.put("exchange", gttParams.exchange);
			if (gttParams.transactionType != null)
				params.put("transactiontype", gttParams.transactionType);
			if (gttParams.productType != null)
				params.put("producttype", gttParams.productType);
			if (gttParams.price != null)
				params.put("price", gttParams.price);
			if (gttParams.qty != null)
				params.put("qty", gttParams.qty);
			if (gttParams.triggerPrice != null)
				params.put("triggerprice", gttParams.triggerPrice);
			if (gttParams.disclosedQty != null)
				params.put("disclosedqty", gttParams.disclosedQty);
			if (gttParams.timePeriod != null)
				params.put("timeperiod", gttParams.timePeriod);

			JSONObject jsonObject = smartAPIRequestHandler.postRequest(this.apiKey, url, params, accessToken);
			Gtt gtt = new Gtt();
			gtt.id = jsonObject.getJSONObject("data").getInt("id");
			logger.info(String.valueOf(gtt));
			return gtt;
		} catch (Exception | SmartAPIException e) {
			logger.info(e.getMessage());
			return null;
		}

	}

	/**
	 * Modify a Gtt Rule.
	 *
	 * @param gttParams is gtt Params.
	 * @return Gtt contains only orderId.
	 *
	 */

	public Gtt gttModifyRule(Integer id, GttParams gttParams) {
		try {
			String url = routes.get("api.gtt.modify");

			JSONObject params = new JSONObject();

			if (gttParams.symbolToken != null)
				params.put("symboltoken", gttParams.symbolToken);
			if (gttParams.exchange != null)
				params.put("exchange", gttParams.exchange);
			if (gttParams.price != null)
				params.put("price", gttParams.price);
			if (gttParams.qty != null)
				params.put("qty", gttParams.qty);
			if (gttParams.triggerPrice != null)
				params.put("triggerprice", gttParams.triggerPrice);
			if (gttParams.disclosedQty != null)
				params.put("disclosedqty", gttParams.disclosedQty);
			if (gttParams.timePeriod != null)
				params.put("timeperiod", gttParams.timePeriod);

			params.put("id", id);

			JSONObject jsonObject = smartAPIRequestHandler.postRequest(this.apiKey, url, params, accessToken);
			Gtt gtt = new Gtt();
			gtt.id = jsonObject.getJSONObject("data").getInt("id");
			logger.info(String.valueOf(gtt));
			return gtt;
		} catch (Exception | SmartAPIException e) {
			logger.info(e.getMessage());
			return null;
		}

	}

	/**
	 * Cancel a Gtt Rule.
	 *
	 * @param exchange,id,symboltoken is gtt Params.
	 * @return Gtt contains only orderId.
	 */

	public Gtt gttCancelRule(Integer id, String symboltoken, String exchange) {
		try {
			JSONObject params = new JSONObject();
			params.put("id", id);
			params.put("symboltoken", symboltoken);
			params.put("exchange", exchange);

			String url = routes.get("api.gtt.cancel");
			JSONObject jsonObject = smartAPIRequestHandler.postRequest(this.apiKey, url, params, accessToken);
			Gtt gtt = new Gtt();
			gtt.id = jsonObject.getJSONObject("data").getInt("id");
			logger.info(String.valueOf(gtt));
			return gtt;
		} catch (Exception | SmartAPIException e) {
			logger.info(e.getMessage());
			return null;
		}
	}

	/**
	 * Get Gtt Rule Details.
	 *
	 * @param id is gtt rule id.
	 * @return returns the details of gtt rule.
	 */

	public JSONObject gttRuleDetails(Integer id) {
		try {

			JSONObject params = new JSONObject();
			params.put("id", id);

			String url = routes.get("api.gtt.details");
			JSONObject response = smartAPIRequestHandler.postRequest(this.apiKey, url, params, accessToken);
			logger.info(String.valueOf(response));

			return response.getJSONObject("data");
		} catch (Exception | SmartAPIException e) {
			logger.info(e.getMessage());
			return null;
		}

	}

	/**
	 * Get Gtt Rule Details.
	 *
	 * @param status is list of gtt rule status.
	 * @param page   is no of page
	 * @param count  is the count of gtt rules
	 * @return returns the detailed list of gtt rules.
	 */
	public JSONArray gttRuleList(List<String> status, Integer page, Integer count) {
		try {
			JSONObject params = new JSONObject();
			params.put("status", status);
			params.put("page", page);
			params.put("count", count);

			String url = routes.get("api.gtt.list");
			JSONObject response = smartAPIRequestHandler.postRequest(this.apiKey, url, params, accessToken);
			logger.info(String.valueOf(response));
			return response.getJSONArray("data");
		} catch (Exception | SmartAPIException e) {
			logger.info(e.getMessage());
			return null;
		}

	}

	/**
	 * Get Historic Data.
	 *
	 * @param params is historic data params.
	 * @return returns the details of historic data.
	 */
	public String candleData(JSONObject params) {
		try {
			String url = routes.get("api.candle.data");
			JSONObject response = smartAPIRequestHandler.postRequest(this.apiKey, url, params, accessToken);
			logger.info(String.valueOf(response));
			return response.getString("data");
		} catch (Exception | SmartAPIException e) {
			logger.info(e.getMessage());
			return null;
		}
	}

	/**
	 * Logs out user by invalidating the access token.
	 *
	 * @return JSONObject which contains status
	 *
	 */

	public JSONObject logout() {
		try {
			String url = routes.get("api.user.logout");
			JSONObject params = new JSONObject();
			params.put("clientcode", this.userId);
			JSONObject response = smartAPIRequestHandler.postRequest(this.apiKey, url, params, accessToken);
			return response;
		} catch (Exception | SmartAPIException e) {
			logger.info(e.getMessage());
			return null;
		}
	}

}
