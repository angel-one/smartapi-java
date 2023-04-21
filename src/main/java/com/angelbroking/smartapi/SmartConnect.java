package com.angelbroking.smartapi;

import com.angelbroking.smartapi.http.SessionExpiryHook;
import com.angelbroking.smartapi.http.SmartAPIRequestHandler;
import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import com.angelbroking.smartapi.http.exceptions.SmartConnectException;
import com.angelbroking.smartapi.models.GttParams;
import com.angelbroking.smartapi.models.GttRuleParams;
import com.angelbroking.smartapi.models.OrderParams;
import com.angelbroking.smartapi.models.User;
import com.angelbroking.smartapi.smartstream.models.LTPParams;
import com.angelbroking.smartapi.utils.ApiResponse;
import com.angelbroking.smartapi.utils.ResponseParser;
import com.angelbroking.smartapi.utils.Utils;
import com.angelbroking.smartapi.utils.Validators;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Proxy;
import java.util.List;
import java.util.Optional;

import static com.angelbroking.smartapi.utils.Constants.IO_EXCEPTION_ERROR_MSG;
import static com.angelbroking.smartapi.utils.Constants.IO_EXCEPTION_OCCURRED;
import static com.angelbroking.smartapi.utils.Constants.JSON_EXCEPTION_ERROR_MSG;
import static com.angelbroking.smartapi.utils.Constants.JSON_EXCEPTION_OCCURRED;
import static com.angelbroking.smartapi.utils.Constants.SMART_API_EXCEPTION_ERROR_MSG;
import static com.angelbroking.smartapi.utils.Constants.SMART_API_EXCEPTION_OCCURRED;
import static com.angelbroking.smartapi.utils.Constants.SMART_CONNECT_API_CANDLE_DATA;
import static com.angelbroking.smartapi.utils.Constants.SMART_CONNECT_API_GTT_CANCEL;
import static com.angelbroking.smartapi.utils.Constants.SMART_CONNECT_API_GTT_CREATE;
import static com.angelbroking.smartapi.utils.Constants.SMART_CONNECT_API_GTT_DETAILS;
import static com.angelbroking.smartapi.utils.Constants.SMART_CONNECT_API_GTT_LIST;
import static com.angelbroking.smartapi.utils.Constants.SMART_CONNECT_API_GTT_MODIFY;
import static com.angelbroking.smartapi.utils.Constants.SMART_CONNECT_API_LTP_DATA;
import static com.angelbroking.smartapi.utils.Constants.SMART_CONNECT_API_ORDER_BOOK;
import static com.angelbroking.smartapi.utils.Constants.SMART_CONNECT_API_ORDER_CANCEL;
import static com.angelbroking.smartapi.utils.Constants.SMART_CONNECT_API_ORDER_MODIFY;
import static com.angelbroking.smartapi.utils.Constants.SMART_CONNECT_API_ORDER_PLACE;
import static com.angelbroking.smartapi.utils.Constants.SMART_CONNECT_API_ORDER_RMS_DATA;
import static com.angelbroking.smartapi.utils.Constants.SMART_CONNECT_API_ORDER_RMS_HOLDING;
import static com.angelbroking.smartapi.utils.Constants.SMART_CONNECT_API_ORDER_RMS_POSITION;
import static com.angelbroking.smartapi.utils.Constants.SMART_CONNECT_API_ORDER_RMS_POSITION_CONVERT;
import static com.angelbroking.smartapi.utils.Constants.SMART_CONNECT_API_ORDER_TRADE_BOOK;
import static com.angelbroking.smartapi.utils.Constants.SMART_CONNECT_API_USER_LOGOUT;
import static com.angelbroking.smartapi.utils.Constants.SMART_CONNECT_API_USER_PROFILE;
import static com.angelbroking.smartapi.utils.Constants.SMART_CONNECT_DATA;
import static com.angelbroking.smartapi.utils.Constants.SMART_CONNECT_FEED_TOKEN;
import static com.angelbroking.smartapi.utils.Constants.SMART_CONNECT_JWT_TOKEN;
import static com.angelbroking.smartapi.utils.Constants.SMART_CONNECT_REFRESH_TOKEN;


