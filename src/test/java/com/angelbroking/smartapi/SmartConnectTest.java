package com.angelbroking.smartapi;

import com.angelbroking.smartapi.http.SmartAPIRequestHandler;
import com.angelbroking.smartapi.http.exceptions.DataException;
import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import com.angelbroking.smartapi.models.*;
import com.angelbroking.smartapi.utils.Constants;
import com.github.tomakehurst.wiremock.common.Json;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import static com.angelbroking.smartapi.utils.Constants.IO_EXCEPTION_ERROR_MSG;
import static com.angelbroking.smartapi.utils.Constants.IO_EXCEPTION_OCCURRED;
import static com.angelbroking.smartapi.utils.Constants.JSON_EXCEPTION_ERROR_MSG;
import static com.angelbroking.smartapi.utils.Constants.JSON_EXCEPTION_OCCURRED;
import static com.angelbroking.smartapi.utils.Constants.SMART_API_EXCEPTION_ERROR_MSG;
import static com.angelbroking.smartapi.utils.Constants.SMART_API_EXCEPTION_OCCURRED;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.Silent.class)
@Slf4j
public class SmartConnectTest {
    @Mock
    private SmartAPIRequestHandler smartAPIRequestHandler;

    @Mock
    private SmartConnect smartConnect;

    @Mock
    private Routes routes;
    private String apiKey;
    private String accessToken;


    @Before
    public void setup() {
        apiKey = "api_key_test";
        accessToken = "dwkdwodmi";
    }

    public JSONObject getErrorResponse(){
        JSONObject errorResponse = new JSONObject();
        errorResponse.put("status",false);
        errorResponse.put("message","error_message");
        errorResponse.put("errorcode","AB10011");
        errorResponse.put("data","null");

        return  errorResponse;
    }

    @Test
    public void generateSession() throws SmartAPIException, IOException {
        User user = new User();
        user.setAccessToken("dummyToken");
        user.setRefreshToken("dummyRefreshToken");
        user.setFeedToken("dummyfeedtoken");
        user.setUserId("user001");
        when(smartConnect.generateSession("user001","1100","123321")).thenReturn(user);

        User userData = smartConnect.generateSession("user001","1100","123321");
        assertNotNull(userData);
    }

    @Test(expected = SmartAPIException.class)
    public void generate_session_exception() throws SmartAPIException, IOException {
        String url = routes.getLoginUrl();
        JSONObject params = new JSONObject();
        params.put("clientcode", "user001");
        params.put("password", "1100");
        params.put("totp", "123321");
        when(smartAPIRequestHandler.postRequest(this.apiKey, url, params))
                .thenThrow(new SmartAPIException("Generate Session API request failed"));
        try {
            JSONObject response = smartAPIRequestHandler.postRequest(apiKey, url, params);
            response.getJSONObject("data");
        } catch (SmartAPIException ex) {
            throw new SmartAPIException(String.format("%s in generate session %s", SMART_API_EXCEPTION_ERROR_MSG, ex));
        }
    }

    @Test
    public void getProfile() throws SmartAPIException, IOException {
        User user = new User();
        user.setUserId("user001");
        user.setUserName("user_name");
        user.setEmail("usermail@mail.com");
        user.setMobileNo("987176688");
        when(smartConnect.getProfile()).thenReturn(user);

        User userData = smartConnect.getProfile();
        assertNotNull(userData);
    }

    @Test(expected = SmartAPIException.class)
    public void get_profile_exception() throws SmartAPIException, IOException {
        String url = routes.get("api.user.profile");
        when(smartAPIRequestHandler.getRequest(this.apiKey, url, accessToken))
                .thenThrow(new SmartAPIException("Get Profile API Request Failed"));
        try {
            JSONObject response = smartAPIRequestHandler.getRequest(apiKey, url, accessToken);
            response.getJSONObject("data");
        } catch (SmartAPIException ex) {
            throw new SmartAPIException(String.format("%s in generate session %s", SMART_API_EXCEPTION_ERROR_MSG, ex));
        }
    }

    @Test
    public void placeOrder() throws SmartAPIException, IOException {

        OrderParams orderParams = new OrderParams();
        orderParams.variety = Constants.VARIETY_STOPLOSS;
        orderParams.quantity = 1;
        orderParams.symboltoken = "1660";
        orderParams.exchange = Constants.EXCHANGE_NSE;
        orderParams.ordertype = Constants.ORDER_TYPE_STOPLOSS_LIMIT;
        orderParams.tradingsymbol = "ITC-EQ";
        orderParams.producttype = Constants.PRODUCT_INTRADAY;
        orderParams.duration = Constants.DURATION_DAY;
        orderParams.transactiontype = Constants.TRANSACTION_TYPE_BUY;
        orderParams.price = 122.2;
        orderParams.triggerprice = "209";

        Order orderResponse = new Order();
        orderResponse.orderId = "generated_orderid";

        when(smartConnect.placeOrder(orderParams,"STOPLOSS")).thenReturn(orderResponse);

        Order placeOrder = smartConnect.placeOrder(orderParams,"STOPLOSS");
        assertNotNull(placeOrder);
        assertEquals(Constants.ORDER_API_CALL_SUCCESS, placeOrder.status);
    }

