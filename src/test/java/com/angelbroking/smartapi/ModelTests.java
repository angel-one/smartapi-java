package com.angelbroking.smartapi;

import com.angelbroking.smartapi.models.*;
import com.angelbroking.smartapi.smartstream.models.ExchangeType;
import com.angelbroking.smartapi.smartstream.models.LTP;
import com.angelbroking.smartapi.utils.Constants;
import com.google.gson.annotations.SerializedName;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ModelTests {

    /* ---------------- Profile Model Tests------------------------- */
    @Test
    public void testProfileModelWithAllValues() {
        Profile profile = new Profile();
        profile.setEmail("test@example.com");
        profile.setUserName("John Doe");
        profile.setBroker("ABC Broker");
        profile.setExchanges(new String[]{"Exchange1", "Exchange2"});
        profile.setProducts(new String[]{"Product1", "Product2"});

        assertEquals("test@example.com", profile.getEmail());
        assertEquals("John Doe", profile.getUserName());
        assertEquals("ABC Broker", profile.getBroker());
        assertArrayEquals(new String[]{"Exchange1", "Exchange2"}, profile.getExchanges());
        assertArrayEquals(new String[]{"Product1", "Product2"}, profile.getProducts());
    }

    @Test
    public void testGetEmailForProfileModel() {
        Profile profile = new Profile();
        profile.setEmail("test@example.com");

        assertEquals("test@example.com", profile.getEmail());
    }

    @Test
    public void testGetUserNameForProfileModel() {
        Profile profile = new Profile();
        profile.setUserName("John Doe");

        assertEquals("John Doe", profile.getUserName());
    }

    @Test
    public void testCreateProfileWithEmptyArrays() {
        Profile profile = new Profile();
        profile.setEmail("test@example.com");
        profile.setUserName("John Doe");
        profile.setBroker("ABC Broker");
        profile.setExchanges(new String[]{});
        profile.setProducts(new String[]{});

        assertEquals("test@example.com", profile.getEmail());
        assertEquals("John Doe", profile.getUserName());
        assertEquals("ABC Broker", profile.getBroker());
        assertArrayEquals(new String[]{}, profile.getExchanges());
        assertArrayEquals(new String[]{}, profile.getProducts());
    }

    @Test
    public void testCreateProfileWithNullArrays() {
        Profile profile = new Profile();
        profile.setEmail("test@example.com");
        profile.setUserName("John Doe");
        profile.setBroker("ABC Broker");
        profile.setExchanges(null);
        profile.setProducts(null);

        assertEquals("test@example.com", profile.getEmail());
        assertEquals("John Doe", profile.getUserName());
        assertEquals("ABC Broker", profile.getBroker());
        assertNull(profile.getExchanges());
        assertNull(profile.getProducts());
    }

    @Test
    public void testSetEmailToNull() {
        Profile profile = new Profile();
        profile.setEmail("test@example.com");
        profile.setUserName("John Doe");
        profile.setBroker("ABC Broker");

        profile.setEmail(null);

        assertNull(profile.getEmail());
    }

    @Test
    public void testGetBroker() {
        Profile profile = new Profile();
        profile.setBroker("ABC Broker");

        assertEquals("ABC Broker", profile.getBroker());
    }

    @Test
    public void testGetExchanges() {
        Profile profile = new Profile();
        profile.setExchanges(new String[]{"Exchange1", "Exchange2"});

        assertArrayEquals(new String[]{"Exchange1", "Exchange2"}, profile.getExchanges());
    }

    @Test
    public void testGetProducts() {
        Profile profile = new Profile();
        profile.setProducts(new String[]{"Product1", "Product2"});

        assertArrayEquals(new String[]{"Product1", "Product2"}, profile.getProducts());
    }

    /* ---------------- TokenSet Model Tests------------------------- */


    @Test
    public void testTokenSetModelWithAllValues() {
        TokenSet tokenSet = new TokenSet();
        tokenSet.setUserId("12345");
        assertEquals("12345", tokenSet.getUserId());
    }

    @Test
    public void testGetAccessTokenForTokenSetModel() {
        TokenSet tokenSet = new TokenSet();
        tokenSet.setAccessToken("abc123");
        assertEquals("abc123", tokenSet.getAccessToken());
    }

    @Test
    public void testGetRefreshTokenForTokenSetModel() {
        TokenSet tokenSet = new TokenSet();
        tokenSet.setRefreshToken("xyz789");
        assertEquals("xyz789", tokenSet.getRefreshToken());
    }

    @Test
    public void testNullUserIdForTokenSetModel() {
        TokenSet tokenSet = new TokenSet();
        assertNull(tokenSet.getUserId());
    }

    @Test
    public void testNullAccessToken() {
        TokenSet tokenSet = new TokenSet();
        assertNull(tokenSet.getAccessToken());
    }

    @Test
    public void testNullRefreshToken() {
        TokenSet tokenSet = new TokenSet();
        assertNull(tokenSet.getRefreshToken());
    }

    @Test
    public void testUserModelWithAllValues() {
        User user = new User();
        user.setUserName("John Doe");
        user.setUserId("123456");
        user.setMobileNo("9876543210");
        user.setBrokerName("Angel Broking");
        user.setEmail("john.doe@example.com");
        Date lastLoginTime = new Date();
        user.setLastLoginTime(lastLoginTime);
        user.setAccessToken("access_token");
        user.setRefreshToken("refresh_token");
        String[] products = {"Product 1", "Product 2"};
        user.setProducts(products);
        String[] exchanges = {"Exchange 1", "Exchange 2"};
        user.setExchanges(exchanges);
        user.setFeedToken("feed_token");

        assertEquals("John Doe", user.getUserName());
        assertEquals("123456", user.getUserId());
        assertEquals("9876543210", user.getMobileNo());
        assertEquals("Angel Broking", user.getBrokerName());
        assertEquals("john.doe@example.com", user.getEmail());
        assertEquals(lastLoginTime, user.getLastLoginTime());
        assertEquals("access_token", user.getAccessToken());
        assertEquals("refresh_token", user.getRefreshToken());
        assertArrayEquals(products, user.getProducts());
        assertArrayEquals(exchanges, user.getExchanges());
        assertEquals("feed_token", user.getFeedToken());
    }

    @Test
    public void testUserModelParseResponse() throws JSONException, ParseException {
        JSONObject response = new JSONObject();
        JSONObject data = new JSONObject();
        data.put("name", "John Doe");
        data.put("clientcode", "123456");
        data.put("mobileno", "9876543210");
        data.put("broker", "Angel Broking");
        data.put("email", "john.doe@example.com");
        data.put("lastlogintime", "2022-01-01 10:00:00");
        data.put("accessToken", "access_token");
        data.put("refreshToken", "refresh_token");
        JSONArray products = new JSONArray();
        products.put("Product 1");
        products.put("Product 2");
        data.put("products", products);
        JSONArray exchanges = new JSONArray();
        exchanges.put("Exchange 1");
        exchanges.put("Exchange 2");
        data.put("exchanges", exchanges);
        data.put("feedToken", "feed_token");
        response.put("data", data);

        User user = new User();
        user = user.parseResponse(response);

        System.out.println(user.getUserName());
        assertEquals("John Doe", user.getUserName());
        assertEquals("123456", user.getUserId());
        assertEquals("9876543210", user.getMobileNo());
        assertEquals("Angel Broking", user.getBrokerName());
        assertEquals("john.doe@example.com", user.getEmail());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date lastLoginTime = format.parse("2022-01-01 10:00:00");
        assertEquals(lastLoginTime, user.getLastLoginTime());
        assertEquals("access_token", user.getAccessToken());
        assertEquals("refresh_token", user.getRefreshToken());
        assertArrayEquals(new String[]{"Product 1", "Product 2"}, user.getProducts());
        assertArrayEquals(new String[]{"Exchange 1", "Exchange 2"}, user.getExchanges());
        assertEquals("feed_token", user.getFeedToken());
    }

    @Test
    public void testUserModelSetters() {
        User user = new User();
        user.setUserName("John Doe");
        user.setUserId("123456");
        user.setMobileNo("9876543210");
        user.setBrokerName("Angel Broking");
        user.setEmail("john.doe@example.com");
        Date lastLoginTime = new Date();
        user.setLastLoginTime(lastLoginTime);
        user.setAccessToken("access_token");
        user.setRefreshToken("refresh_token");
        String[] products = {"Product 1", "Product 2"};
        user.setProducts(products);
        String[] exchanges = {"Exchange 1", "Exchange 2"};
        user.setExchanges(exchanges);
        user.setFeedToken("feed_token");

        assertEquals("John Doe", user.getUserName());
        assertEquals("123456", user.getUserId());
        assertEquals("9876543210", user.getMobileNo());
        assertEquals("Angel Broking", user.getBrokerName());
        assertEquals("john.doe@example.com", user.getEmail());
        assertEquals(lastLoginTime, user.getLastLoginTime());
        assertEquals("access_token", user.getAccessToken());
        assertEquals("refresh_token", user.getRefreshToken());
        assertArrayEquals(products, user.getProducts());
        assertArrayEquals(exchanges, user.getExchanges());
        assertEquals("feed_token", user.getFeedToken());

        // Update fields
        user.setUserName("Jane Smith");
        user.setUserId("654321");
        user.setMobileNo("0123456789");
        user.setBrokerName("XYZ Broking");
        user.setEmail("jane.smith@example.com");
        Date newLastLoginTime = new Date();
        user.setLastLoginTime(newLastLoginTime);
        user.setAccessToken("new_access_token");
        user.setRefreshToken("new_refresh_token");
        String[] newProducts = {"Product 3", "Product 4"};
        user.setProducts(newProducts);
        String[] newExchanges = {"Exchange 3", "Exchange 4"};
        user.setExchanges(newExchanges);
        user.setFeedToken("new_feed_token");

        assertEquals("Jane Smith", user.getUserName());
        assertEquals("654321", user.getUserId());
        assertEquals("0123456789", user.getMobileNo());
        assertEquals("XYZ Broking", user.getBrokerName());
        assertEquals("jane.smith@example.com", user.getEmail());
        assertEquals(newLastLoginTime, user.getLastLoginTime());
        assertEquals("new_access_token", user.getAccessToken());
        assertEquals("new_refresh_token", user.getRefreshToken());
        assertArrayEquals(newProducts, user.getProducts());
        assertArrayEquals(newExchanges, user.getExchanges());
        assertEquals("new_feed_token", user.getFeedToken());
    }

    @Test
    public void test_null_or_missing_fields_in_json_response() throws JSONException {
        JSONObject response = null;
        JSONObject finalResponse = response;

        User user = new User();
        assertThrows(NullPointerException.class, () -> {
            user.parseResponse(finalResponse);
        });

        response = new JSONObject();
        assertThrows(NullPointerException.class, () -> {
            user.parseResponse(finalResponse);
        });

        JSONObject data = new JSONObject();
        response.put("data", data);
        assertThrows(NullPointerException.class, () -> {
            user.parseResponse(finalResponse);
        });

        data.put("name", "John Doe");
        assertThrows(NullPointerException.class, () -> {
            user.parseResponse(finalResponse);
        });
    }

    @Test
    public void test_trade_object_created_with_all_fields_populated() {
        Trade trade = new Trade();
        trade.tradeId = "123";
        trade.orderId = "456";
        trade.exchangeOrderId = "789";
        trade.tradingSymbol = "AAPL";
        trade.exchange = "NASDAQ";
        trade.instrumentToken = "12345";
        trade.product = "Equity";
        trade.averagePrice = "100.50";
        trade.quantity = "10";
        trade.fillTimestamp = new Date();
        trade.exchangeTimestamp = new Date();
        trade.transactionType = "BUY";

        assertNotNull(trade);
        assertEquals("123", trade.tradeId);
        assertEquals("456", trade.orderId);
        assertEquals("789", trade.exchangeOrderId);
        assertEquals("AAPL", trade.tradingSymbol);
        assertEquals("NASDAQ", trade.exchange);
        assertEquals("12345", trade.instrumentToken);
        assertEquals("Equity", trade.product);
        assertEquals("100.50", trade.averagePrice);
        assertEquals("10", trade.quantity);
        assertNotNull(trade.fillTimestamp);
        assertNotNull(trade.exchangeTimestamp);
        assertEquals("BUY", trade.transactionType);
    }

    @Test
    public void test_serialized_name_annotations_mapped_correctly() throws NoSuchFieldException {
        Trade trade = new Trade();

        assertNotNull(trade.getClass().getDeclaredField("tradeId").getAnnotation(SerializedName.class));
        assertNotNull(trade.getClass().getDeclaredField("orderId").getAnnotation(SerializedName.class));
        assertNotNull(trade.getClass().getDeclaredField("exchangeOrderId").getAnnotation(SerializedName.class));
        assertNotNull(trade.getClass().getDeclaredField("tradingSymbol").getAnnotation(SerializedName.class));
        assertNotNull(trade.getClass().getDeclaredField("exchange").getAnnotation(SerializedName.class));
        assertNotNull(trade.getClass().getDeclaredField("instrumentToken").getAnnotation(SerializedName.class));
        assertNotNull(trade.getClass().getDeclaredField("product").getAnnotation(SerializedName.class));
        assertNotNull(trade.getClass().getDeclaredField("averagePrice").getAnnotation(SerializedName.class));
        assertNotNull(trade.getClass().getDeclaredField("quantity").getAnnotation(SerializedName.class));
        assertNotNull(trade.getClass().getDeclaredField("fillTimestamp").getAnnotation(SerializedName.class));
        assertNotNull(trade.getClass().getDeclaredField("exchangeTimestamp").getAnnotation(SerializedName.class));
        assertNotNull(trade.getClass().getDeclaredField("transactionType").getAnnotation(SerializedName.class));
    }

    @Test
    public void test_date_fields_parsed_and_stored_correctly() {
        Trade trade = new Trade();
        Date fillTimestamp = new Date();
        Date exchangeTimestamp = new Date();

        trade.fillTimestamp = fillTimestamp;
        trade.exchangeTimestamp = exchangeTimestamp;

        assertEquals(fillTimestamp, trade.fillTimestamp);
        assertEquals(exchangeTimestamp, trade.exchangeTimestamp);
    }

    @Test
    public void test_trade_object_created_with_null_values() {
        Trade trade = new Trade();

        assertNull(trade.tradeId);
        assertNull(trade.orderId);
        assertNull(trade.exchangeOrderId);
        assertNull(trade.tradingSymbol);
        assertNull(trade.exchange);
        assertNull(trade.instrumentToken);
        assertNull(trade.product);
        assertNull(trade.averagePrice);
        assertNull(trade.quantity);
        assertNull(trade.fillTimestamp);
        assertNull(trade.exchangeTimestamp);
        assertNull(trade.transactionType);
    }

    @Test
    public void test_trade_object_created_with_invalid_date_formats() {
        Trade trade = new Trade();
        String invalidDateFormat = "2021-13-01";
        try {
            trade.fillTimestamp = new Date(invalidDateFormat);
            trade.exchangeTimestamp = new Date(invalidDateFormat);
        } catch (Exception e) {
            assertNull(trade.fillTimestamp);
            assertNull(trade.exchangeTimestamp);
        }
    }

    @Test
    public void test_fieldAccess() {
        Order order = new Order();
        order.disclosedQuantity = "10";
        order.duration = "DAY";
        order.tradingSymbol = "AAPL";
        order.variety = "NORMAL";
        order.orderType = "LIMIT";
        order.triggerPrice = "100.00";
        order.text = "Sample order";
        order.price = "99.50";
        order.status = "OPEN";
        order.productType = "CNC";
        order.exchange = "NSE";
        order.orderId = "12345";
        order.symbol = "AAPL";
        order.updateTime = "2021-01-01 10:00:00";
        order.exchangeTimestamp = "2021-01-01 10:00:00";
        order.exchangeUpdateTimestamp = "2021-01-01 10:00:00";
        order.averagePrice = "99.75";
        order.transactionType = "BUY";
        order.quantity = "100";
        order.squareOff = "0";
        order.stopLoss = "0";
        order.trailingStopLoss = "0";
        order.symbolToken = "123456";
        order.instrumentType = "EQ";
        order.strikePrice = "0.00";
        order.optionType = "";
        order.expiryDate = "";
        order.lotSize = "";
        order.cancelSize = "";
        order.filledShares = "";
        order.orderStatus = "";
        order.unfilledShares = "";
        order.fillId = "";
        order.fillTime = "";

        assertEquals("10", order.disclosedQuantity);
        assertEquals("DAY", order.duration);
        assertEquals("AAPL", order.tradingSymbol);
        assertEquals("NORMAL", order.variety);
        assertEquals("LIMIT", order.orderType);
        assertEquals("100.00", order.triggerPrice);
        assertEquals("Sample order", order.text);
        assertEquals("99.50", order.price);
        assertEquals("OPEN", order.status);
        assertEquals("CNC", order.productType);
        assertEquals("NSE", order.exchange);
        assertEquals("12345", order.orderId);
        assertEquals("AAPL", order.symbol);
        assertEquals("2021-01-01 10:00:00", order.updateTime);
        assertEquals("2021-01-01 10:00:00", order.exchangeTimestamp);
        assertEquals("2021-01-01 10:00:00", order.exchangeUpdateTimestamp);
        assertEquals("99.75", order.averagePrice);
        assertEquals("BUY", order.transactionType);
        assertEquals("100", order.quantity);
        assertEquals("0", order.squareOff);
        assertEquals("0", order.stopLoss);
        assertEquals("0", order.trailingStopLoss);
        assertEquals("123456", order.symbolToken);
        assertEquals("EQ", order.instrumentType);
        assertEquals("0.00", order.strikePrice);
        assertEquals("", order.optionType);
        assertEquals("", order.expiryDate);
        assertEquals("", order.lotSize);
        assertEquals("", order.cancelSize);
        assertEquals("", order.filledShares);
        assertEquals("", order.orderStatus);
        assertEquals("", order.unfilledShares);
        assertEquals("", order.fillId);
        assertEquals("", order.fillTime);
    }

    @Test
    void TestSSS34() {
        SmartConnect smartConnect = new SmartConnect();
        smartConnect.setApiKey("R2WSHRE9");
        User user = smartConnect.generateSession("user", "pass", "502216");

        System.out.println(smartConnect.getProfile());

    }

    @Test
    public void testGttModelWithAllFields() {
        Gtt gtt = new Gtt();
        gtt.id = 123;
        gtt.tradingSymbol = "AAPL";
        gtt.symbolToken = "1111";
        gtt.exchange = "NCE";
        gtt.transactionType = "BUY";
        gtt.productType = "Equity";
        gtt.price = 100;
        gtt.quantity = 10;
        gtt.triggerPrice = 100;
        gtt.disclosedQty = 10;
        gtt.timePeriod = 10;

        assertNotNull(gtt);
        assertEquals(123, (int) gtt.id);
        assertEquals("AAPL", gtt.tradingSymbol);
        assertEquals("1111", gtt.symbolToken);
        assertEquals("NCE", gtt.exchange);
        assertEquals("BUY", gtt.transactionType);
        assertEquals("Equity", gtt.productType);
        assertEquals(100, (int) gtt.price);
        assertEquals(10, (int) gtt.quantity);
        assertEquals(100,(int) gtt.triggerPrice);
        assertEquals(10, (int) gtt.disclosedQty);
        assertEquals(10, (int) gtt.timePeriod);
    }

    @Test
    public void test_empty_string() {
        assertFalse(Constants.PRODUCT_DELIVERY.isEmpty());
        assertFalse(Constants.PRODUCT_INTRADAY.isEmpty());
        assertFalse(Constants.PRODUCT_MARGIN.isEmpty());
        assertFalse(Constants.PRODUCT_BO.isEmpty());
        assertFalse(Constants.PRODUCT_CARRYFORWARD.isEmpty());

        assertFalse(Constants.ORDER_TYPE_MARKET.isEmpty());
        assertFalse(Constants.ORDER_TYPE_LIMIT.isEmpty());
        assertFalse(Constants.ORDER_TYPE_STOPLOSS_LIMIT.isEmpty());
        assertFalse(Constants.ORDER_TYPE_STOPLOSS_MARKET.isEmpty());

        assertFalse(Constants.VARIETY_NORMAL.isEmpty());
        assertFalse(Constants.VARIETY_AMO.isEmpty());
        assertFalse(Constants.VARIETY_STOPLOSS.isEmpty());
        assertFalse(Constants.VARIETY_ROBO.isEmpty());

        assertFalse(Constants.TRANSACTION_TYPE_BUY.isEmpty());
        assertFalse(Constants.TRANSACTION_TYPE_SELL.isEmpty());

        assertFalse(Constants.DURATION_DAY.isEmpty());
        assertFalse(Constants.DURATION_IOC.isEmpty());

        assertFalse(Constants.EXCHANGE_NSE.isEmpty());
        assertFalse(Constants.EXCHANGE_BSE.isEmpty());
        assertFalse(Constants.EXCHANGE_NFO.isEmpty());
        assertFalse(Constants.EXCHANGE_CDS.isEmpty());
        assertFalse(Constants.EXCHANGE_NCDEX.isEmpty());
        assertFalse(Constants.EXCHANGE_MCX.isEmpty());
    }

    @Test
    public void test_ltp_object_created_with_valid_input_parameters() {
        ByteBuffer buffer = ByteBuffer.allocate(25);
        buffer.put((byte) 0); // Subscription mode
        buffer.put((byte) 1); // Exchange type
        buffer.put("TOKEN1234567890".getBytes(StandardCharsets.UTF_8)); // Token
        buffer.putLong(Long.MAX_VALUE); // Sequence number
        buffer.putLong(Long.MAX_VALUE); // Exchange feed time epoch millis
        buffer.putLong(Long.MAX_VALUE); // Last traded price

        LTP ltp = new LTP(buffer);

        assertEquals(0, ltp.getSubscriptionMode());
        assertEquals(ExchangeType.NSE_CM, ltp.getExchangeType());
        assertEquals("NSE_CM-TOKEN1234567890", ltp.getToken().toString());
        assertEquals(1234567890L, ltp.getSequenceNumber());
        assertEquals(1609459200000L, ltp.getExchangeFeedTimeEpochMillis());
        assertEquals(1000L, ltp.getLastTradedPrice());
    }
}