@Data
@Slf4j
public class SmartConnect {

    private static final Routes routes = new Routes();
    private static final Proxy proxy = Proxy.NO_PROXY;
    private static SessionExpiryHook sessionExpiryHook = null;
    private static boolean enableLogging = false;
    private SmartAPIRequestHandler smartAPIRequestHandler;
    private String apiKey;
    private String accessToken;
    private String refreshToken;
    private String userId;

    public static SessionExpiryHook getSessionExpiryHook() {
        return sessionExpiryHook;
    }

    public static void setSessionExpiryHook(SessionExpiryHook sessionExpiryHook) {
        SmartConnect.sessionExpiryHook = sessionExpiryHook;
    }

    public static boolean isEnableLogging() {
        return enableLogging;
    }

    public static void setEnableLogging(boolean enableLogging) {
        SmartConnect.enableLogging = enableLogging;
    }

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
     * @throws SmartConnectException if _apiKey is not found.
     */
    public String getApiKey() throws SmartConnectException {
        if (apiKey != null) return apiKey;
        else throw new SmartConnectException("The API key is missing.");
    }


    /**
     * Returns accessToken.
     *
     * @return String access_token is returned.
     * @throws SmartConnectException if accessToken is null.
     */
    public String getAccessToken() throws SmartConnectException {
        if (accessToken != null) return accessToken;
        else throw new SmartConnectException("The Access Token key is missing.");
    }


    /**
     * Returns userId.
     *
     * @return String userId is returned.
     * @throws SmartConnectException if userId is null.
     */
    public String getUserId() {
        return Optional.ofNullable(userId).orElseThrow(() -> new SmartConnectException("The user ID is missing."));

    }


    /**
     * Returns publicToken.
     *
     * @return String public token is returned.
     * @throws SmartConnectException if publicToken is null.
     */
    public String getPublicToken() throws SmartConnectException {
        if (refreshToken != null) {
            return refreshToken;
        } else {
            throw new SmartConnectException("The Public Token key is missing.");
        }
    }


    /**
     * Retrieves login url
     *
     * @return String loginUrl is returned.
     */
    public String getLoginURL() throws SmartConnectException {
        String baseUrl = routes.getLoginUrl();
        if (baseUrl != null) {
            return baseUrl;
        } else {
            throw new SmartConnectException("The Login URL key is missing.");
        }
    }