    @Test(expected = SmartAPIException.class)
    public void place_order_exception() throws SmartAPIException, IOException {
        String url = routes.get("api.order.place");
        OrderParams orderParams = new OrderParams();
        orderParams.variety = Constants.VARIETY_STOPLOSS;
        orderParams.quantity = 1;
        orderParams.symboltoken = "1660";
        orderParams.exchange = Constants.EXCHANGE_NSE;
        orderParams.ordertype = Constants.ORDER_TYPE_STOPLOSS_LIMIT;
        orderParams.tradingsymbol = "ITC-EQ";
        orderParams.producttype = Constants.PRODUCT_INTRADAY;
        orderParams.duration = Constants.DURATION_DAY;
        orderParams.transactiontype = Constants.TRANSACTION_TYPE_BUY;
        orderParams.price = 122.2;
        orderParams.triggerprice = "209";

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

        when(smartAPIRequestHandler.postRequest(this.apiKey, url, params,accessToken))
                .thenThrow(new SmartAPIException("Place order API request failed"));
        try {
            JSONObject response = smartAPIRequestHandler.postRequest(apiKey, url, params,accessToken);
            response.getJSONObject("data");
        } catch (SmartAPIException ex) {
            throw new SmartAPIException(String.format("%s in place order %s", SMART_API_EXCEPTION_ERROR_MSG, ex));
        }
    }

    @Test
    public void modifyOrder() throws SmartAPIException,IOException {
        OrderParams orderParams = new OrderParams();
        orderParams.quantity = 1;
        orderParams.ordertype = Constants.ORDER_TYPE_LIMIT;
        orderParams.tradingsymbol = "ASHOKLEY";
        orderParams.symboltoken = "3045";
        orderParams.producttype = Constants.PRODUCT_DELIVERY;
        orderParams.exchange = Constants.EXCHANGE_NSE;
        orderParams.duration = Constants.DURATION_DAY;
        orderParams.price = 122.2;

        Order orderResponse = new Order();
        orderResponse.orderId = "generated_orderid";

        when(smartConnect.modifyOrder(orderResponse.orderId,orderParams, orderParams.variety)).thenReturn(orderResponse);

        Order modifyOrder = smartConnect.modifyOrder(orderResponse.orderId,orderParams, orderParams.variety);
        assertNotNull(modifyOrder);
        assertEquals(Constants.ORDER_API_CALL_SUCCESS, modifyOrder.status);
    }

    @Test
    public void cancelOrder() throws SmartAPIException,IOException {

        Order orderResponse = new Order();
        orderResponse.orderId = "generated_orderid";

        when(smartConnect.cancelOrder(orderResponse.orderId,Constants.VARIETY_NORMAL)).thenReturn(orderResponse);

        Order cancelOrder = smartConnect.cancelOrder(orderResponse.orderId,Constants.VARIETY_NORMAL);
        assertNotNull(cancelOrder);
        assertEquals(Constants.ORDER_API_CALL_SUCCESS, cancelOrder.status);
    }

    @Test
    public void getOrderHistory() throws SmartAPIException,IOException {

        JSONObject response = new JSONObject();
        response.put("status",true);
        response.put("message","success");

        when(smartConnect.getOrderHistory("user001")).thenReturn(response);

        JSONObject orderHistory = smartConnect.getOrderHistory("user001");
        assertNotNull(orderHistory);
        Boolean status = orderHistory.getBoolean("status");
        if(!status){
            assertEquals("AB10011",orderHistory.getString("errorcode"));
        }
    }

    @Test
    public void getLTP() throws SmartAPIException,IOException {

        JSONObject response = new JSONObject();
        response.put("status",true);
        response.put("message","success");
        JSONObject data = new JSONObject();
        data.put("exchange","NSE");
        data.put("tradingsymbol","SBIN-EQ");
        data.put("symboltoken","3045");
        data.put("open","186");
        data.put("high","191.25");
        data.put("low", "185");
        data.put("ltp","191");

        response.put("data",data);

        when(smartConnect.getLTP("NSE","SBIN-EQ","3045")).thenReturn(response);

        JSONObject ltpData = smartConnect.getLTP("NSE","SBIN-EQ","3045");
        assertNotNull(ltpData);
        Boolean status = ltpData.getBoolean("status");
        if(!status){
            assertEquals("AB10011",ltpData.getString("errorcode"));
        }
    }

    @Test
    public void getTrades() throws SmartAPIException,IOException {

        JSONObject response = new JSONObject();
        response.put("status",true);
        response.put("message","success");

        when(smartConnect.getTrades()).thenReturn(response);

        JSONObject tradebook = smartConnect.getTrades();
        assertNotNull(tradebook);
        Boolean status = tradebook.getBoolean("status");
        if(!status){
            assertEquals("AB10011",tradebook.getString("errorcode"));
        }
    }

    @Test
    public void getRMS() throws SmartAPIException,IOException {

        JSONObject response = new JSONObject();
        response.put("status",true);
        response.put("message","success");
        JSONObject data = new JSONObject();
        data.put("net","9999999999999");
        data.put("availablecash","9999999999999");
        data.put("availableintradaypayin","0");
        data.put("availablelimitmargin","0");
        data.put("collateral","0");
        data.put("m2munrealized", "0");
        data.put("utilisedpayout","0");

        response.put("data",data);

        when(smartConnect.getRMS()).thenReturn(response);

        JSONObject rms = smartConnect.getRMS();
        assertNotNull(rms);
        Boolean status = rms.getBoolean("status");
        if(!status){
            assertEquals("AB10011",rms.getString("errorcode"));
        }
    }

