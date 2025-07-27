package com.angelbroking.smartapi;

import com.angelbroking.smartapi.http.SessionExpiryHook;
import com.angelbroking.smartapi.http.SmartAPIRequestHandler;
import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import com.angelbroking.smartapi.models.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



import java.io.IOException;
import java.net.URI;
import java.net.Proxy;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;
import javax.net.ssl.HttpsURLConnection;

import static com.angelbroking.smartapi.utils.Constants.IO_EXCEPTION_ERROR_MSG;
import static com.angelbroking.smartapi.utils.Constants.IO_EXCEPTION_OCCURRED;
import static com.angelbroking.smartapi.utils.Constants.JSON_EXCEPTION_ERROR_MSG;
import static com.angelbroking.smartapi.utils.Constants.JSON_EXCEPTION_OCCURRED;
import static com.angelbroking.smartapi.utils.Constants.SMART_API_EXCEPTION_ERROR_MSG;
import static com.angelbroking.smartapi.utils.Constants.SMART_API_EXCEPTION_OCCURRED;

public class SmartConnect {
	
	private static final Logger log = LoggerFactory.getLogger(SmartConnect.class);
	
	public static SessionExpiryHook sessionExpiryHook = null;
	public static boolean ENABLE_LOGGING = false;
	private Proxy proxy = null;
	private String apiKey;
	private String accessToken;
	private String refreshToken;
	private Routes routes = new Routes();
	private String feedToken; // Added for WebSocket
	private String userId;
	private SmartAPIRequestHandler smartAPIRequestHandler = new SmartAPIRequestHandler(proxy);

	// WebSocket related fields
	private HttpClient wsHttpClient;
	private WebSocket webSocketClient;
	private OnStreamListener streamListener;

	public interface OnStreamListener {
		void onConnected();
		void onDisconnected(int statusCode, String reason);
		void onError(Throwable error);
		void onData(ByteBuffer data); // Listener to handle raw binary data
	}