    /**
     * Generates a session for the given client with the provided credentials and TOTP.
     *
     * @param clientCode the client code for which the session needs to be generated
     * @param password   the password of the client account
     * @param totp       the TOTP generated by the client's TOTP device
     * @return a User object representing the client's session, or null if an error occurs
     * @throws SmartAPIException if an error occurs while making the API request
     * @throws IOException       if an I/O error occurs while making the API request
     * @throws JSONException     if there is an error while parsing the JSON response
     */
    public User generateSession(String clientCode, String password, String totp) throws SmartAPIException, IOException {
        User user;
        JSONObject loginResultObject;
        try {
            smartAPIRequestHandler = new SmartAPIRequestHandler(proxy, 10000);

            loginResultObject = smartAPIRequestHandler.postRequest(this.apiKey, routes.getLoginUrl(), Utils.createLoginParams(clientCode, password, totp));
        } catch (SmartAPIException ex) {
            log.error("{} {}", SMART_API_EXCEPTION_OCCURRED, ex.getMessage());
            throw new SmartAPIException(SMART_API_EXCEPTION_ERROR_MSG);
        } catch (IOException ex) {
            log.error("{} {}", IO_EXCEPTION_OCCURRED, ex.getMessage());
            throw new IOException(IO_EXCEPTION_ERROR_MSG);
        } catch (JSONException ex) {
            log.error("{} {}", JSON_EXCEPTION_OCCURRED, ex.getMessage());
            throw new JSONException(JSON_EXCEPTION_ERROR_MSG);
        }
        String jwtToken = loginResultObject.optJSONObject(SMART_CONNECT_DATA).optString(SMART_CONNECT_JWT_TOKEN);
        String refreshTokenLocal = loginResultObject.optJSONObject(SMART_CONNECT_DATA).optString(SMART_CONNECT_REFRESH_TOKEN);
        String feedToken = loginResultObject.optJSONObject(SMART_CONNECT_DATA).optString(SMART_CONNECT_FEED_TOKEN);
        String url = routes.get(SMART_CONNECT_API_USER_PROFILE);
        try {
            user = ResponseParser.parseResponse(smartAPIRequestHandler.getRequest(this.apiKey, url, jwtToken));
        } catch (SmartAPIException ex) {
            log.error("{} {}", SMART_API_EXCEPTION_OCCURRED, ex.getMessage());
            throw new SmartAPIException(SMART_API_EXCEPTION_ERROR_MSG);
        } catch (IOException ex) {
            log.error("{} {}", IO_EXCEPTION_OCCURRED, ex.getMessage());
            throw new IOException(IO_EXCEPTION_ERROR_MSG);
        } catch (JSONException ex) {
            log.error("{} {}", JSON_EXCEPTION_OCCURRED, ex.getMessage());
            throw new JSONException(JSON_EXCEPTION_ERROR_MSG);
        }
        user.setAccessToken(jwtToken);
        user.setRefreshToken(refreshTokenLocal);
        user.setFeedToken(feedToken);

        return user;

    }