    @Test
    public void getHolding() throws SmartAPIException,IOException {

        JSONObject response = new JSONObject();
        response.put("status",true);
        response.put("message","success");
        JSONObject data = new JSONObject();
        data.put("tradingsymbol","TATASTEEL-EQ");
        data.put("exchange","NSE");
        data.put("isin","INE081A01020");
        data.put("t1quantity","0");
        data.put("realisedquantity","2");
        data.put("quantity", "2");
        data.put("product","DELIVERY");
        data.put("authorisedquantity","0");
        data.put("collateralquantity","null");
        data.put("collateraltype","null");
        data.put("haircut","0");
        data.put("averageprice","111.87");
        data.put("ltp","111.87");
        data.put("symboltoken","3499");
        data.put("close","129.6");
        data.put("profitandloss","37");
        data.put("pnlpercentage","16.34");

        response.put("data",data);

        when(smartConnect.getHolding()).thenReturn(response);

        JSONObject holdings = smartConnect.getHolding();
        assertNotNull(holdings);
        Boolean status = holdings.getBoolean("status");
        if(!status){
            assertEquals("AB10011",holdings.getString("errorcode"));
        }
    }

    @Test
    public void getAllHolding() throws SmartAPIException,IOException {

        JSONObject response = new JSONObject();
        response.put("status",true);
        response.put("message","success");
        JSONObject data = new JSONObject();
        JSONObject totalholding = new JSONObject();
        totalholding.put("totalholdingvalue","5294");
        totalholding.put("totalinvvalue","5116");
        totalholding.put("totalprofitandloss","178.14");
        totalholding.put("totalpnlpercentage","3.48");

        data.put("totalholding",totalholding);
        JSONObject holding = new JSONObject();
        holding.put("tradingsymbol","TATASTEEL-EQ");
        holding.put("exchange","NSE");
        holding.put("isin","INE081A01020");
        holding.put("t1quantity","0");
        holding.put("realisedquantity","2");
        holding.put("quantity", "2");
        holding.put("product","DELIVERY");
        holding.put("authorisedquantity","0");
        holding.put("collateralquantity","null");
        holding.put("collateraltype","null");
        holding.put("haircut","0");
        holding.put("averageprice","111.87");
        holding.put("ltp","111.87");
        holding.put("symboltoken","3499");
        holding.put("close","129.6");
        holding.put("profitandloss","37");
        holding.put("pnlpercentage","16.34");

        JSONArray holdings = new JSONArray();
        holdings.put(holding);
        data.put("holdings",holdings);
        response.put("data",data);

        when(smartConnect.getAllHolding()).thenReturn(response);

        JSONObject allHoldings = smartConnect.getAllHolding();
        assertNotNull(allHoldings);
        Boolean status = allHoldings.getBoolean("status");
        if(!status){
            assertEquals("AB10011",allHoldings.getString("errorcode"));
        }
    }

    @Test
    public void getPosition() throws SmartAPIException,IOException {

        JSONObject response = new JSONObject();
        response.put("status",true);
        response.put("message","success");

        JSONObject position = new JSONObject();
        position.put("tradingsymbol","TATASTEEL-EQ");
        position.put("exchange","NSE");
        position.put("instrumenttype","");
        position.put("t1quantity","0");
        position.put("realisedquantity","2");
        position.put("quantity", "2");
        position.put("product","DELIVERY");
        position.put("authorisedquantity","0");
        position.put("collateralquantity","null");
        position.put("collateraltype","null");
        position.put("haircut","0");
        position.put("averageprice","111.87");
        position.put("ltp","111.87");
        position.put("symboltoken","3499");
        position.put("close","129.6");
        position.put("totalbuyavgprice","37");
        position.put("totalsellavgprice","16.34");

        JSONArray positions = new JSONArray();
        positions.put(position);
        response.put("data",positions);

        when(smartConnect.getPosition()).thenReturn(response);

        JSONObject positionData = smartConnect.getPosition();
        assertNotNull(positionData);
    }

    @Test
    public void convertPosition() throws  SmartAPIException, IOException{

        JSONObject requestObejct = new JSONObject();
        requestObejct.put("exchange", "NSE");
        requestObejct.put("oldproducttype", "DELIVERY");
        requestObejct.put("newproducttype", "MARGIN");
        requestObejct.put("tradingsymbol", "SBIN-EQ");
        requestObejct.put("transactiontype", "BUY");
        requestObejct.put("quantity", 1);
        requestObejct.put("type", "DAY");

        JSONObject response = new JSONObject();
        response.put("status",true);
        response.put("message","success");
        response.put("errorcode","");
        response.put("data","null");

        when(smartConnect.convertPosition(requestObejct)).thenReturn(response);

        JSONObject convertPosition = smartConnect.convertPosition(requestObejct);
        assertNotNull(convertPosition);
    }

    @Test
    public void gttCreateRule() throws SmartAPIException,IOException {
        GttParams gttParams = new GttParams();

        gttParams.tradingsymbol = "SBIN-EQ";
        gttParams.symboltoken = "3045";
        gttParams.exchange = "NSE";
        gttParams.producttype = "MARGIN";
        gttParams.transactiontype = "BUY";
        gttParams.price = 100000.01;
        gttParams.qty = 10;
        gttParams.disclosedqty = 10;
        gttParams.triggerprice = 20000.1;
        gttParams.timeperiod = 300;

        Gtt response = new Gtt();

        when(smartConnect.gttCreateRule(gttParams)).thenReturn(response);

        Gtt gttData = smartConnect.gttCreateRule(gttParams);
        assertNotNull(gttData);
    }

