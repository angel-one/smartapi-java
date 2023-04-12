//package com.angelbroking.smartapi;
//
//import com.angelbroking.smartapi.http.SmartAPIRequestHandler;
//import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
//import com.angelbroking.smartapi.models.Order;
//import com.angelbroking.smartapi.models.OrderParams;
//import com.angelbroking.smartapi.models.User;
//import com.angelbroking.smartapi.utils.Constants;
//import com.angelbroking.smartapi.utils.Validators;
//import com.google.gson.JsonArray;
//import okhttp3.Response;
//import org.json.JSONException;
//import org.json.JSONObject;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.jupiter.api.BeforeEach;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.MockitoAnnotations;
//import org.mockito.stubbing.OngoingStubbing;
//
//import java.io.IOException;
//import java.util.concurrent.CompletableFuture;
//import static org.junit.Assert.*;
//import static org.mockito.Mockito.*;
//
//public class SmartConnectTest {
//
//    private static final String apiKey = "zkWvUuLx";
//    private static final String clientCode = "D541276";
//    private static final String password = "1501";
//    private static final String totp = "173716";
//
//    private static final String API_KEY = "myApiKey";
//    private static final String ACCESS_TOKEN = "myAccessToken";
//    private static final String USER_PROFILE_URL = "https://example.com/user/profile";
//    private static final String USER_PROFILE_RESPONSE = "{\"name\": \"John Doe\", \"email\": \"john.doe@example.com\"}";
//
//    @Mock
//    private SmartAPIRequestHandler mockRequestHandler;
//    private SmartConnect smartConnect;
//    private OrderParams orderParams;
//    private static final String ORDER_ID = "orderid";
//
//    public SmartConnectTest(String apiKey, String accessToken, JsonArray routes, SmartAPIRequestHandler smartAPIRequestHandler, Validators validator) {
//
//    }
//
//    @Before
//    public void setUp2() {
//        MockitoAnnotations.openMocks(this);
//        smartConnect = new SmartConnect(apiKey);
//        smartConnect.setSmartAPIRequestHandler(mockRequestHandler);
//
//    }
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        mockRequestHandler = mock(SmartAPIRequestHandler.class);
//        smartConnect = new SmartConnect(API_KEY);
//        smartConnect.setSmartAPIRequestHandler(mockRequestHandler);
//
//
//    }
//
//    @Test
//    public void testGenerateSessionWithValidCredentials() {
//        smartConnect = new SmartConnect(apiKey);
//        User user = smartConnect.generateSession(clientCode, password, totp);
//
//        assertNotNull(user);
//        assertNotNull(user.getAccessToken());
//        assertNotNull(user.getRefreshToken());
//        assertNotNull(user.getFeedToken());
//    }
//
//    @Test
//    public void testGenerateSessionWithInInvalidCredentials() {
//        smartConnect = new SmartConnect(apiKey);
//        User user = smartConnect.generateSession("invalid_client_code", "invalid_password", "invalid_totp");
//        assertNull(user);
//    }
//
//
//
//
//    @Test
//    public void testGetProfileSmartAPIException() throws SmartAPIException, IOException, JSONException {
//        when(mockRequestHandler.getRequest(API_KEY, USER_PROFILE_URL, ACCESS_TOKEN)).thenThrow(new SmartAPIException("API error"));
//        User user = smartConnect.getProfile();
//        assertNull(user);
//    }
//
//    @Test
//    public void testGetProfileIOException() throws SmartAPIException, IOException, JSONException {
//        when(mockRequestHandler.getRequest(API_KEY, USER_PROFILE_URL, ACCESS_TOKEN)).thenThrow(new IOException("Connection error"));
//        User user = smartConnect.getProfile();
//        assertNull(user);
//    }
//
//    @Test
//    public void testGetProfileJSONException() throws SmartAPIException, IOException, JSONException {
//        when(mockRequestHandler.getRequest(API_KEY, USER_PROFILE_URL, ACCESS_TOKEN)).thenThrow(new IOException("{}")); // invalid JSON response
//        User user = smartConnect.getProfile();
//        assertNull(user);
//    }
//
//
//
//    private JSONObject getOrderParamsJson(OrderParams orderParams) throws JSONException {
//        JSONObject params = new JSONObject();
//        params.put(Constants.EXCHANGE, orderParams.exchange);
//        params.put(Constants.TRADING_SYMBOL, orderParams.tradingSymbol);
//        params.put(Constants.TRANSACTION_TYPE, orderParams.transactionType);
//        params.put(Constants.QUANTITY, orderParams.quantity);
//        params.put(Constants.PRICE, orderParams.price);
//        params.put(Constants.PRODUCT_TYPE, orderParams.productType);
//        params.put(Constants.ORDER_TYPE, orderParams.orderType);
//        params.put(Constants.DURATION, orderParams.duration);
//        params.put(Constants.SYMBOL_TOKEN, orderParams.symbolToken);
//        params.put(Constants.SQUARE_OFF, orderParams.squareOff);
//        params.put(Constants.STOP_LOSS, orderParams.stopLoss);
//        params.put(Constants.TRIGGER_PRICE, orderParams.triggerPrice);
//        return params;
//    }
//
//    @Test
//    public void testPlaceOrder() throws Exception, SmartAPIException {
//        // Create mock objects for dependencies
//        Validators validator = Mockito.mock(Validators.class);
//        SmartAPIRequestHandler smartAPIRequestHandler = Mockito.mock(SmartAPIRequestHandler.class);
//
//        // Create an instance of the class under test
//        String accessToken;
//        accessToken = null;
//        JsonArray routes = null;
//        SmartConnectTest myClass = new SmartConnectTest(apiKey, accessToken, routes, smartAPIRequestHandler, validator);
//
//        // Create mock input parameters
//        OrderParams orderParams = new OrderParams();
//        orderParams.exchange = "NSE";
//        orderParams.tradingSymbol = "INFY";
//        orderParams.transactionType = "BUY";
//        orderParams.quantity = 1;
//        orderParams.price = Double.valueOf(1000);
//        orderParams.productType = "INTRADAY";
//        orderParams.orderType = "LIMIT";
//        orderParams.duration = "DAY";
//        orderParams.symbolToken = "123456";
//        orderParams.squareOff = String.valueOf(0);
//        orderParams.stopLoss = String.valueOf(0);
//        orderParams.triggerPrice = String.valueOf(0);
//
//        // Create mock response JSON
//        JSONObject responseJson = new JSONObject();
//        JSONObject dataJson = new JSONObject();
//        dataJson.put("orderID", "1234");
//        responseJson.put("data", dataJson);
//
//        // Set up mock behavior for validator
//        Mockito.doNothing().when(validator).orderValidator(orderParams);
//
//        // Set up mock behavior for smartAPIRequestHandler
//        Mockito.when(smartAPIRequestHandler.postRequest(apiKey, String.valueOf(routes.get(Integer.parseInt(Constants.SMART_CONNECT_API_ORDER_PLACE))), Mockito.any(JSONObject.class), accessToken))
//                .thenReturn(responseJson);
//
//        // Call the method under test
//        Order order = myClass.placeOrder(orderParams, "regular");
//
//        // Verify that the validator was called with the correct parameters
//        Mockito.verify(validator).orderValidator(orderParams);
//
//        // Verify that the smartAPIRequestHandler was called with the correct parameters
//        Mockito.verify(smartAPIRequestHandler).postRequest(apiKey, String.valueOf(routes.get(Integer.parseInt(Constants.SMART_CONNECT_API_ORDER_PLACE))), Mockito.any(JSONObject.class), accessToken);
//
//        // Verify that the method returns the expected result
//        assertNotNull(order);
//        assertEquals("1234", order.orderId);
//    }
//
//
//}