    /**
     * Get the profile details of the use.
     *
     * @return Profile is a POJO which contains profile related data.
     */
    public User getProfile() throws SmartAPIException, IOException {
        try {
            String url = routes.get(SMART_CONNECT_API_USER_PROFILE);
            return ResponseParser.parseResponse(smartAPIRequestHandler.getRequest(this.apiKey, url, accessToken));

        } catch (SmartAPIException ex) {
            log.error("{} {}", SMART_API_EXCEPTION_OCCURRED, ex.getMessage());
            throw new SmartAPIException(SMART_API_EXCEPTION_ERROR_MSG);
        } catch (IOException ex) {
            log.error("{} {}", IO_EXCEPTION_OCCURRED, ex.getMessage());
            throw new IOException(IO_EXCEPTION_ERROR_MSG);
        } catch (JSONException ex) {
            log.error("{} {}", JSON_EXCEPTION_OCCURRED, ex.getMessage());
            throw new JSONException(JSON_EXCEPTION_ERROR_MSG);
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
    public ApiResponse placeOrder(OrderParams orderParams, String variety) throws SmartAPIException, IOException {
        try {
            Validators validator = new Validators();
            orderParams.setVariety(variety);
            validator.orderValidator(orderParams);
            String url = routes.get(SMART_CONNECT_API_ORDER_PLACE);
            Gson gson = new Gson();
            String json = gson.toJson(orderParams);
            JSONObject jsonObject = smartAPIRequestHandler.postRequest(this.apiKey, url, new JSONObject(json), accessToken);
            return new ObjectMapper().readValue(jsonObject.toString(), ApiResponse.class);
        } catch (SmartAPIException ex) {
            log.error("{} {}", SMART_API_EXCEPTION_OCCURRED, ex.getMessage());
            throw new SmartAPIException(SMART_API_EXCEPTION_ERROR_MSG);
        } catch (IOException ex) {
            log.error("{} {}", IO_EXCEPTION_OCCURRED, ex.getMessage());
            throw new IOException(IO_EXCEPTION_ERROR_MSG);
        } catch (JSONException ex) {
            log.error("{} {}", JSON_EXCEPTION_OCCURRED, ex.getMessage());
            throw new JSONException(JSON_EXCEPTION_ERROR_MSG);
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
    public ApiResponse modifyOrder(String orderId, OrderParams orderParams, String variety) throws SmartAPIException, IOException {
        try {
            orderParams.setVariety(variety);
            orderParams.setOrderId(orderId);
            Validators validator = new Validators();
            validator.modifyOrderValidator(orderParams);
            String url = routes.get(SMART_CONNECT_API_ORDER_MODIFY);
            Gson gson = new Gson();
            String json = gson.toJson(orderParams);
            JSONObject jsonObject = smartAPIRequestHandler.postRequest(this.apiKey, url, new JSONObject(json), accessToken);
            return new ObjectMapper().readValue(jsonObject.toString(), ApiResponse.class);
        } catch (SmartAPIException ex) {
            log.error("{} {}", SMART_API_EXCEPTION_OCCURRED, ex.getMessage());
            throw new SmartAPIException(SMART_API_EXCEPTION_ERROR_MSG);
        } catch (IOException ex) {
            log.error("{} {}", IO_EXCEPTION_OCCURRED, ex.getMessage());
            throw new IOException(IO_EXCEPTION_ERROR_MSG);
        } catch (JSONException ex) {
            log.error("{} {}", JSON_EXCEPTION_OCCURRED, ex.getMessage());
            throw new JSONException(JSON_EXCEPTION_ERROR_MSG);
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
    public ApiResponse cancelOrder(String orderId, String variety) throws SmartAPIException, IOException {
        try {
            String url = routes.get(SMART_CONNECT_API_ORDER_CANCEL);
            OrderParams orderParams = new OrderParams();
            orderParams.setVariety(variety);
            orderParams.setOrderId(orderId);
            Gson gson = new Gson();
            String json = gson.toJson(orderParams);
            JSONObject jsonObject = smartAPIRequestHandler.postRequest(this.apiKey, url, new JSONObject(json), accessToken);
            return new ObjectMapper().readValue(jsonObject.toString(), ApiResponse.class);
        } catch (SmartAPIException ex) {
            log.error("{} {}", SMART_API_EXCEPTION_OCCURRED, ex.getMessage());
            throw new SmartAPIException(SMART_API_EXCEPTION_ERROR_MSG);
        } catch (IOException ex) {
            log.error("{} {}", IO_EXCEPTION_OCCURRED, ex.getMessage());
            throw new IOException(IO_EXCEPTION_ERROR_MSG);
        } catch (JSONException ex) {
            log.error("{} {}", JSON_EXCEPTION_OCCURRED, ex.getMessage());
            throw new JSONException(JSON_EXCEPTION_ERROR_MSG);
        }
    }

    /**
     * Returns list of different stages an order has gone through.
     *
     * @return List of multiple stages an order has gone through in the system.
     * @throws SmartAPIException is thrown for all Smart API trade related errors.
     */
    @SuppressWarnings({})
    public JSONObject getOrderHistory() throws SmartAPIException, IOException {
        try {
            String url = routes.get(SMART_CONNECT_API_ORDER_BOOK);
            return smartAPIRequestHandler.getRequest(this.apiKey, url, accessToken);
        } catch (SmartAPIException ex) {
            log.error("{} {}", SMART_API_EXCEPTION_OCCURRED, ex.getMessage());
            throw new SmartAPIException(SMART_API_EXCEPTION_ERROR_MSG);
        } catch (IOException ex) {
            log.error("{} {}", IO_EXCEPTION_OCCURRED, ex.getMessage());
            throw new IOException(IO_EXCEPTION_ERROR_MSG);
        } catch (JSONException ex) {
            log.error("{} {}", JSON_EXCEPTION_OCCURRED, ex.getMessage());
            throw new JSONException(JSON_EXCEPTION_ERROR_MSG);
        }
    }

    /**
     * Retrieves last price. User can either pass exchange with tradingsymbol or
     * instrument token only. For example {NSE:NIFTY 50, BSE:SENSEX} or {256265,
     * 265}.
     *
     * @return Map of String and LTPQuote.
     */
    public ApiResponse getLTP(String exchange, String tradingSymbol, String symboltoken) throws SmartAPIException, IOException {
        try {
            LTPParams ltpParams = new LTPParams();
            ltpParams.setExchange(exchange);
            ltpParams.setTradingSymbol(tradingSymbol);
            ltpParams.setSymbolToken(symboltoken);
            Gson gson = new Gson();
            String json = gson.toJson(ltpParams);
            String url = routes.get(SMART_CONNECT_API_LTP_DATA);
            JSONObject response = smartAPIRequestHandler.postRequest(this.apiKey, url, new JSONObject(json), accessToken);
            return new ObjectMapper().readValue(response.toString(), ApiResponse.class);
        } catch (SmartAPIException ex) {
            log.error("{} {}", SMART_API_EXCEPTION_OCCURRED, ex.getMessage());
            throw new SmartAPIException(SMART_API_EXCEPTION_ERROR_MSG);
        } catch (IOException ex) {
            log.error("{} {}", IO_EXCEPTION_OCCURRED, ex.getMessage());
            throw new IOException(IO_EXCEPTION_ERROR_MSG);
        } catch (JSONException ex) {
            log.error("{} {}", JSON_EXCEPTION_OCCURRED, ex.getMessage());
            throw new JSONException(JSON_EXCEPTION_ERROR_MSG);
        }
    }

    /**
     * Retrieves list of trades executed.
     *
     * @return List of trades.
     */
    public ApiResponse getTrades() throws SmartAPIException, IOException {
        try {
            String url = routes.get(SMART_CONNECT_API_ORDER_TRADE_BOOK);

            JSONObject response = smartAPIRequestHandler.getRequest(this.apiKey, url, accessToken);
            return new ObjectMapper().readValue(response.toString(), ApiResponse.class);
        } catch (SmartAPIException ex) {
            log.error("{} {}", SMART_API_EXCEPTION_OCCURRED, ex.getMessage());
            throw new SmartAPIException(SMART_API_EXCEPTION_ERROR_MSG);
        } catch (IOException ex) {
            log.error("{} {}", IO_EXCEPTION_OCCURRED, ex.getMessage());
            throw new IOException(IO_EXCEPTION_ERROR_MSG);
        } catch (JSONException ex) {
            log.error("{} {}", JSON_EXCEPTION_OCCURRED, ex.getMessage());
            throw new JSONException(JSON_EXCEPTION_ERROR_MSG);
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
    public ApiResponse getRMS() throws SmartAPIException, IOException {
        try {
            String url = routes.get(SMART_CONNECT_API_ORDER_RMS_DATA);
            JSONObject response = smartAPIRequestHandler.getRequest(this.apiKey, url, accessToken);
            return new ObjectMapper().readValue(response.toString(), ApiResponse.class);
        } catch (SmartAPIException ex) {
            log.error("{} {}", SMART_API_EXCEPTION_OCCURRED, ex.getMessage());
            throw new SmartAPIException(SMART_API_EXCEPTION_ERROR_MSG);
        } catch (IOException ex) {
            log.error("{} {}", IO_EXCEPTION_OCCURRED, ex.getMessage());
            throw new IOException(IO_EXCEPTION_ERROR_MSG);
        } catch (JSONException ex) {
            log.error("{} {}", JSON_EXCEPTION_OCCURRED, ex.getMessage());
            throw new JSONException(JSON_EXCEPTION_ERROR_MSG);
        }
    }

    /**
     * Retrieves Holding.
     *
     * @return Object of Holding.
     */
    public ApiResponse getHolding() throws SmartAPIException, IOException {
        try {
            String url = routes.get(SMART_CONNECT_API_ORDER_RMS_HOLDING);
            JSONObject response = smartAPIRequestHandler.getRequest(this.apiKey, url, accessToken);
            return new ObjectMapper().readValue(response.toString(), ApiResponse.class);
        } catch (SmartAPIException ex) {
            log.error("{} {}", SMART_API_EXCEPTION_OCCURRED, ex.getMessage());
            throw new SmartAPIException(SMART_API_EXCEPTION_ERROR_MSG);
        } catch (IOException ex) {
            log.error("{} {}", IO_EXCEPTION_OCCURRED, ex.getMessage());
            throw new IOException(IO_EXCEPTION_ERROR_MSG);
        } catch (JSONException ex) {
            log.error("{} {}", JSON_EXCEPTION_OCCURRED, ex.getMessage());
            throw new JSONException(JSON_EXCEPTION_ERROR_MSG);
        }
    }

    /**
     * Retrieves position.
     *
     * @return Object of position.
     */
    public ApiResponse getPosition() throws SmartAPIException, IOException {
        try {
            String url = routes.get(SMART_CONNECT_API_ORDER_RMS_POSITION);

            JSONObject response = smartAPIRequestHandler.getRequest(this.apiKey, url, accessToken);
            return new ObjectMapper().readValue(response.toString(), ApiResponse.class);
        } catch (SmartAPIException ex) {
            log.error("{} {}", SMART_API_EXCEPTION_OCCURRED, ex.getMessage());
            throw new SmartAPIException(SMART_API_EXCEPTION_ERROR_MSG);
        } catch (IOException ex) {
            log.error("{} {}", IO_EXCEPTION_OCCURRED, ex.getMessage());
            throw new IOException(IO_EXCEPTION_ERROR_MSG);
        } catch (JSONException ex) {
            log.error("{} {}", JSON_EXCEPTION_OCCURRED, ex.getMessage());
            throw new JSONException(JSON_EXCEPTION_ERROR_MSG);
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
    public ApiResponse convertPosition(JSONObject params) throws SmartAPIException, IOException {
        try {
            String url = routes.get(SMART_CONNECT_API_ORDER_RMS_POSITION_CONVERT);
            JSONObject response = smartAPIRequestHandler.postRequest(this.apiKey, url, params, accessToken);
            return new ObjectMapper().readValue(response.toString(), ApiResponse.class);
        } catch (SmartAPIException ex) {
            log.error("{} {}", SMART_API_EXCEPTION_OCCURRED, ex.getMessage());
            throw new SmartAPIException(SMART_API_EXCEPTION_ERROR_MSG);
        } catch (IOException ex) {
            log.error("{} {}", IO_EXCEPTION_OCCURRED, ex.getMessage());
            throw new IOException(IO_EXCEPTION_ERROR_MSG);
        } catch (JSONException ex) {
            log.error("{} {}", JSON_EXCEPTION_OCCURRED, ex.getMessage());
            throw new JSONException(JSON_EXCEPTION_ERROR_MSG);
        }
    }

    /**
     * Create a Gtt Rule.
     *
     * @param gttParams is gtt Params.
     * @return Gtt contains only orderId.
     */

    public ApiResponse gttCreateRule(GttParams gttParams) throws SmartAPIException, IOException {
        try {
            Validators validator = new Validators();
            validator.gttParamsValidator(gttParams);
            String url = routes.get(SMART_CONNECT_API_GTT_CREATE);
            Gson gson = new Gson();
            String json = gson.toJson(gttParams);
            JSONObject jsonObject = smartAPIRequestHandler.postRequest(this.apiKey, url, new JSONObject(json), accessToken);
            return new ObjectMapper().readValue(jsonObject.toString(), ApiResponse.class);
        } catch (SmartAPIException ex) {
            log.error("{} {}", SMART_API_EXCEPTION_OCCURRED, ex.getMessage());
            throw new SmartAPIException(SMART_API_EXCEPTION_ERROR_MSG);
        } catch (IOException ex) {
            log.error("{} {}", IO_EXCEPTION_OCCURRED, ex.getMessage());
            throw new IOException(IO_EXCEPTION_ERROR_MSG);
        } catch (JSONException ex) {
            log.error("{} {}", JSON_EXCEPTION_OCCURRED, ex.getMessage());
            throw new JSONException(JSON_EXCEPTION_ERROR_MSG);
        }

    }

    /**
     * Modify a Gtt Rule.
     *
     * @param gttParams is gtt Params.
     * @return Gtt contains only orderId.
     */

    public ApiResponse gttModifyRule(Integer id, GttParams gttParams) throws SmartAPIException, IOException {
        try {
            Validators validator = new Validators();
            gttParams.setId(id);
            validator.gttModifyRuleValidator(gttParams);
            String url = routes.get(SMART_CONNECT_API_GTT_MODIFY);
            Gson gson = new Gson();
            String json = gson.toJson(gttParams);
            JSONObject jsonObject = smartAPIRequestHandler.postRequest(this.apiKey, url, new JSONObject(json), accessToken);

            return new ObjectMapper().readValue(jsonObject.toString(), ApiResponse.class);
        } catch (SmartAPIException ex) {
            log.error("{} {}", SMART_API_EXCEPTION_OCCURRED, ex.getMessage());
            throw new SmartAPIException(SMART_API_EXCEPTION_ERROR_MSG);
        } catch (IOException ex) {
            log.error("{} {}", IO_EXCEPTION_OCCURRED, ex.getMessage());
            throw new IOException(IO_EXCEPTION_ERROR_MSG);
        } catch (JSONException ex) {
            log.error("{} {}", JSON_EXCEPTION_OCCURRED, ex.getMessage());
            throw new JSONException(JSON_EXCEPTION_ERROR_MSG);
        }


    }

    /**
     * Cancel a Gtt Rule.
     *
     * @param exchange,id,symboltoken is gtt Params.
     * @return Gtt contains only orderId.
     */

    public ApiResponse gttCancelRule(Integer id, String symboltoken, String exchange) throws SmartAPIException, IOException {
        try {
            GttParams gttParams = new GttParams();
            gttParams.setId(id);
            gttParams.setSymbolToken(symboltoken);
            gttParams.setExchange(exchange);
            Gson gson = new Gson();
            String json = gson.toJson(gttParams);
            String url = routes.get(SMART_CONNECT_API_GTT_CANCEL);
            JSONObject jsonObject = smartAPIRequestHandler.postRequest(this.apiKey, url, new JSONObject(json), accessToken);
            return new ObjectMapper().readValue(jsonObject.toString(), ApiResponse.class);
        } catch (SmartAPIException ex) {
            log.error("{} {}", SMART_API_EXCEPTION_OCCURRED, ex.getMessage());
            throw new SmartAPIException(SMART_API_EXCEPTION_ERROR_MSG);
        } catch (IOException ex) {
            log.error("{} {}", IO_EXCEPTION_OCCURRED, ex.getMessage());
            throw new IOException(IO_EXCEPTION_ERROR_MSG);
        } catch (JSONException ex) {
            log.error("{} {}", JSON_EXCEPTION_OCCURRED, ex.getMessage());
            throw new JSONException(JSON_EXCEPTION_ERROR_MSG);
        }
    }

    /**
     * Get Gtt Rule Details.
     *
     * @param id is gtt rule id.
     * @return returns the details of gtt rule.
     */

    public ApiResponse gttRuleDetails(Integer id) throws SmartAPIException, IOException {
        try {

            GttParams gttParams = new GttParams();
            gttParams.setId(id);

            String url = routes.get(SMART_CONNECT_API_GTT_DETAILS);
            Gson gson = new Gson();
            String json = gson.toJson(gttParams);
            JSONObject response = smartAPIRequestHandler.postRequest(this.apiKey, url, new JSONObject(json), accessToken);
            return new ObjectMapper().readValue(response.toString(), ApiResponse.class);
        } catch (SmartAPIException ex) {
            log.error("{} {}", SMART_API_EXCEPTION_OCCURRED, ex.getMessage());
            throw new SmartAPIException(SMART_API_EXCEPTION_ERROR_MSG);
        } catch (IOException ex) {
            log.error("{} {}", IO_EXCEPTION_OCCURRED, ex.getMessage());
            throw new IOException(IO_EXCEPTION_ERROR_MSG);
        } catch (JSONException ex) {
            log.error("{} {}", JSON_EXCEPTION_OCCURRED, ex.getMessage());
            throw new JSONException(JSON_EXCEPTION_ERROR_MSG);
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
    public ApiResponse gttRuleList(List<String> status, Integer page, Integer count) throws SmartAPIException, IOException {
        try {
            GttRuleParams gttRuleParams = new GttRuleParams();
            gttRuleParams.setStatus(status);
            gttRuleParams.setPage(page);
            gttRuleParams.setCount(count);
            Gson gson = new Gson();
            String json = gson.toJson(gttRuleParams);
            String url = routes.get(SMART_CONNECT_API_GTT_LIST);
            JSONObject response = smartAPIRequestHandler.postRequest(this.apiKey, url, new JSONObject(json), accessToken);
            return new ObjectMapper().readValue(response.toString(), ApiResponse.class);
        } catch (SmartAPIException ex) {
            log.error("{} {}", SMART_API_EXCEPTION_OCCURRED, ex.getMessage());
            throw new SmartAPIException(SMART_API_EXCEPTION_ERROR_MSG);
        } catch (IOException ex) {
            log.error("{} {}", IO_EXCEPTION_OCCURRED, ex.getMessage());
            throw new IOException(IO_EXCEPTION_ERROR_MSG);
        } catch (JSONException ex) {
            log.error("{} {}", JSON_EXCEPTION_OCCURRED, ex.getMessage());
            throw new JSONException(JSON_EXCEPTION_ERROR_MSG);
        }

    }

    /**
     * Get Historic Data.
     *
     * @param params is historic data params.
     * @return returns the details of historic data.
     */
    public ApiResponse candleData(JSONObject params) throws SmartAPIException, IOException {
        try {
            String url = routes.get(SMART_CONNECT_API_CANDLE_DATA);
            JSONObject response = smartAPIRequestHandler.postRequest(this.apiKey, url, params, accessToken);

            return new ObjectMapper().readValue(response.toString(), ApiResponse.class);
        } catch (SmartAPIException ex) {
            log.error("{} {}", SMART_API_EXCEPTION_OCCURRED, ex.getMessage());
            throw new SmartAPIException(SMART_API_EXCEPTION_ERROR_MSG);
        } catch (IOException ex) {
            log.error("{} {}", IO_EXCEPTION_OCCURRED, ex.getMessage());
            throw new IOException(IO_EXCEPTION_ERROR_MSG);
        } catch (JSONException ex) {
            log.error("{} {}", JSON_EXCEPTION_OCCURRED, ex.getMessage());
            throw new JSONException(JSON_EXCEPTION_ERROR_MSG);
        }
    }

    /**
     * Logs out user by invalidating the access token.
     *
     * @return JSONObject which contains status
     */

    public ApiResponse logout() throws SmartAPIException, IOException {
        try {
            String url = routes.get(SMART_CONNECT_API_USER_LOGOUT);
            User user = new User();
            user.setUserId(this.userId);
            Gson gson = new Gson();
            String json = gson.toJson(user);
            JSONObject response = smartAPIRequestHandler.postRequest(this.apiKey, url, new JSONObject(json), accessToken);
            return new ObjectMapper().readValue(response.toString(), ApiResponse.class);
        } catch (SmartAPIException ex) {
            log.error("{} {}", SMART_API_EXCEPTION_OCCURRED, ex.getMessage());
            throw new SmartAPIException(SMART_API_EXCEPTION_ERROR_MSG);
        } catch (IOException ex) {
            log.error("{} {}", IO_EXCEPTION_OCCURRED, ex.getMessage());
            throw new IOException(IO_EXCEPTION_ERROR_MSG);
        } catch (JSONException ex) {
            log.error("{} {}", JSON_EXCEPTION_OCCURRED, ex.getMessage());
            throw new JSONException(JSON_EXCEPTION_ERROR_MSG);
        }
    }

}