    @Test
    public void gttModifyRule() throws SmartAPIException,IOException {
        GttParams gttParams = new GttParams();

        gttParams.tradingsymbol = "SBIN-EQ";
        gttParams.symboltoken = "3045";
        gttParams.exchange = "NSE";
        gttParams.producttype = "MARGIN";
        gttParams.transactiontype = "BUY";
        gttParams.price = 100000.1;
        gttParams.qty = 10;
        gttParams.disclosedqty = 10;
        gttParams.triggerprice = 20000.1;
        gttParams.timeperiod = 300;

        Integer id = 1000051;
        Gtt response = new Gtt();

        when(smartConnect.gttModifyRule(id,gttParams)).thenReturn(response);

        Gtt gttData = smartConnect.gttModifyRule(id,gttParams);
        assertNotNull(gttData);
    }

    @Test
    public void gttCancelRule() throws SmartAPIException,IOException {

        Integer id = 1000051;
        String token = "3045";
        String exchange = "NSE";

        Gtt response = new Gtt();

        when(smartConnect.gttCancelRule(id,token,exchange)).thenReturn(response);

        Gtt gttData = smartConnect.gttCancelRule(id,token,exchange);
        assertNotNull(gttData);
    }

    @Test
    public void gttRuleDetails() throws SmartAPIException,IOException {

        Integer id = 1000051;

        JSONObject response = new JSONObject();
        response.put("status",true);
        response.put("message","success");
        response.put("errorcode","");
        JSONObject data = new JSONObject();
        data.put("status","NEW");
        data.put("createddate","2020-11-16T14:19:51Z");
        data.put("updateddate","2020-11-16T14:28:01Z");
        data.put("expirydate","2021-11-16T14:19:51Z");
        data.put("clientid","100");
        data.put("tradingsymbol","SBIN-EQ");
        data.put("symboltoken","3045");
        data.put("exchange","NSE");
        data.put("transactiontype","BUY");
        data.put("producttype","DELIVERY");
        data.put("price","195");
        data.put("qty","1");
        data.put("triggerprice","196");
        data.put("disclosedqty","10");
        response.put("data",data);

        when(smartConnect.gttRuleDetails(id)).thenReturn(response);

        JSONObject gttData = smartConnect.gttRuleDetails(id);
        assertNotNull(gttData);
    }

    @Test
    public void gttRuleList() throws SmartAPIException,IOException {

        List<String> status = new ArrayList<String>() {
            {
                add("NEW");
                add("CANCELLED");
                add("ACTIVE");
                add("SENTTOEXCHANGE");
                add("FORALL");
            }
        };
        Integer page = 1;
        Integer count = 10;

        JSONObject gtt = new JSONObject();
        gtt.put("status","NEW");
        gtt.put("createddate","2020-11-16T14:19:51Z");
        gtt.put("updateddate","2020-11-16T14:28:01Z");
        gtt.put("expirydate","2021-11-16T14:19:51Z");
        gtt.put("clientid","100");
        gtt.put("tradingsymbol","SBIN-EQ");
        gtt.put("symboltoken","3045");
        gtt.put("exchange","NSE");
        gtt.put("transactiontype","BUY");
        gtt.put("producttype","DELIVERY");
        gtt.put("price","195");
        gtt.put("qty","1");
        gtt.put("triggerprice","196");
        gtt.put("disclosedqty","10");
        JSONArray data = new JSONArray();
        data.put(gtt);

        when(smartConnect.gttRuleList(status, page,count)).thenReturn(data);

        JSONArray gttData = smartConnect.gttRuleList(status, page,count);
        assertNotNull(gttData);
    }

    @Test
    public void candleData() throws SmartAPIException,IOException {

        JSONObject requestObejct = new JSONObject();
        requestObejct.put("exchange", "NSE");
        requestObejct.put("symboltoken", "3045");
        requestObejct.put("interval", "ONE_MINUTE");
        requestObejct.put("fromdate", "2021-03-08 09:00");
        requestObejct.put("todate", "2021-03-09 09:20");

        JSONObject response = new JSONObject();
        response.put("status",true);
        response.put("message","success");
        response.put("errorcode","");
        JSONArray candleData = new JSONArray();
        candleData.put("2023-09-06T11:15:00+05:30");
        candleData.put("19571.2");
        candleData.put("19571.34");
        response.put("data",candleData);

        when(smartConnect.candleData(requestObejct)).thenReturn(candleData);

        JSONArray candleDatas = smartConnect.candleData(requestObejct);
        assertNotNull(candleDatas);
    }

    @Test
    public void logout() throws SmartAPIException, IOException {
        JSONObject response = new JSONObject();
        response.put("status",true);
        response.put("message","success");
        response.put("errorcode","");
        response.put("data","");

        when(smartConnect.logout()).thenReturn(response);
        JSONObject user = smartConnect.logout();
        assertNotNull(user);
    }