	public SmartConnect() {
		//setting up TLS min and max version
		System.setProperty("https.protocols","TLSv1.2,TLSv1.3");
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
	 * @param publicToken is the public token received after sending request token
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
	 * @param requestToken received from login process.
	 * @param apiSecret    which is unique for each aap.
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
			log.info("login result: {}",loginResultObject);

			if (loginResultObject != null && loginResultObject.optBoolean("status", false) && loginResultObject.has("data") && !loginResultObject.isNull("data")) {
				JSONObject dataObject = loginResultObject.getJSONObject("data");
				String jwtToken = dataObject.getString("jwtToken");
				String refreshToken = dataObject.getString("refreshToken");
				String feedToken = dataObject.getString("feedToken");
				this.feedToken = feedToken; // Store feedToken for WebSocket
				
				String url = routes.get("api.user.profile");
				User user = new User().parseResponse(smartAPIRequestHandler.getRequest(this.apiKey, url, jwtToken));
				user.setAccessToken(jwtToken);
				// Store tokens and user ID in the SmartConnect instance
				this.setAccessToken(jwtToken);
				this.setRefreshToken(refreshToken);
				this.setUserId(user.getUserId()); 

				user.setRefreshToken(refreshToken);
				user.setFeedToken(feedToken);
				return user;
			} else {
				log.error("Login failed or data object is missing/null in response: {}", loginResultObject != null ? loginResultObject.toString() : "Null response");
				return null; // Indicate login failure
			}
		} catch (Exception | SmartAPIException e) {
			log.error(e.getMessage());
			return null;
		}

	}

	/**
	 * Get a new access token using refresh token.
	 *
	 * @param refreshToken is the refresh token obtained after generateSession.
	 * @param apiSecret    is unique for each app.
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

			String newAccessToken = response.getJSONObject("data").getString("jwtToken");
			String newRefreshToken = response.getJSONObject("data").getString("refreshToken");

			// Update instance tokens
			this.setAccessToken(newAccessToken);
			this.setRefreshToken(newRefreshToken);

			TokenSet tokenSet = new TokenSet();
			tokenSet.setUserId(userId);
			tokenSet.setAccessToken(newAccessToken);
			tokenSet.setRefreshToken(newRefreshToken);

			return tokenSet;
		} catch (Exception | SmartAPIException e) {
			log.error(e.getMessage());
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
			log.error(e.getMessage());
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
			if (orderParams.tradingsymbol != null)
				params.put("tradingsymbol", orderParams.tradingsymbol);
			if (orderParams.transactiontype != null)
				params.put("transactiontype", orderParams.transactiontype);
			if (orderParams.quantity != null)
				params.put("quantity", orderParams.quantity);
			if (orderParams.price != null)
				params.put("price", orderParams.price);
			if (orderParams.producttype != null)
				params.put("producttype", orderParams.producttype);
			if (orderParams.ordertype != null)
				params.put("ordertype", orderParams.ordertype);
			if (orderParams.duration != null)
				params.put("duration", orderParams.duration);
			if (orderParams.price != null)
				params.put("price", orderParams.price);
			if (orderParams.symboltoken != null)
				params.put("symboltoken", orderParams.symboltoken);
			if (orderParams.squareoff != null)
				params.put("squareoff", orderParams.squareoff);
			if (orderParams.stoploss != null)
				params.put("stoploss", orderParams.stoploss);
			if (orderParams.triggerprice != null)
				params.put("triggerprice", orderParams.triggerprice);

			params.put("variety", variety);

			JSONObject jsonObject = smartAPIRequestHandler.postRequest(this.apiKey, url, params, accessToken);
			Order order = new Order();
			order.orderId = jsonObject.getJSONObject("data").getString("orderid");
			order.uniqueOrderId = jsonObject.getJSONObject("data").getString("uniqueorderid");
			log.info("order : {}",order);
			return order;
		} catch (Exception | SmartAPIException e) {
			log.error(e.getMessage());
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
			if (orderParams.tradingsymbol != null)
				params.put("tradingsymbol", orderParams.tradingsymbol);
			if (orderParams.symboltoken != null)
				params.put("symboltoken", orderParams.symboltoken);
			if (orderParams.quantity != null)
				params.put("quantity", orderParams.quantity);
			if (orderParams.price != null)
				params.put("price", orderParams.price);
			if (orderParams.producttype != null)
				params.put("producttype", orderParams.producttype);
			if (orderParams.ordertype != null)
				params.put("ordertype", orderParams.ordertype);
			if (orderParams.duration != null)
				params.put("duration", orderParams.duration);

			params.put("variety", variety);
			params.put("orderid", orderId);

			JSONObject jsonObject = smartAPIRequestHandler.postRequest(this.apiKey, url, params, accessToken);
			Order order = new Order();
			order.orderId = jsonObject.getJSONObject("data").getString("orderid");
			return order;
		} catch (Exception | SmartAPIException e) {
			log.error(e.getMessage());
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
			Order order = new Order();
			order.orderId = jsonObject.getJSONObject("data").getString("orderid");
			return order;
		} catch (Exception | SmartAPIException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	/**
	 * Returns list of different stages an order has gone through.
	 *
	 * @return List of multiple stages an order has gone through in the system.
	 * @throws SmartAPIException is thrown for all Smart API trade related errors.
	 * @param orderId is the order id which is obtained from orderbook.
	 *
	 */
	@SuppressWarnings({})
	public JSONObject getOrderHistory(String clientId) {
		try {
			String url = routes.get("api.order.book");
			JSONObject response = smartAPIRequestHandler.getRequest(this.apiKey, url, accessToken);
			log.info("Order history : {}",response);
			return response;
		} catch (Exception | SmartAPIException e) {
			log.error("Exception#: {}" , e.getMessage());
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

			return response.getJSONObject("data");
		} catch (Exception | SmartAPIException e) {
			log.error(e.getMessage());
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
			return response;
		} catch (Exception | SmartAPIException e) {
			log.error(e.getMessage());
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
			return response.getJSONObject("data");
		} catch (Exception | SmartAPIException e) {
			log.error(e.getMessage());
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
			return response;
		} catch (Exception | SmartAPIException e) {
			log.error(e.getMessage());
			return null;
		}
	}


	/**
	 * Retrieves All Holdings.
	 *
	 * @return Object of Holding.
	 *
	 */
	public JSONObject getAllHolding() throws SmartAPIException, IOException {
		try {
			String url = routes.get("api.order.rms.AllHolding");
			return smartAPIRequestHandler.getRequest(this.apiKey, url, accessToken);
		} catch (SmartAPIException ex) {
			log.error("{} while getting all holdings {}", SMART_API_EXCEPTION_OCCURRED, ex.toString());
			throw new SmartAPIException(String.format("%s in getting all holdings %s", SMART_API_EXCEPTION_ERROR_MSG, ex));
		} catch (IOException ex) {
			log.error("{} while getting all holdings {}", IO_EXCEPTION_OCCURRED, ex.getMessage());
			throw new IOException(String.format("%s in getting all holdings %s", IO_EXCEPTION_ERROR_MSG, ex.getMessage()));
		} catch (JSONException ex) {
			log.error("{} while getting all holdings {}", JSON_EXCEPTION_OCCURRED, ex.getMessage());
			throw new JSONException(String.format("%s in getting all holdings %s", JSON_EXCEPTION_ERROR_MSG, ex.getMessage()));
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
			return response;
		} catch (Exception | SmartAPIException e) {
			log.error(e.getMessage());
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
			log.error(e.getMessage());
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

			if (gttParams.tradingsymbol != null)
				params.put("tradingsymbol", gttParams.tradingsymbol);
			if (gttParams.symboltoken != null)
				params.put("symboltoken", gttParams.symboltoken);
			if (gttParams.exchange != null)
				params.put("exchange", gttParams.exchange);
			if (gttParams.transactiontype != null)
				params.put("transactiontype", gttParams.transactiontype);
			if (gttParams.producttype != null)
				params.put("producttype", gttParams.producttype);
			if (gttParams.price != null)
				params.put("price", gttParams.price);
			if (gttParams.qty != null)
				params.put("qty", gttParams.qty);
			if (gttParams.triggerprice != null)
				params.put("triggerprice", gttParams.triggerprice);
			if (gttParams.disclosedqty != null)
				params.put("disclosedqty", gttParams.disclosedqty);
			if (gttParams.timeperiod != null)
				params.put("timeperiod", gttParams.timeperiod);

			JSONObject jsonObject = smartAPIRequestHandler.postRequest(this.apiKey, url, params, accessToken);
			Gtt gtt = new Gtt();
			gtt.id = jsonObject.getJSONObject("data").getInt("id");
			log.info("gtt : {}",gtt);
			return gtt;
		} catch (Exception | SmartAPIException e) {
			log.error(e.getMessage());
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

			if (gttParams.symboltoken != null)
				params.put("symboltoken", gttParams.symboltoken);
			if (gttParams.exchange != null)
				params.put("exchange", gttParams.exchange);
			if (gttParams.price != null)
				params.put("price", gttParams.price);
			if (gttParams.qty != null)
				params.put("qty", gttParams.qty);
			if (gttParams.triggerprice != null)
				params.put("triggerprice", gttParams.triggerprice);
			if (gttParams.disclosedqty != null)
				params.put("disclosedqty", gttParams.disclosedqty);
			if (gttParams.timeperiod != null)
				params.put("timeperiod", gttParams.timeperiod);

			params.put("id", id);

			JSONObject jsonObject = smartAPIRequestHandler.postRequest(this.apiKey, url, params, accessToken);
			Gtt gtt = new Gtt();
			gtt.id = jsonObject.getJSONObject("data").getInt("id");
			log.info("gtt : {}",gtt);
			return gtt;
		} catch (Exception | SmartAPIException e) {
			log.error(e.getMessage());
			return null;
		}

	}

	/**
	 * Cancel a Gtt Rule.
	 *
	 * @param gttParams is gtt Params.
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
			log.info("gtt : {}",gtt);
			return gtt;
		} catch (Exception | SmartAPIException e) {
			log.error(e.getMessage());
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
			log.info("response : {}",response);

			return response.getJSONObject("data");
		} catch (Exception | SmartAPIException e) {
			log.error(e.getMessage());
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
			log.info("response : {}",response);
			return response.getJSONArray("data");
		} catch (Exception | SmartAPIException e) {
			log.error(e.getMessage());
			return null;
		}

	}

	/**
	 * Get Historic Data.
	 *
	 * @param params is historic data params.
	 * @return returns the details of historic data.
	 */
	public JSONArray candleData(JSONObject params) throws SmartAPIException, IOException, JSONException {
		try {
			String url = routes.get("api.candle.data");
			JSONObject response = smartAPIRequestHandler.postRequest(this.apiKey, url, params, accessToken);
			//log.info("Candle data API response : {}",response);//
			if (response != null && response.optBoolean("status", false) && response.has("data") && !response.isNull("data")) {
				return response.getJSONArray("data");
			} else {
				String message = response != null ? response.optString("message", "Unknown error from API") : "Null response from API";
				String errorCode = response != null ? response.optString("errorcode", "N/A") : "N/A";
				log.error("Error fetching candle data from API: {}", message);
				throw new SmartAPIException(message, errorCode); // Throw exception with API error details
			}
		} catch (SmartAPIException e) {
			log.error("SmartAPIException in candleData: {}", e.getMessage());
			throw e;
		} catch (IOException | JSONException e) { // Catch and re-throw IO and JSON exceptions
			log.error("Exception in candleData: {}", e.getMessage());
			throw e;
		}
	}

	public JSONArray oiData(JSONObject params) {
		try {
			String url = routes.get("api.oi.data");
			JSONObject response = smartAPIRequestHandler.postRequest(this.apiKey, url, params, accessToken);
			log.info("response : {}",response);
			return response.getJSONArray("data");
		} catch (Exception | SmartAPIException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	/**

	 * Get Search Script Data.
	 *
	 * @param payload is Search Script params.
	 * @return returns the details of Search Script data.
	 */

	public String getSearchScrip(JSONObject payload) throws SmartAPIException, IOException {
		try {
			String url = routes.get("api.search.script.data");
			return smartAPIRequestHandler.postRequestJSONObject(this.apiKey, url, payload, accessToken);
		}catch (IOException ex) {
			log.error("{} while generating session {}", IO_EXCEPTION_OCCURRED, ex.getMessage());
			throw new IOException(String.format("%s in generating Session  %s", IO_EXCEPTION_ERROR_MSG, ex.getMessage()));
		} catch (JSONException ex) {
			log.error("{} while generating session {}", JSON_EXCEPTION_OCCURRED, ex.getMessage());
			throw new JSONException(String.format("%s in generating Session %s", JSON_EXCEPTION_ERROR_MSG, ex.getMessage()));
		} catch (SmartAPIException ex) {
			log.error("{} while generating session {}", SMART_API_EXCEPTION_OCCURRED, ex.toString());
			throw new SmartAPIException(String.format("%s in generating Session %s", SMART_API_EXCEPTION_ERROR_MSG, ex));
    }
  }

/**
	 * Get Market Data.
	 *
	 * @param params is market data params.
	 * @return returns the details of market data.
	 */
	public JSONObject marketData(JSONObject params) throws SmartAPIException, IOException {
		try{
			String url = routes.get("api.market.data");
			JSONObject response = smartAPIRequestHandler.postRequest(this.apiKey, url, params, accessToken);
			return response.getJSONObject("data");
		}catch (SmartAPIException ex) {
			log.error("{} while placing order {}", SMART_API_EXCEPTION_OCCURRED, ex.toString());
			throw new SmartAPIException(String.format("%s in placing order %s", SMART_API_EXCEPTION_ERROR_MSG, ex));
		} catch (IOException ex) {
			log.error("{} while placing order {}", IO_EXCEPTION_OCCURRED, ex.getMessage());
			throw new IOException(String.format("%s in placing order %s", IO_EXCEPTION_ERROR_MSG, ex.getMessage()));
		} catch (JSONException ex) {
			log.error("{} while placing order {}", JSON_EXCEPTION_OCCURRED, ex.getMessage());
			throw new JSONException(String.format("%s in placing order %s", JSON_EXCEPTION_ERROR_MSG, ex.getMessage()));

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
			// Clear local session details upon successful logout from API
			if (response != null && response.optBoolean("status", false)) {
				this.accessToken = null;
				this.refreshToken = null;
				// this.userId = null; // Optional: clear userId or keep for re-login
			}
			return response;
		} catch (Exception | SmartAPIException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	/**
	 * Get Margin Data.
	 *
	 * @param marginParams is margin data params.
	 * @return returns the response of margin data.
	 */
	public JSONObject getMarginDetails(List<MarginParams> marginParams) throws IOException, SmartAPIException {
		try {
			JSONArray positionsArray = new JSONArray();

			for (MarginParams params : marginParams) {
				JSONObject position = new JSONObject();
				position.put("exchange", params.exchange);
				position.put("qty", params.quantity);
				position.put("price", params.price);
				position.put("productType", params.productType);
				position.put("token", params.token);
				position.put("tradeType", params.tradeType);
				positionsArray.put(position);
			}

			JSONObject requestBody = new JSONObject();
			requestBody.put("positions", positionsArray);

			String url = routes.get("api.margin.batch");
			JSONObject response = smartAPIRequestHandler.postRequest(this.apiKey, url, requestBody, accessToken);
			return response;
		} catch (SmartAPIException ex) {
			log.error("{} while fetching margin data {}", SMART_API_EXCEPTION_OCCURRED, ex.toString());
			throw new SmartAPIException(String.format("%s  while fetching margin data %s", SMART_API_EXCEPTION_ERROR_MSG, ex));
		} catch (IOException ex) {
			log.error("{}  while fetching margin data {}", IO_EXCEPTION_OCCURRED, ex.getMessage());
			throw new IOException(String.format("%s  while fetching margin data %s", IO_EXCEPTION_ERROR_MSG, ex.getMessage()));
		} catch (JSONException ex) {
			log.error("{}  while fetching margin data {}", JSON_EXCEPTION_OCCURRED, ex.getMessage());
			throw new JSONException(String.format("%s  while fetching margin data %s", JSON_EXCEPTION_ERROR_MSG, ex.getMessage()));

		}
	}

	/** Get Individual Order Details
	 *
	 * @return JSONObject which contains order details from Smart API
	 *
	 */
	public JSONObject getIndividualOrderDetails(String orderId) throws IOException, SmartAPIException {
		try {
			String url = routes.get("api.individual.order").concat(orderId);
			return smartAPIRequestHandler.getRequest(this.apiKey, url, accessToken);
		} catch (SmartAPIException ex) {
			log.error("{} while getting individual order {}", SMART_API_EXCEPTION_OCCURRED, ex.toString());
			throw new SmartAPIException(String.format("%s in getting individual order %s", SMART_API_EXCEPTION_ERROR_MSG, ex));
		} catch (IOException ex) {
		   log.error("{} while getting individual order {}", IO_EXCEPTION_OCCURRED, ex.getMessage());
		   throw new IOException(String.format("%s  while fetching margin data %s", IO_EXCEPTION_ERROR_MSG, ex.getMessage()));
	   } catch (JSONException ex) {
		   log.error("{}  while getting individual order {}", JSON_EXCEPTION_OCCURRED, ex.getMessage());
		   throw new JSONException(String.format("%s  while fetching margin data %s", JSON_EXCEPTION_ERROR_MSG, ex.getMessage()));
	   }
	}


	public JSONObject estimateCharges(List<EstimateChargesParams> estimateChargesParams) throws IOException, SmartAPIException {
		try {
			JSONArray ordersArray = new JSONArray();

			for (EstimateChargesParams params : estimateChargesParams) {
				JSONObject order = new JSONObject();
				order.put("product_type", params.product_type);
				order.put("transaction_type", params.transaction_type);
				order.put("quantity", params.quantity);
				order.put("price", params.price);
				order.put("exchange", params.exchange);
				order.put("symbol_name", params.symbol_name);
				order.put("token", params.token);
				ordersArray.put(order);
			}

			JSONObject requestBody = new JSONObject();
			requestBody.put("orders", ordersArray);

			String url = routes.get("api.estimateCharges");
			JSONObject response = smartAPIRequestHandler.postRequest(this.apiKey, url, requestBody, accessToken);
			return response;
		} catch (SmartAPIException ex) {
			log.error("{} while fetching estimateCharges data {}", SMART_API_EXCEPTION_OCCURRED, ex.toString());
			throw new SmartAPIException(String.format("%s  while fetching estimateCharges data %s", SMART_API_EXCEPTION_ERROR_MSG, ex));
		} catch (IOException ex) {
			log.error("{}  while fetching estimateCharges data {}", IO_EXCEPTION_OCCURRED, ex.getMessage());
			throw new IOException(String.format("%s  while fetching estimateCharges data %s", IO_EXCEPTION_ERROR_MSG, ex.getMessage()));
		} catch (JSONException ex) {
			log.error("{}  while fetching estimateCharges data {}", JSON_EXCEPTION_OCCURRED, ex.getMessage());
			throw new JSONException(String.format("%s  while fetching estimateCharges data %s", JSON_EXCEPTION_ERROR_MSG, ex.getMessage()));

		}
	}

	public JSONObject verifyDis(JSONObject params) throws SmartAPIException, IOException {
		try{
			String url = routes.get("api.verifyDis");
			JSONObject response = smartAPIRequestHandler.postRequest(this.apiKey, url, params, accessToken);
			return response;
		}catch (SmartAPIException ex) {
			log.error("{} while verifyDis {}", SMART_API_EXCEPTION_OCCURRED, ex.toString());
			throw new SmartAPIException(String.format("%s in verifyDis %s", SMART_API_EXCEPTION_ERROR_MSG, ex));
		} catch (IOException ex) {
			log.error("{} while verifyDis {}", IO_EXCEPTION_OCCURRED, ex.getMessage());
			throw new IOException(String.format("%s in verifyDis %s", IO_EXCEPTION_ERROR_MSG, ex.getMessage()));
		} catch (JSONException ex) {
			log.error("{} while verifyDis {}", JSON_EXCEPTION_OCCURRED, ex.getMessage());
			throw new JSONException(String.format("%s in verifyDis %s", JSON_EXCEPTION_ERROR_MSG, ex.getMessage()));

		}
	}

	public JSONObject generateTPIN(JSONObject params) throws SmartAPIException, IOException {
		try{
			String url = routes.get("api.generateTPIN");
			JSONObject response = smartAPIRequestHandler.postRequest(this.apiKey, url, params, accessToken);
			return response;
		}catch (SmartAPIException ex) {
			log.error("{} while generateTPIN {}", SMART_API_EXCEPTION_OCCURRED, ex.toString());
			throw new SmartAPIException(String.format("%s in generateTPIN %s", SMART_API_EXCEPTION_ERROR_MSG, ex));
		} catch (IOException ex) {
			log.error("{} while generateTPIN {}", IO_EXCEPTION_OCCURRED, ex.getMessage());
			throw new IOException(String.format("%s in generateTPIN %s", IO_EXCEPTION_ERROR_MSG, ex.getMessage()));
		} catch (JSONException ex) {
			log.error("{} while generateTPIN {}", JSON_EXCEPTION_OCCURRED, ex.getMessage());
			throw new JSONException(String.format("%s in generateTPIN %s", JSON_EXCEPTION_ERROR_MSG, ex.getMessage()));

		}
	}

	public JSONObject getTranStatus(JSONObject params) throws SmartAPIException, IOException {
		try{
			String url = routes.get("api.getTranStatus");
			JSONObject response = smartAPIRequestHandler.postRequest(this.apiKey, url, params, accessToken);
			return response;
		}catch (SmartAPIException ex) {
			log.error("{} while getTranStatus {}", SMART_API_EXCEPTION_OCCURRED, ex.toString());
			throw new SmartAPIException(String.format("%s in getTranStatus %s", SMART_API_EXCEPTION_ERROR_MSG, ex));
		} catch (IOException ex) {
			log.error("{} while getTranStatus {}", IO_EXCEPTION_OCCURRED, ex.getMessage());
			throw new IOException(String.format("%s in getTranStatus %s", IO_EXCEPTION_ERROR_MSG, ex.getMessage()));
		} catch (JSONException ex) {
			log.error("{} while getTranStatus {}", JSON_EXCEPTION_OCCURRED, ex.getMessage());
			throw new JSONException(String.format("%s in getTranStatus %s", JSON_EXCEPTION_ERROR_MSG, ex.getMessage()));

		}
	}

	public JSONObject optionGreek(JSONObject params) throws SmartAPIException, IOException {
		try{
			String url = routes.get("api.optionGreek");
			JSONObject response = smartAPIRequestHandler.postRequest(this.apiKey, url, params, accessToken);
			return response;
		}catch (SmartAPIException ex) {
			log.error("{} while optionGreek {}", SMART_API_EXCEPTION_OCCURRED, ex.toString());
			throw new SmartAPIException(String.format("%s in optionGreek %s", SMART_API_EXCEPTION_ERROR_MSG, ex));
		} catch (IOException ex) {
			log.error("{} while optionGreek {}", IO_EXCEPTION_OCCURRED, ex.getMessage());
			throw new IOException(String.format("%s in optionGreek %s", IO_EXCEPTION_ERROR_MSG, ex.getMessage()));
		} catch (JSONException ex) {
			log.error("{} while optionGreek {}", JSON_EXCEPTION_OCCURRED, ex.getMessage());
			throw new JSONException(String.format("%s in optionGreek %s", JSON_EXCEPTION_ERROR_MSG, ex.getMessage()));

		}
	}

	public JSONObject gainersLosers(JSONObject params) throws SmartAPIException, IOException {
		try{
			String url = routes.get("api.gainersLosers");
			JSONObject response = smartAPIRequestHandler.postRequest(this.apiKey, url, params, accessToken);
			return response;
		}catch (SmartAPIException ex) {
			log.error("{} while gainersLosers {}", SMART_API_EXCEPTION_OCCURRED, ex.toString());
			throw new SmartAPIException(String.format("%s in gainersLosers %s", SMART_API_EXCEPTION_ERROR_MSG, ex));
		} catch (IOException ex) {
			log.error("{} while gainersLosers {}", IO_EXCEPTION_OCCURRED, ex.getMessage());
			throw new IOException(String.format("%s in gainersLosers %s", IO_EXCEPTION_ERROR_MSG, ex.getMessage()));
		} catch (JSONException ex) {
			log.error("{} while gainersLosers {}", JSON_EXCEPTION_OCCURRED, ex.getMessage());
			throw new JSONException(String.format("%s in gainersLosers %s", JSON_EXCEPTION_ERROR_MSG, ex.getMessage()));

		}
	}

	public JSONObject putCallRatio() throws IOException, SmartAPIException {
		try {
			String url = routes.get("api.putCallRatio");
			JSONObject response = smartAPIRequestHandler.getRequest(this.apiKey, url, accessToken);
			return response;
		} catch (SmartAPIException ex) {
			log.error("{} while getting putCallRatio {}", SMART_API_EXCEPTION_OCCURRED, ex.toString());
			throw new SmartAPIException(String.format("%s in getting putCallRatio %s", SMART_API_EXCEPTION_ERROR_MSG, ex));
		} catch (IOException ex) {
			log.error("{} while getting putCallRatio {}", IO_EXCEPTION_OCCURRED, ex.getMessage());
			throw new IOException(String.format("%s  while fetching putCallRatio data %s", IO_EXCEPTION_ERROR_MSG, ex.getMessage()));
		} catch (JSONException ex) {
			log.error("{}  while getting putCallRatio {}", JSON_EXCEPTION_OCCURRED, ex.getMessage());
			throw new JSONException(String.format("%s  while fetching putCallRatio data %s", JSON_EXCEPTION_ERROR_MSG, ex.getMessage()));
		}
	}

	public JSONObject nseIntraday() throws IOException, SmartAPIException {
		try {
			String url = routes.get("api.nseIntraday");
			JSONObject response = smartAPIRequestHandler.getRequest(this.apiKey, url, accessToken);
			return response;
		} catch (SmartAPIException ex) {
			log.error("{} while getting nseIntraday {}", SMART_API_EXCEPTION_OCCURRED, ex.toString());
			throw new SmartAPIException(String.format("%s in getting nseIntraday %s", SMART_API_EXCEPTION_ERROR_MSG, ex));
		} catch (IOException ex) {
			log.error("{} while getting nseIntraday {}", IO_EXCEPTION_OCCURRED, ex.getMessage());
			throw new IOException(String.format("%s  while fetching nseIntraday data %s", IO_EXCEPTION_ERROR_MSG, ex.getMessage()));
		} catch (JSONException ex) {
			log.error("{}  while getting nseIntraday {}", JSON_EXCEPTION_OCCURRED, ex.getMessage());
			throw new JSONException(String.format("%s  while fetching nseIntraday data %s", JSON_EXCEPTION_ERROR_MSG, ex.getMessage()));
		}
	}

	public JSONObject bseIntraday() throws IOException, SmartAPIException {
		try {
			String url = routes.get("api.bseIntraday");
			JSONObject response = smartAPIRequestHandler.getRequest(this.apiKey, url, accessToken);
			return response;
		} catch (SmartAPIException ex) {
			log.error("{} while getting bseIntraday {}", SMART_API_EXCEPTION_OCCURRED, ex.toString());
			throw new SmartAPIException(String.format("%s in getting bseIntraday %s", SMART_API_EXCEPTION_ERROR_MSG, ex));
		} catch (IOException ex) {
			log.error("{} while getting bseIntraday {}", IO_EXCEPTION_OCCURRED, ex.getMessage());
			throw new IOException(String.format("%s  while fetching bseIntraday data %s", IO_EXCEPTION_ERROR_MSG, ex.getMessage()));
		} catch (JSONException ex) {
			log.error("{}  while getting bseIntraday {}", JSON_EXCEPTION_OCCURRED, ex.getMessage());
			throw new JSONException(String.format("%s  while fetching bseIntraday data %s", JSON_EXCEPTION_ERROR_MSG, ex.getMessage()));
		}
	}

	public JSONObject oIBuildup(JSONObject params) throws SmartAPIException, IOException {
		try{
			String url = routes.get("api.oIBuildup");
			JSONObject response = smartAPIRequestHandler.postRequest(this.apiKey, url, params, accessToken);
			return response;
		}catch (SmartAPIException ex) {
			log.error("{} while oIBuildup {}", SMART_API_EXCEPTION_OCCURRED, ex.toString());
			throw new SmartAPIException(String.format("%s in oIBuildup %s", SMART_API_EXCEPTION_ERROR_MSG, ex));
		} catch (IOException ex) {
			log.error("{} while oIBuildup {}", IO_EXCEPTION_OCCURRED, ex.getMessage());
			throw new IOException(String.format("%s in oIBuildup %s", IO_EXCEPTION_ERROR_MSG, ex.getMessage()));
		} catch (JSONException ex) {
			log.error("{} while oIBuildup {}", JSON_EXCEPTION_OCCURRED, ex.getMessage());
			throw new JSONException(String.format("%s in oIBuildup %s", JSON_EXCEPTION_ERROR_MSG, ex.getMessage()));

		}
	}

	/**
	 * Connects to the WebSocket stream for live market data.
	 *
	 * @param listener The listener to handle WebSocket events and data.
	 */
	public void connectStream(OnStreamListener listener) {
		if (this.feedToken == null || this.apiKey == null || this.userId == null) {
			String errorMessage = "Feed token, API key, or User ID is not available. Please login first.";
			log.error(errorMessage);
			if (listener != null) {
				listener.onError(new SmartAPIException(errorMessage));
			}
			return;
		}

		this.streamListener = listener;
		try {
			String wsUrl = routes.getWsUrl("ws.connect"); // Use the new method for WebSocket URLs
			if (wsUrl == null) {
				// Fallback or error if not in Routes, for example:
				wsUrl = "wss://smartapisocket.angelone.in/smart-stream"; // Fallback to a likely correct one
				log.warn("WebSocket URL not found in Routes, using default: {}", wsUrl);
			}

			wsHttpClient = HttpClient.newBuilder()
					.proxy(proxy != null ? HttpClient.Builder.NO_PROXY : HttpClient.Builder.NO_PROXY) // Configure proxy if needed
					.build();

			WebSocketClientListener wsListener = new WebSocketClientListener();

			log.info("Connecting to WebSocket: {}", wsUrl);
			wsHttpClient.newWebSocketBuilder()
					// Add authentication headers for the initial handshake
					.header("Authorization", "Bearer " + this.accessToken) // Assuming feedToken is the primary auth for WS, but accessToken might be needed for handshake
					.header("x-api-key", this.apiKey)
					.header("x-client-code", this.userId) // Or "client-code" - check API docs
					.header("x-feed-token", this.feedToken) // Send feedToken as a header as well
					.buildAsync(URI.create(wsUrl), wsListener)
					.thenAccept(ws -> {
						webSocketClient = ws;
						log.info("WebSocket connection initiated.");
						// Authentication will be handled in onOpen
					}).exceptionally(e -> {
						log.error("WebSocket connection failed: {}", e.getMessage(), e);
						if (streamListener != null) {
							streamListener.onError(e);
						}
						return null;
					});

		} catch (Exception e) {
			log.error("Exception while connecting to WebSocket: {}", e.getMessage(), e);
			if (streamListener != null) {
				streamListener.onError(e);
			}
		}
	}

	private class WebSocketClientListener implements WebSocket.Listener {
		private StringBuilder textMessageBuilder = new StringBuilder();

		@Override
		public void onOpen(WebSocket webSocket) {
			log.info("WebSocket connected.");
			webSocket.request(1); // Request the next message
			if (streamListener != null) {
				streamListener.onConnected();
			}
			// Send authentication message
			authenticateStream();
		}

		@Override
		public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
			textMessageBuilder.append(data);
			webSocket.request(1);
			if (last) {
				String message = textMessageBuilder.toString();
				log.info("WebSocket Text Message Received: {}", message);
				// Handle text messages (e.g., responses to auth/subscribe, errors)
				// For AngelBroking, most data ticks are binary.
				textMessageBuilder.setLength(0); // Reset for next message
			}
			return null;
		}

		@Override
		public CompletionStage<?> onBinary(WebSocket webSocket, ByteBuffer data, boolean last) {
			// log.debug("WebSocket Binary Message Received, size: {}", data.remaining());
			webSocket.request(1);
			if (streamListener != null) {
				// Pass the raw ByteBuffer. The listener is responsible for parsing.
				// Note: data ByteBuffer might be reused by the WebSocket client, so copy if needed for async processing.
				ByteBuffer dataCopy = ByteBuffer.allocate(data.remaining());
				dataCopy.put(data);
				dataCopy.flip();
				streamListener.onData(dataCopy);
			}
			return null;
		}

		@Override
		public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
			log.info("WebSocket closed. Status: {}, Reason: {}", statusCode, reason);
			if (streamListener != null) {
				streamListener.onDisconnected(statusCode, reason);
			}
			webSocketClient = null; // Clear the client
			return null;
		}

		@Override
		public void onError(WebSocket webSocket, Throwable error) {
			log.error("WebSocket error: {}", error.getMessage(), error);
			if (streamListener != null) {
				streamListener.onError(error);
			}
			// Attempt to close gracefully if an error occurs and connection is still perceived as open
			if (webSocketClient != null && !webSocketClient.isOutputClosed()) {
				webSocketClient.sendClose(WebSocket.NORMAL_CLOSURE, "Error occurred");
			}
			webSocketClient = null;
		}
	}

	private void authenticateStream() {
		if (webSocketClient != null && !webSocketClient.isInputClosed()) {
			// It's possible the server authenticates solely via handshake headers.
			// If an explicit "authorize" message is still required after connection,
			// keep this. Otherwise, this might be redundant if headers work.
			// For now, let's assume headers are primary and this is secondary or not needed if 401 is handshake issue.
			// JSONObject authPayload = new JSONObject();
			// authPayload.put("action", "authorize"); // Or "auth" or "authenticate" - check docs
			// JSONObject params = new JSONObject();
			// params.put("token", this.feedToken); // Parameter name might be just "token" or "feedToken"
			// // API key and client code might not be needed in this message if sent in headers
			// authPayload.put("params", params);
			// String authMsg = authPayload.toString();
			// log.info("Sending WebSocket Post-Connection Auth Message (if required): {}", authMsg);
			// webSocketClient.sendText(authMsg, true);
			log.info("WebSocket handshake successful, authentication likely handled by headers. If further auth message needed, it would be sent here.");
		}
	}

	public void subscribeStream(String mode, List<String> instrumentTokens) {
		if (webSocketClient != null && !webSocketClient.isInputClosed()) {
			JSONObject subPayload = new JSONObject();
			subPayload.put("action", "subscribe");
			JSONObject params = new JSONObject();
			params.put("mode", mode); // e.g., "MODE_LTP", "MODE_QUOTE", "MODE_FULL"
			params.put("tokens", new JSONArray(instrumentTokens));
			subPayload.put("params", params);
			String subMsg = subPayload.toString();
			log.info("Sending WebSocket Subscription: {}", subMsg);
			webSocketClient.sendText(subMsg, true);
		}
	}

	public void unsubscribeStream(String mode, List<String> instrumentTokens) {
		if (webSocketClient != null && !webSocketClient.isInputClosed()) {
			JSONObject unsubPayload = new JSONObject();
			unsubPayload.put("action", "unsubscribe"); // Changed action to "unsubscribe"
			JSONObject params = new JSONObject();
			// Mode might not be strictly necessary for unsubscribe for some brokers,
			// but including it for consistency or if the API requires it.
			// If not needed, it can be removed from the params.
			params.put("mode", mode); 
			params.put("tokens", new JSONArray(instrumentTokens));
			unsubPayload.put("params", params);
			String unsubMsg = unsubPayload.toString();
			log.info("Sending WebSocket Unsubscription: {}", unsubMsg);
			webSocketClient.sendText(unsubMsg, true);
		}
	}
	public void disconnectStream() {
		if (webSocketClient != null) {
			log.info("Disconnecting WebSocket stream.");
			webSocketClient.sendClose(WebSocket.NORMAL_CLOSURE, "User initiated disconnect");
			webSocketClient = null;
		}
		if (wsHttpClient != null) {
			// HttpClient doesn't have a direct close method for general cleanup.
			// It's designed to be reused. If executor was custom, shut it down.
		}
	}

}
