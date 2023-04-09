package com.angelbroking.smartapi;

import com.angelbroking.smartapi.http.SessionExpiryHook;
import com.angelbroking.smartapi.http.SmartAPIRequestHandler;
import com.angelbroking.smartapi.http.exceptions.CustomException;
import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import com.angelbroking.smartapi.models.*;
import com.angelbroking.smartapi.utils.Constants;
import com.angelbroking.smartapi.utils.ResponseParser;
import com.angelbroking.smartapi.utils.Validators;
import lombok.Data;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Proxy;
import java.util.List;
import java.util.Optional;

@Data
public class SmartConnect {

    private static final Logger logger = LoggerFactory.getLogger(SmartConnect.class);
    public static SessionExpiryHook sessionExpiryHook = null;
    public static boolean enableLogging = false;
    public static final Routes routes = new Routes();
    private final Proxy proxy = Proxy.NO_PROXY;

    private String apiKey;
    private String accessToken;
    private String refreshToken;
    private String userId;
    public SmartAPIRequestHandler smartAPIRequestHandler;


    public SmartConnect(String apiKey) {
        this.apiKey = apiKey;
    }

    public SmartConnect(String apiKey, String accessToken, String refreshToken) {
        this.apiKey = apiKey;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }


    /**
     * Returns apiKey of the App.
     *
     * @return String apiKey is returned.
     * @throws CustomException if _apiKey is not found.
     */
    public String getApiKey() throws CustomException {
        if (apiKey != null) return apiKey;
        else throw new CustomException("The API key is missing.");
    }


    /**
     * Returns accessToken.
     *
     * @return String access_token is returned.
     * @throws CustomException if accessToken is null.
     */
    public String getAccessToken() throws CustomException {
        if (accessToken != null) return accessToken;
        else throw new CustomException("The Access Token key is missing.");
    }


    /**
     * Returns userId.
     *
     * @return String userId is returned.
     * @throws CustomException if userId is null.
     */
    public String getUserId() {
        return Optional.ofNullable(userId).orElseThrow(() -> new CustomException("The user ID is missing."));

    }


    /**
     * Returns publicToken.
     *
     * @return String public token is returned.
     * @throws CustomException if publicToken is null.
     */
    public String getPublicToken() throws CustomException {
        if (refreshToken != null) {
            return refreshToken;
        } else {
            throw new CustomException("The Public Token key is missing.");
        }
    }


    /**
     * Retrieves login url
     *
     * @return String loginUrl is returned.
     */
    public String getLoginURL() throws CustomException {
        String baseUrl = routes.getLoginUrl();
        if (baseUrl != null) {
            return baseUrl;
        } else {
            throw new CustomException("The Login URL key is missing.");
        }
    }

    /**
     * Do the token exchange with the `request_token` obtained after the login flow,
     * and retrieve the `access_token` required for all subsequent requests.
     *
     * @param clientCode received from login process.
     * @return User is the user model which contains user and session details.
     */
    public User generateSession(String clientCode, String password, String totp) {
        User user;
        JSONObject loginResultObject;
        try {
            smartAPIRequestHandler = new SmartAPIRequestHandler(proxy);

            loginResultObject = smartAPIRequestHandler.postRequest(this.apiKey, routes.getLoginUrl(), createLoginParams(clientCode, password, totp));

        }catch (SmartAPIException ex) {
            logger.error(ex.getMessage());
            return null;
        } catch (IOException ex) {
            logger.error(ex.getMessage());
            return null;
        } catch (JSONException ex) {
            logger.error(ex.getMessage());
            return null;
        }

        logger.info("genrate: " + loginResultObject);
        String jwtToken = loginResultObject.getJSONObject(Constants.SmartConnect_data).getString(Constants.SmartConnect_jwtToken);
        String refreshTokenLocal = loginResultObject.getJSONObject(Constants.SmartConnect_data).getString(Constants.SmartConnect_refreshToken);
        String feedToken = loginResultObject.getJSONObject(Constants.SmartConnect_data).getString(Constants.SmartConnect_feedToken);
        String url = routes.get(Constants.SmartConnect_api_user_profile);
        try {
            user = ResponseParser.parseResponse(smartAPIRequestHandler.getRequest(this.apiKey, url, jwtToken));
        }catch (SmartAPIException ex) {
            logger.error(ex.getMessage());
            return null;
        } catch (IOException ex) {
            logger.error(ex.getMessage());
            return null;
        } catch (JSONException ex) {
            logger.error(ex.getMessage());
            return null;
        }
        user.setAccessToken(jwtToken);
        user.setRefreshToken(refreshTokenLocal);
        user.setFeedToken(feedToken);

        return user;

    }