    @Test
    public void estimateCharges() throws SmartAPIException, IOException {
        List<EstimateChargesParams> estimateChargesParamsList = new ArrayList<>();
        EstimateChargesParams estimate_Charges_Params = new EstimateChargesParams();
        estimate_Charges_Params.product_type = Constants.PRODUCT_DELIVERY;
        estimate_Charges_Params.transaction_type = Constants.TRANSACTION_TYPE_BUY;
        estimate_Charges_Params.quantity = "10";
        estimate_Charges_Params.price = "800";
        estimate_Charges_Params.exchange = Constants.EXCHANGE_NSE;
        estimate_Charges_Params.symbol_name = "745AS33";
        estimate_Charges_Params.token = "17117";

        JSONObject response = new JSONObject();
        response.put("status",true);
        response.put("message","success");
        response.put("errorcode","");
        response.put("data","");

        estimateChargesParamsList.add(estimate_Charges_Params);
        when(smartConnect.estimateCharges(estimateChargesParamsList)).thenReturn(response);
        JSONObject estimateData = smartConnect.estimateCharges(estimateChargesParamsList);
        assertNotNull(estimateData);
    }

    @Test
    public void verifyDis() throws SmartAPIException, IOException{
        JSONObject payload = new JSONObject();
        payload.put("isin", "INE242A01010");
        payload.put("quantity", "1");

        JSONObject response = new JSONObject();
        response.put("status",true);
        response.put("message","success");
        response.put("errorcode","");

        JSONObject data = new JSONObject();
        data.put("ReqId","8180722758077144");
        data.put("ReturnURL","https://trade.angelbroking.com/cdslpoa/response");
        data.put("DPId","33200");
        data.put("BOID","1203320015222472");
        data.put("TransDtls","fZ6jlmt9GosSO0kFdI9S9FCD+IYFvjUN94Bph46YMC1BzexE1hDuCwjSUzXVoEgV3mTP5aXnwxJDikFm1xymcjfhEgtWm7ykrpxaDXSuQF25Fg5olDgXy/Suu916VNg9XID0GHy1CT5E9WBP0gocixvOFrCPFmRMmY+7Nqbb3cNfqPZ8uHfX0un5tFnQXV3AXK0g2exwRf3EINSTHSv66/inPArtDko70vuc1pb5e0tRXa14FN2hzklop2UyrcIiDmkmzcHvjdGGomCM4PU90S28xHze5/dAlTv7ywIzrQOA4N2LW7n7M9atFExQTC2tczdwhKkVzScYmpVdRvtY+Lqo8o/OSVQ8Gd3Guz7ZRpqV5073lZ/BnPN8FtJW6shfOwgbYgqmhV2jik0uw57eQ7f+SI0pYYC8noOwKncJ3/umtVY2NVgLjdZq2yxZh7wTQKC2WYaYl/MnZLpgF9yYlBAtZ6trgLG6kcmtlbibtkimi2TecMGN3KrAETsWY07fZCc9Ks/RetFzD1qNIGWZ/4WnpWyW5XTxSxlQILCz7Qwk5f+dKcXVe5CS/7DMB7aa9KQ4t1ru1xo7jM4GmUdwcGVwUqb2pfNSEUX+I10INA0TwXkWdUAAQYO0++q6mYpN59G3IW9vV/dW0/bHh4xcsPg9ysDBZ8bLlk4C3hL6RiVrxcF2AgxjJgLSxL2YerAKkgivk22KULtEDpOpE7IcwxjuWjRxOPGfnSGbc4Ii1avte50WZFh6EKIP6E16sZgKaOh/r5CSnysEtDetvhn8siIWDQbhInAoExMpsuewIthg4QVWvx0EzQL6LqNO74Hc9/JicH+cGFtn6nHjwHgquzbCZx6W6XlzHJVV04Pd0m4kXC27RrnoraFj3KpXcfmdSr0GK3HtUKYV2QgSM8COvEEuL++oxjD4PKi40JmTv2I=");
        data.put("version","1.1");

        response.put("data",data);
        when(smartConnect.verifyDis(payload)).thenReturn(response);

        JSONObject verifyDisResponse = smartConnect.verifyDis(payload);
        assertNotNull(verifyDisResponse);
    }

    @Test
    public void generateTPIN() throws SmartAPIException,IOException{
        JSONObject payload = new JSONObject();
        payload.put("dpId", "33200");
        payload.put("ReqId", "1431307824801952");
        payload.put("boid", "1203320018563571");
        payload.put("pan", "JZTPS2255C");

        JSONObject response = new JSONObject();
        response.put("status",true);
        response.put("message","success");
        response.put("errorcode","");
        response.put("data","null");

        when(smartConnect.generateTPIN(payload)).thenReturn(response);
        JSONObject tpinResponse = smartConnect.generateTPIN(payload);
        assertNotNull(tpinResponse);
    }

    @Test
    public void getTranStatus() throws SmartAPIException,IOException{
        JSONObject payload = new JSONObject();
        payload.put("ReqId", "1431307824801952");

        JSONObject response = new JSONObject();
        response.put("status",true);
        response.put("message","success");
        response.put("errorcode","");

        JSONObject data = new JSONObject();
        data.put("ReqId","8180722758077144");
        data.put("ReqType","D");
        data.put("ResId","1603202259241073");
        data.put("ResStatus","0");
        data.put("ResTime","17032022000022");
        data.put("ResError","");
        data.put("Remarks","");

        response.put("data",data);
        when(smartConnect.getTranStatus(payload)).thenReturn(response);
        JSONObject transResponse = smartConnect.getTranStatus(payload);
        assertNotNull(transResponse);
    }