    private JSONObject createLoginParams(String clientCode, String password, String totp) {
        // Create JSON params object needed to be sent to api.

        JSONObject params = new JSONObject();
        params.put(Constants.SmartConnect_clientcode, clientCode);
        params.put(Constants.SmartConnect_password, password);
        params.put(Constants.SmartConnect_totp, totp);
        return params;
    }

//    /**
//     * Get a new access token using refresh token.
//     *
//     * @param refreshToken is the refresh token obtained after generateSession.
//     * @param accessToken  is unique for each app.
//     * @return TokenSet contains user id, refresh token, api secret.
//     */
//    public TokenSet renewAccessToken(String accessToken, String refreshToken) {
//        try {
//            String hashableText = this.apiKey + refreshToken + accessToken;
//            String sha256hex = sha256Hex(hashableText);
//
//            JSONObject params = new JSONObject();
//            params.put(Constants.SmartConnect_refreshToken, refreshToken);
//            params.put(Constants.SmartConnect_checksum, sha256hex);
//            String url = routes.get(Constants.SmartConnect_api_refresh);
//            JSONObject response = smartAPIRequestHandler.postRequest(this.apiKey, url, params, accessToken);
//            logger.info("jsonObject: " + response);
//            accessToken = response.getJSONObject(Constants.SmartConnect_data).getString(Constants.SmartConnect_jwtToken);
//            refreshToken = response.getJSONObject(Constants.SmartConnect_data).getString(Constants.SmartConnect_refreshToken);
//
//            TokenSet tokenSet = new TokenSet();
//            tokenSet.setUserId(userId);
//            tokenSet.setAccessToken(accessToken);
//            tokenSet.setRefreshToken(refreshToken);
//
//            return tokenSet;
//        } catch (SmartAPIException e) {
//            logger.error(e.getMessage());
//            return null;
//        } catch (Exception e) {
//            logger.error(e.getMessage());
//            return null;
//        }
//    }

    /**
     * Hex encodes sha256 output for android support.
     *
     * @param str is the String that has to be encrypted.
     * @return Hex encoded String.
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
     */
    public User getProfile() {
        try {
            String url = routes.get(Constants.SmartConnect_api_user_profile);
            return ResponseParser.parseResponse(smartAPIRequestHandler.getRequest(this.apiKey, url, accessToken));

        } catch (SmartAPIException ex) {
            logger.error(ex.getMessage());
            return null;
        } catch (IOException ex) {
            logger.error(ex.getMessage());
            return null;
        } catch (JSONException ex) {
            logger.error(ex.getMessage());
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
     */
    public Order placeOrder(OrderParams orderParams, String variety) {


        try {

            Validators validator = new Validators();
            validator.orderValidator(orderParams);

            String url = routes.get(Constants.SmartConnect_api_order_place);

            JSONObject params = new JSONObject();
            params.put(Constants.EXCHANGE, orderParams.exchange);
            params.put(Constants.TRADING_SYMBOL, orderParams.tradingSymbol);
            params.put(Constants.TRANSACTION_TYPE, orderParams.transactionType);
            params.put(Constants.QUANTITY, orderParams.quantity);
            params.put(Constants.PRICE, orderParams.price);
            params.put(Constants.PRODUCT_TYPE, orderParams.productType);
            params.put(Constants.ORDER_TYPE, orderParams.orderType);
            params.put(Constants.DURATION, orderParams.duration);
            params.put(Constants.SYMBOL_TOKEN, orderParams.symbolToken);
            params.put(Constants.SQUARE_OFF, orderParams.squareOff);
            params.put(Constants.STOP_LOSS, orderParams.stopLoss);
            params.put(Constants.TRIGGER_PRICE, orderParams.triggerPrice);
            params.put(Constants.VARIETY, variety);


            JSONObject jsonObject = smartAPIRequestHandler.postRequest(this.apiKey, url, params, accessToken);
            logger.info("jsonObject: " + jsonObject);
            Order order = new Order();
            order.orderId = jsonObject.getJSONObject(Constants.SmartConnect_data).getString("orderid");
            logger.info(String.valueOf(order));
            return order;
        } catch (SmartAPIException ex) {
            logger.error(ex.getMessage());
            return null;
        } catch (IOException ex) {
            logger.error(ex.getMessage());
            return null;
        } catch (JSONException ex) {
            logger.error(ex.getMessage());
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
     */
    public Order modifyOrder(String orderId, OrderParams orderParams, String variety) {
        try {
            Validators validator = new Validators();
            validator.modifyOrderValidator(orderParams);

            String url = routes.get(Constants.SmartConnect_api_order_modify);

            JSONObject params = new JSONObject();


            params.put(Constants.EXCHANGE, orderParams.exchange);
            params.put(Constants.TRADING_SYMBOL, orderParams.tradingSymbol);
            params.put(Constants.SYMBOL_TOKEN, orderParams.symbolToken);
            params.put(Constants.QUANTITY, orderParams.quantity);
            params.put(Constants.PRICE, orderParams.price);
            params.put(Constants.PRODUCT_TYPE, orderParams.productType);
            params.put(Constants.ORDER_TYPE, orderParams.orderType);
            params.put(Constants.DURATION, orderParams.duration);
            params.put(Constants.VARIETY, variety);
            params.put(Constants.ORDER_ID, orderId);


            JSONObject jsonObject = smartAPIRequestHandler.postRequest(this.apiKey, url, params, accessToken);
            logger.info("JsonObject: " + jsonObject);
            Order order = new Order();
            order.orderId = jsonObject.getJSONObject(Constants.SmartConnect_data).getString("orderid");
            return order;
        } catch (SmartAPIException ex) {
            logger.error(ex.getMessage());
            return null;
        } catch (IOException ex) {
            logger.error(ex.getMessage());
            return null;
        } catch (JSONException ex) {
            logger.error(ex.getMessage());
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
     */
    public Order cancelOrder(String orderId, String variety) {
        try {
            String url = routes.get(Constants.SmartConnect_api_order_cancel);
            JSONObject params = new JSONObject();
            params.put(Constants.VARIETY, variety);
            params.put(Constants.ORDER_ID, orderId);

            JSONObject jsonObject = smartAPIRequestHandler.postRequest(this.apiKey, url, params, accessToken);
            logger.info("jsonObject: " + jsonObject);

            Order order = new Order();
            order.orderId = jsonObject.getJSONObject(Constants.SmartConnect_data).getString("orderid");
            return order;
        } catch (SmartAPIException ex) {
            logger.error(ex.getMessage());
            return null;
        } catch (IOException ex) {
            logger.error(ex.getMessage());
            return null;
        } catch (JSONException ex) {
            logger.error(ex.getMessage());
            return null;
        }
    }

    /**
     * Returns list of different stages an order has gone through.
     *
     * @return List of multiple stages an order has gone through in the system.
     * @throws SmartAPIException is thrown for all Smart API trade related errors.
     */
    @SuppressWarnings({})
    public JSONObject getOrderHistory() {
        try {
            String url = routes.get(Constants.SmartConnect_api_order_book);
            JSONObject response = smartAPIRequestHandler.getRequest(this.apiKey, url, accessToken);
            logger.info("response: " + response);
            return response;
        } catch (SmartAPIException ex) {
            logger.error(ex.getMessage());
            return null;
        } catch (IOException ex) {
            logger.error(ex.getMessage());
            return null;
        } catch (JSONException ex) {
            logger.error(ex.getMessage());
            return null;
        }
    }

    /**
     * Retrieves last price. User can either pass exchange with tradingsymbol or
     * instrument token only. For example {NSE:NIFTY 50, BSE:SENSEX} or {256265,
     * 265}.
     *
     * @return Map of String and LTPQuote.
     */
    public JSONObject getLTP(String exchange, String tradingSymbol, String symboltoken) {
        try {
            JSONObject params = new JSONObject();
            params.put(Constants.EXCHANGE, exchange);
            params.put(Constants.TRADING_SYMBOL, tradingSymbol);
            params.put(Constants.SYMBOL_TOKEN, symboltoken);


            String url = routes.get(Constants.SmartConnect_api_ltp_data);
            JSONObject response = smartAPIRequestHandler.postRequest(this.apiKey, url, params, accessToken);
            logger.info("jsonObject: " + response);
            return response.getJSONObject(Constants.SmartConnect_data);
        } catch (SmartAPIException ex) {
            logger.error(ex.getMessage());
            return null;
        } catch (IOException ex) {
            logger.error(ex.getMessage());
            return null;
        } catch (JSONException ex) {
            logger.error(ex.getMessage());
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
            String url = routes.get(Constants.SmartConnect_api_order_trade_book);
            JSONObject response = smartAPIRequestHandler.getRequest(this.apiKey, url, accessToken);
            logger.info("jsonObject: " + response);
            return response;
        }  catch (SmartAPIException ex) {
            logger.error(ex.getMessage());
            return null;
        } catch (IOException ex) {
            logger.error(ex.getMessage());
            return null;
        } catch (JSONException ex) {
            logger.error(ex.getMessage());
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
            String url = routes.get(Constants.SmartConnect_api_order_rms_data);
            JSONObject response = smartAPIRequestHandler.getRequest(this.apiKey, url, accessToken);
            logger.info("jsonObject: " + response);
            return response.getJSONObject(Constants.SmartConnect_data);
        } catch (SmartAPIException ex) {
            logger.error(ex.getMessage());
            return null;
        } catch (IOException ex) {
            logger.error(ex.getMessage());
            return null;
        } catch (JSONException ex) {
            logger.error(ex.getMessage());
            return null;
        }
    }

    /**
     * Retrieves Holding.
     *
     * @return Object of Holding.
     */
    public JSONObject getHolding() {
        try {
            String url = routes.get(Constants.SmartConnect_api_order_rms_holding);
            JSONObject response = smartAPIRequestHandler.getRequest(this.apiKey, url, accessToken);
            logger.info("jsonObject: " + response);
            return response;
        }  catch (SmartAPIException ex) {
            logger.error(ex.getMessage());
            return null;
        } catch (IOException ex) {
            logger.error(ex.getMessage());
            return null;
        } catch (JSONException ex) {
            logger.error(ex.getMessage());
            return null;
        }
    }

    /**
     * Retrieves position.
     *
     * @return Object of position.
     */
    public JSONObject getPosition() {
        try {
            String url = routes.get(Constants.SmartConnect_api_order_rms_position);
            JSONObject response = smartAPIRequestHandler.getRequest(this.apiKey, url, accessToken);
            logger.info("jsonObject: " + response);
            return response;
        }  catch (SmartAPIException ex) {
            logger.error(ex.getMessage());
            return null;
        } catch (IOException ex) {
            logger.error(ex.getMessage());
            return null;
        } catch (JSONException ex) {
            logger.error(ex.getMessage());
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
            String url = routes.get(Constants.SmartConnect_api_order_rms_position_convert);
            JSONObject response = smartAPIRequestHandler.postRequest(this.apiKey, url, params, accessToken);
            return response;
        }  catch (SmartAPIException ex) {
            logger.error(ex.getMessage());
            return null;
        } catch (IOException ex) {
            logger.error(ex.getMessage());
            return null;
        } catch (JSONException ex) {
            logger.error(ex.getMessage());
            return null;
        }
    }

    /**
     * Create a Gtt Rule.
     *
     * @param gttParams is gtt Params.
     * @return Gtt contains only orderId.
     */

    public String gttCreateRule(GttParams gttParams) {
        try {

            Validators validator = new Validators();

            validator.gttParamsValidator(gttParams);

            String url = routes.get(Constants.SmartConnect_api_gtt_create);

            JSONObject params = new JSONObject();
            params.put(Constants.TRADING_SYMBOL, gttParams.tradingSymbol);
            params.put(Constants.SYMBOL_TOKEN, gttParams.symbolToken);
            params.put(Constants.EXCHANGE, gttParams.exchange);
            params.put(Constants.TRANSACTION_TYPE, gttParams.transactionType);
            params.put(Constants.PRODUCT_TYPE, gttParams.productType);
            params.put(Constants.PRICE, gttParams.price);
            params.put(Constants.QTY, gttParams.qty);
            params.put(Constants.TRIGGER_PRICE, gttParams.triggerPrice);
            params.put(Constants.DISCLOSED_QTY, gttParams.disclosedQty);
            params.put(Constants.TIME_PERIOD, gttParams.timePeriod);

            JSONObject jsonObject = smartAPIRequestHandler.postRequest(this.apiKey, url, params, accessToken);
            int gttId = jsonObject.getJSONObject(Constants.SmartConnect_data).getInt("id");
            logger.info(String.valueOf(gttId));
            return String.valueOf(gttId);
        }  catch (SmartAPIException ex) {
            logger.error(ex.getMessage());
            return null;
        } catch (IOException ex) {
            logger.error(ex.getMessage());
            return null;
        } catch (JSONException ex) {
            logger.error(ex.getMessage());
            return null;
        }

    }

    /**
     * Modify a Gtt Rule.
     *
     * @param gttParams is gtt Params.
     * @return Gtt contains only orderId.
     */

    public String gttModifyRule(Integer id, GttParams gttParams) {
        try {
            Validators validator = new Validators();
            validator.gttModifyRuleValidator(gttParams);


            String url = routes.get(Constants.SmartConnect_api_gtt_modify);

            JSONObject params = new JSONObject();
            params.put(Constants.SYMBOL_TOKEN, gttParams.symbolToken);
            params.put(Constants.EXCHANGE, gttParams.exchange);
            params.put(Constants.PRICE, gttParams.price);
            params.put(Constants.QTY, gttParams.qty);
            params.put(Constants.TRIGGER_PRICE, gttParams.triggerPrice);
            params.put(Constants.DISCLOSED_QTY, gttParams.disclosedQty);
            params.put(Constants.TIME_PERIOD, gttParams.timePeriod);
            params.put(Constants.ID, id);

            JSONObject jsonObject = smartAPIRequestHandler.postRequest(this.apiKey, url, params, accessToken);

            int gttId  = jsonObject.getJSONObject(Constants.SmartConnect_data).getInt("id");
            logger.info(String.valueOf(gttId));
            return String.valueOf(gttId);
        }catch (SmartAPIException ex) {
            logger.error(ex.getMessage());
            return null;
        } catch (IOException ex) {
            logger.error(ex.getMessage());
            return null;
        } catch (JSONException ex) {
            logger.error(ex.getMessage());
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
            params.put(Constants.ID, id);
            params.put(Constants.SYMBOL_TOKEN, symboltoken);
            params.put(Constants.EXCHANGE, exchange);


            String url = routes.get(Constants.SmartConnect_api_gtt_cancel);
            JSONObject jsonObject = smartAPIRequestHandler.postRequest(this.apiKey, url, params, accessToken);
            Gtt gtt = new Gtt();
            gtt.id = jsonObject.getJSONObject(Constants.SmartConnect_data).getInt("id");
            logger.info(String.valueOf(gtt));
            return gtt;
        }  catch (SmartAPIException ex) {
            logger.error(ex.getMessage());
            return null;
        } catch (IOException ex) {
            logger.error(ex.getMessage());
            return null;
        } catch (JSONException ex) {
            logger.error(ex.getMessage());
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

            String url = routes.get(Constants.SmartConnect_api_gtt_details);
            JSONObject response = smartAPIRequestHandler.postRequest(this.apiKey, url, params, accessToken);
            logger.info(String.valueOf(response));

            return response.getJSONObject(Constants.SmartConnect_data);
        }  catch (SmartAPIException ex) {
            logger.error(ex.getMessage());
            return null;
        } catch (IOException ex) {
            logger.error(ex.getMessage());
            return null;
        } catch (JSONException ex) {
            logger.error(ex.getMessage());
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
            params.put(Constants.STATUS, status);
            params.put(Constants.PAGE, page);
            params.put(Constants.COUNT, count);

            String url = routes.get(Constants.SmartConnect_api_gtt_list);
            JSONObject response = smartAPIRequestHandler.postRequest(this.apiKey, url, params, accessToken);
            logger.info(String.valueOf(response));
            return response.getJSONArray(Constants.SmartConnect_data);
        }  catch (SmartAPIException ex) {
            logger.error(ex.getMessage());
            return null;
        } catch (IOException ex) {
            logger.error(ex.getMessage());
            return null;
        } catch (JSONException ex) {
            logger.error(ex.getMessage());
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
            String url = routes.get(Constants.SmartConnect_api_candle_data);
            JSONObject response = smartAPIRequestHandler.postRequest(this.apiKey, url, params, accessToken);
            logger.info(String.valueOf(response));
            return response.getString(Constants.SmartConnect_data);
        } catch (SmartAPIException ex) {
            logger.error(ex.getMessage());
            return null;
        } catch (IOException ex) {
            logger.error(ex.getMessage());
            return null;
        } catch (JSONException ex) {
            logger.error(ex.getMessage());
            return null;
        }
    }

    /**
     * Logs out user by invalidating the access token.
     *
     * @return JSONObject which contains status
     */

    public JSONObject logout() {
        try {
            String url = routes.get(Constants.SmartConnect_api_user_logout);
            JSONObject params = new JSONObject();
            params.put(Constants.SmartConnect_clientcode, this.userId);
            JSONObject response = smartAPIRequestHandler.postRequest(this.apiKey, url, params, accessToken);
            return response;
        }  catch (SmartAPIException ex) {
            logger.error(ex.getMessage());
            return null;
        } catch (IOException ex) {
            logger.error(ex.getMessage());
            return null;
        } catch (JSONException ex) {
            logger.error(ex.getMessage());
            return null;
        }
    }

}