    @Test
    public void optionGreek() throws SmartAPIException,IOException{
        JSONObject payload = new JSONObject();
        payload.put("name", "TCS");
        payload.put("expirydate", "25MAR2024");

        JSONObject response = new JSONObject();
        response.put("status",true);
        response.put("message","success");
        response.put("errorcode","");

        JSONObject obj = new JSONObject();
        obj.put("name","TCS");
        obj.put("expiry","25JAN2024");
        obj.put("strikePrice","3900.000000");
        obj.put("optionType","CE");
        obj.put("delta","0.492400");
        obj.put("gamma","0.002800");
        obj.put("tradeVolume","24048.00");
        JSONArray data = new JSONArray();
        data.put(data);
        response.put("data",data);

        when(smartConnect.optionGreek(payload)).thenReturn(response);
        JSONObject optionResponse = smartConnect.optionGreek(payload);
        assertNotNull(optionResponse);
    }

    @Test
    public void gainersLosers() throws SmartAPIException,IOException{
        JSONObject payload = new JSONObject();
        payload.put("datatype", "PercOIGainers");
        payload.put("expirytype", "NEAR");

        JSONObject response = new JSONObject();
        response.put("status",true);
        response.put("message","success");
        response.put("errorcode","");

        JSONObject obj = new JSONObject();
        obj.put("tradingSymbol","HDFCBANK25JAN24FUT");
        obj.put("percentChange","20.02");
        obj.put("symbolToken","55394");
        obj.put("opnInterest","118861600");
        obj.put("netChangeOpnInterest","1.98253E7");
        JSONArray data = new JSONArray();
        data.put(data);
        response.put("data",data);

        when(smartConnect.gainersLosers(payload)).thenReturn(response);
        JSONObject gainersLosers = smartConnect.gainersLosers(payload);
        assertNotNull(gainersLosers);
    }

    @Test
    public void putCallRatio() throws SmartAPIException,IOException{
        JSONObject response = new JSONObject();
        response.put("status",true);
        response.put("message","success");
        response.put("errorcode","");

        JSONObject obj = new JSONObject();
        obj.put("pcr",1.04);
        obj.put("tradingSymbol","NIFTY25JAN24FUT");
        JSONArray data = new JSONArray();
        data.put(data);
        response.put("data",data);

        when(smartConnect.putCallRatio()).thenReturn(response);
        JSONObject putCallRatio = smartConnect.putCallRatio();
        assertNotNull(putCallRatio);
    }

    @Test
    public void oIBuildup() throws SmartAPIException,IOException{
        JSONObject payload = new JSONObject();
        payload.put("expirytype", "NEAR");
        payload.put("datatype", "Long Built Up");

        JSONObject response = new JSONObject();
        response.put("status",true);
        response.put("message","success");
        response.put("errorcode","");

        JSONObject obj = new JSONObject();
        obj.put("symbolToken",55424);
        obj.put("ltp","723.8");
        obj.put("netChange","-28.25");
        obj.put("percentChange","-3.76");
        obj.put("opnInterest","24982.5");
        obj.put("netChangeOpnInterest","76.25");
        obj.put("tradingSymbol","JINDALSTEL25JAN24FUT");
        JSONArray data = new JSONArray();
        data.put(data);
        response.put("data",data);

        when(smartConnect.oIBuildup(payload)).thenReturn(response);
        JSONObject responseData = smartConnect.oIBuildup(payload);
        assertNotNull(responseData);
    }

    @Test
    public void getSearchScrip() throws SmartAPIException, IOException {
        // Mock the necessary objects
        JSONObject payload = new JSONObject();
        when(smartConnect.getSearchScrip(payload)).thenReturn("response-data");

        // Call the method under test
        String result = smartConnect.getSearchScrip(payload);
        // Assert the result
        assertEquals("response-data", result);

    }

    @Test(expected = SmartAPIException.class)
    public void testGetSearchScript_Exception() throws SmartAPIException, IOException {
        JSONObject payload = new JSONObject();
        SmartAPIException expectedException = new SmartAPIException("Simulated SmartAPIException");
        when(smartConnect.getSearchScrip(payload)).thenThrow(expectedException);
        try {
            smartConnect.getSearchScrip(payload);
        } catch (SmartAPIException e) {
            throw new SmartAPIException(String.format("The operation failed to execute because of a SmartAPIException error in Search scrip api data %s", e));
        }
        verify(smartConnect).getSearchScrip(payload);
    }




    private static JSONObject createMarketDataResponse() {
        JSONObject jsonObject = new JSONObject();

        // Create "data" object
        JSONObject dataObject = new JSONObject();

        // Create the "unfetched" array
        JSONArray unfetchedArray = new JSONArray();
        dataObject.put("unfetched", unfetchedArray);

        // Create the "fetched" array and its elements
        JSONArray fetchedArray = new JSONArray();
        JSONObject fetchedElement = new JSONObject();
        fetchedElement.put("netChange", 3.15);
        fetchedElement.put("tradeVolume", 5718111);
        fetchedElement.put("lowerCircuit", 533.15);
        fetchedElement.put("percentChange", 0.53);
        fetchedElement.put("exchFeedTime", "19-Jul-2023 11:20:51");
        fetchedElement.put("avgPrice", 595.25);
        fetchedElement.put("ltp", 595.5);
        fetchedElement.put("exchTradeTime", "19-Jul-2023 11:20:51");
        fetchedElement.put("totSellQuan", 1924292);
        fetchedElement.put("upperCircuit", 651.55);
        fetchedElement.put("lastTradeQty", 46);
        fetchedElement.put("high", 599.6);
        fetchedElement.put("totBuyQuan", 890300);

        // Create the "depth" object and its "buy" and "sell" arrays
        JSONObject depthObject = new JSONObject();
        JSONArray buyArray = new JSONArray();
        JSONArray sellArray = new JSONArray();

        // Add elements to "buy" array
        JSONObject buyElement1 = new JSONObject();
        buyElement1.put("quantity", 1776);
        buyElement1.put("price", 595.3);
        buyElement1.put("orders", 3);
        buyArray.put(buyElement1);

        JSONObject buyElement2 = new JSONObject();
        buyElement2.put("quantity", 767);
        buyElement2.put("price", 595.25);
        buyElement2.put("orders", 3);
        buyArray.put(buyElement2);

        // Add elements to "sell" array
        JSONObject sellElement1 = new JSONObject();
        sellElement1.put("quantity", 249);
        sellElement1.put("price", 595.5);
        sellElement1.put("orders", 5);
        sellArray.put(sellElement1);

        JSONObject sellElement2 = new JSONObject();
        sellElement2.put("quantity", 1379);
        sellElement2.put("price", 595.55);
        sellElement2.put("orders", 4);
        sellArray.put(sellElement2);

        // Add "buy" and "sell" arrays to "depth" object
        depthObject.put("buy", buyArray);
        depthObject.put("sell", sellArray);

        fetchedElement.put("depth", depthObject);

        // Add remaining properties to "fetched" element
        fetchedElement.put("low", 592);
        fetchedElement.put("exchange", "NSE");
        fetchedElement.put("opnInterest", 0);
        fetchedElement.put("tradingSymbol", "SBIN-EQ");
        fetchedElement.put("symbolToken", "3045");
        fetchedElement.put("close", 592.35);
        fetchedElement.put("52WeekLow", 482.1);
        fetchedElement.put("open", 594.65);
        fetchedElement.put("52WeekHigh", 629.55);

        // Add the "fetched" element to the "fetched" array
        fetchedArray.put(fetchedElement);

        dataObject.put("fetched", fetchedArray);

        // Add "data" object, "message", "errorcode", and "status" properties to the main JSON object
        jsonObject.put("data", dataObject);
        jsonObject.put("message", "SUCCESS");
        jsonObject.put("errorcode", "");
        jsonObject.put("status", true);

        return jsonObject;
    }

    // Testing market data success for Full payload
    @Test
    public void marketData() throws SmartAPIException, IOException {
        String url = routes.get("api.market.data");
        JSONObject params = getMarketDataRequest("FULL");
        when(smartAPIRequestHandler.postRequest(eq(this.apiKey), eq(url), eq(params), eq(this.accessToken))).thenReturn(createMarketDataResponse());
        try {
            JSONObject response = smartAPIRequestHandler.postRequest(this.apiKey, url, params, this.accessToken);
            JSONObject data = response.getJSONObject("data");
            assertNotNull(data);
        } catch (SmartAPIException ex) {
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

    // Testing market data failure for Full payload
    @Test(expected = SmartAPIException.class)
    public void testMarketData_Failure() throws SmartAPIException, IOException {
        // Stub the postRequest method
        String url = routes.get("api.market.data");
        JSONObject params = getMarketDataRequest("FULL");
        when(smartAPIRequestHandler.postRequest(eq(this.apiKey), eq(url), eq(params), eq(this.accessToken)))
                .thenThrow(new SmartAPIException("API request failed"));
        try {
            JSONObject response = smartAPIRequestHandler.postRequest(apiKey, url, params, accessToken);
            response.getJSONObject("data");
        } catch (SmartAPIException ex) {
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

    // Testing market data success for LTP payload
    @Test
    public void testMarketDataLTP_Success() throws SmartAPIException, IOException {
        String url = routes.get("api.market.data");
        JSONObject params = getMarketDataRequest("LTP");
        when(smartAPIRequestHandler.postRequest(eq(this.apiKey), eq(url), eq(params), eq(this.accessToken))).thenReturn(createMarketDataResponse());
        try {
            JSONObject response = smartAPIRequestHandler.postRequest(this.apiKey, url, params, this.accessToken);
            JSONObject data = response.getJSONObject("data");
            assertNotNull(data);
        } catch (SmartAPIException ex) {
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

    // Testing market data failure for LTP payload
    @Test(expected = SmartAPIException.class)
    public void testMarketDataLTP_Failure() throws SmartAPIException, IOException {
        // Stub the postRequest method
        String url = routes.get("api.market.data");
        JSONObject params = getMarketDataRequest("LTP");
        when(smartAPIRequestHandler.postRequest(eq(this.apiKey), eq(url), eq(params), eq(this.accessToken)))
                .thenThrow(new SmartAPIException("API request failed"));
        try {
            JSONObject response = smartAPIRequestHandler.postRequest(apiKey, url, params, accessToken);
            response.getJSONObject("data");
        } catch (SmartAPIException ex) {
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

    // Testing market data success for OHLC payload
    @Test
    public void testMarketDataOHLC_Success() throws SmartAPIException, IOException {
        String url = routes.get("api.market.data");
        JSONObject params = getMarketDataRequest("OHLC");
        when(smartAPIRequestHandler.postRequest(eq(this.apiKey), eq(url), eq(params), eq(this.accessToken))).thenReturn(createMarketDataResponse());
        try {
            JSONObject response = smartAPIRequestHandler.postRequest(this.apiKey, url, params, this.accessToken);
            JSONObject data = response.getJSONObject("data");
            assertNotNull(data);
        } catch (SmartAPIException ex) {
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

    // Testing market data failure for OHLC payload
    @Test(expected = SmartAPIException.class)
    public void testMarketDataOHLC_Failure() throws SmartAPIException, IOException {
        // Stub the postRequest method
        String url = routes.get("api.market.data");
        JSONObject params = getMarketDataRequest("OHLC");
        when(smartAPIRequestHandler.postRequest(eq(this.apiKey), eq(url), eq(params), eq(this.accessToken)))
                .thenThrow(new SmartAPIException("API request failed"));
        try {
            JSONObject response = smartAPIRequestHandler.postRequest(apiKey, url, params, accessToken);
            response.getJSONObject("data");
        } catch (SmartAPIException ex) {
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

    private JSONObject getMarketDataRequest(String mode) {
        JSONObject payload = new JSONObject();
        payload.put("mode", mode);
        JSONObject exchangeTokens = new JSONObject();
        JSONArray nseTokens = new JSONArray();
        nseTokens.put("3045");
        exchangeTokens.put("NSE", nseTokens);
        payload.put("exchangeTokens", exchangeTokens);
        return payload;
    }

    public static JSONObject createIndividualOrderResponse() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", true);
        jsonObject.put("message", "SUCCESS");
        jsonObject.put("errorcode", "");

        JSONObject dataObject = new JSONObject();
        dataObject.put("variety", "NORMAL");
        dataObject.put("ordertype", "LIMIT");
        dataObject.put("producttype", "DELIVERY");
        dataObject.put("duration", "DAY");
        dataObject.put("price", 15);
        dataObject.put("triggerprice", 0);
        dataObject.put("quantity", "1");
        dataObject.put("disclosedquantity", "0");
        dataObject.put("squareoff", 0);
        dataObject.put("stoploss", 0);
        dataObject.put("trailingstoploss", 0);
        dataObject.put("tradingsymbol", "YESBANK-EQ");
        dataObject.put("transactiontype", "BUY");
        dataObject.put("exchange", "NSE");
        dataObject.put("symboltoken", "11915");
        dataObject.put("instrumenttype", "");
        dataObject.put("strikeprice", -1);
        dataObject.put("optiontype", "");
        dataObject.put("expirydate", "");
        dataObject.put("lotsize", "1");
        dataObject.put("cancelsize", "0");
        dataObject.put("averageprice", 0);
        dataObject.put("filledshares", "0");
        dataObject.put("unfilledshares", "1");
        dataObject.put("orderid", "231009000001039");
        dataObject.put("text", "Invalid User Id");
        dataObject.put("status", "rejected");
        dataObject.put("orderstatus", "rejected");
        dataObject.put("updatetime", "09-Oct-2023 17:39:28");
        dataObject.put("exchtime", "");
        dataObject.put("exchorderupdatetime", "");
        dataObject.put("fillid", "");
        dataObject.put("filltime", "");
        dataObject.put("parentorderid", "");
        dataObject.put("ordertag", ".test");
        dataObject.put("uniqueorderid", "c7db6526-3f32-47c3-a41e-0e5cb6aad365");

        jsonObject.put("data", dataObject);
        return jsonObject;
    }

    @Test
    public void testIndividualOrder_Success() throws SmartAPIException, IOException {
        String url = routes.get("api.individual.order");
        when(smartAPIRequestHandler.getRequest(this.apiKey, url, this.accessToken)).thenReturn(createIndividualOrderResponse());
        try {
            JSONObject response = smartAPIRequestHandler.getRequest(this.apiKey, url, this.accessToken);
            JSONObject data = response.getJSONObject("data");
            assertNotNull(data);
        } catch (SmartAPIException ex) {
            log.error("{} while getting individual order {}", SMART_API_EXCEPTION_OCCURRED, ex.toString());
            throw new SmartAPIException(String.format("%s in getting individual order %s", SMART_API_EXCEPTION_ERROR_MSG, ex));
        }
    }

    // Testing market data failure for OHLC payload
    @Test(expected = SmartAPIException.class)
    public void testIndividualOrder_Failure() throws SmartAPIException, IOException {
        // Stub the postRequest method
        String url = routes.get("api.market.data");
        JSONObject params = getMarketDataRequest("OHLC");
        when(smartAPIRequestHandler.postRequest(eq(this.apiKey), eq(url), eq(params), eq(this.accessToken)))
                .thenThrow(new SmartAPIException("API request failed"));
        try {
            JSONObject response = smartAPIRequestHandler.postRequest(apiKey, url, params, accessToken);
            response.getJSONObject("data");
        } catch (SmartAPIException ex) {
            log.error("{} while getting individual order {}", SMART_API_EXCEPTION_OCCURRED, ex.toString());
            throw new SmartAPIException(String.format("%s in getting individual order %s", SMART_API_EXCEPTION_ERROR_MSG, ex));
        }
    }
    
}