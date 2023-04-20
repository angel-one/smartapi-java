package com.angelbroking.smartapi.sample;

import com.angelbroking.smartapi.SmartConnect;
import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import com.angelbroking.smartapi.models.*;
import com.angelbroking.smartapi.utils.Constants;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class Examples {

    private static final Logger logger = LoggerFactory.getLogger(Examples.class);

    public void getProfile(SmartConnect smartConnect) throws SmartAPIException, IOException {
        User profile = smartConnect.getProfile();
        logger.debug(profile.toString());
    }

    /** CONSTANT Details */

    /* VARIETY */
    /*
     * VARIETY_NORMAL: Normal Order (Regular)
     * VARIETY_AMO: After Market Order
     * VARIETY_STOPLOSS: Stop loss order
     * VARIETY_ROBO: ROBO (Bracket) Order
     */
    /* TRANSACTION TYPE */
    /*
     * TRANSACTION_TYPE_BUY: Buy TRANSACTION_TYPE_SELL: Sell
     */

    /* ORDER TYPE */
    /*
     * ORDER_TYPE_MARKET: Market Order(MKT)
     * ORDER_TYPE_LIMIT: Limit Order(L)
     * ORDER_TYPE_STOPLOSS_LIMIT: Stop Loss Limit Order(SL)
     * ORDER_TYPE_STOPLOSS_MARKET: Stop Loss Market Order(SL-M)
     */

    /* PRODUCT TYPE */
    /*
     * PRODUCT_DELIVERY: Cash & Carry for equity (CNC)
     * PRODUCT_CARRYFORWARD: Normal
     * for futures and options (NRML)
     * PRODUCT_MARGIN: Margin Delivery
     * PRODUCT_INTRADAY: Margin Intraday Squareoff (MIS)
     * PRODUCT_BO: Bracket Order
     * (Only for ROBO)
     */

    /* DURATION */
    /*
     * DURATION_DAY: Valid for a day
     * DURATION_IOC: Immediate or Cancel
     */

    /* EXCHANGE */
    /*
     * EXCHANGE_BSE: BSE Equity
     * EXCHANGE_NSE: NSE Equity
     * EXCHANGE_NFO: NSE Future and Options
     * EXCHANGE_CDS: NSE Currency
     * EXCHANGE_NCDEX: NCDEX Commodity
     * EXCHANGE_MCX: MCX Commodity
     */

    /**
     * Place order.
     */
    public Order placeOrder(SmartConnect smartConnect) throws SmartAPIException, IOException {

        OrderParams orderParams = new OrderParams();
        orderParams.setVariety(Constants.VARIETY_STOPLOSS);
        orderParams.setQuantity(323);
        orderParams.setSymbolToken("1660");
        orderParams.setExchange(Constants.EXCHANGE_NSE);
        orderParams.setOrderType(Constants.ORDER_TYPE_STOPLOSS_LIMIT);
        orderParams.setTradingSymbol("ITC-EQ");
        orderParams.setProductType(Constants.PRODUCT_INTRADAY);
        orderParams.setDuration(Constants.DURATION_DAY);
        orderParams.setTransactionType(Constants.TRANSACTION_TYPE_BUY);
        orderParams.setPrice(122.2);
        orderParams.setSquareOff("0");
        orderParams.setStopLoss("0");
        Order order = smartConnect.placeOrder(orderParams, "STOPLOSS");
        logger.debug("placeOrder: {}", order);

        return order;
    }

    /**
     * Modify order.
     */
    public Order modifyOrder(SmartConnect smartConnect, Order orderInput) throws SmartAPIException, IOException {
        // Order modify request will return order model which will contain only

        OrderParams orderParams = new OrderParams();
        orderParams.setVariety(Constants.VARIETY_STOPLOSS);
        orderParams.setQuantity(324);
        orderParams.setSymbolToken("1660");
        orderParams.setExchange(Constants.EXCHANGE_NSE);
        orderParams.setOrderType(Constants.ORDER_TYPE_STOPLOSS_LIMIT);
        orderParams.setTradingSymbol("ITC-EQ");
        orderParams.setProductType(Constants.PRODUCT_INTRADAY);
        orderParams.setDuration(Constants.DURATION_DAY);
        orderParams.setTransactionType(Constants.TRANSACTION_TYPE_BUY);
        orderParams.setPrice(122.2);
        orderParams.setTriggerPrice("209");
        Order order = smartConnect.modifyOrder(orderInput.getOrderId(), orderParams, "STOPLOSS");

        logger.debug("modifyOrder {}", order);
        return order;

    }

    /**
     * Cancel an order
     *
     * @return
     */
    public Order cancelOrder(SmartConnect smartConnect, Order modifyOrder) throws SmartAPIException, IOException {
        // Order modify request will return order model which will contain only
        // order_id.
        // Cancel order will return order model which will only have orderId.
        Order order = smartConnect.cancelOrder(modifyOrder.getOrderId(), Constants.VARIETY_STOPLOSS);
        logger.debug("cancelOrder {}", order);
        return order;
    }

    /**
     * Get order details
     */
    public void getOrder(SmartConnect smartConnect) throws SmartAPIException, IOException {
        JSONObject orders = smartConnect.getOrderHistory();
        logger.debug("getOrder {}", orders);
    }

    /**
     * Get last price for multiple instruments at once. USers can either pass
     * exchange with tradingsymbol or instrument token only. For example {NSE:NIFTY
     * 50, BSE:SENSEX} or {256265, 265}
     */
    public void getLTP(SmartConnect smartConnect) throws SmartAPIException, IOException {
        String exchange = "NSE";
        String symboltoken = "3045";
        JSONObject ltpData = smartConnect.getLTP(exchange, Constants.SYMBOL_SBINEQ, symboltoken);
        logger.debug("getLTP {}", ltpData);
    }

    /**
     * Get tradebook
     */
    public void getTrades(SmartConnect smartConnect) throws SmartAPIException, IOException {
        // Returns tradebook.
        JSONObject trades = smartConnect.getTrades();

        logger.debug("getTrades {}", trades);

    }

    /**
     * Get RMS
     */
    public void getRMS(SmartConnect smartConnect) throws SmartAPIException, IOException {
        // Returns RMS.
        JSONObject response = smartConnect.getRMS();
        logger.debug("getRMS {}", response);
    }

    /**
     * Get Holdings
     */
    public void getHolding(SmartConnect smartConnect) throws SmartAPIException, IOException {
        // Returns Holding.
        JSONObject response = smartConnect.getHolding();
        logger.debug("getHolding {}", response);
    }

    /**
     * Get Position
     */
    public void getPosition(SmartConnect smartConnect) throws SmartAPIException, IOException {
        // Returns Position.
        JSONObject response = smartConnect.getPosition();
        logger.debug("getPosition {}", response);
    }

    /**
     * convert Position
     */
    public void convertPosition(SmartConnect smartConnect) throws SmartAPIException, IOException {

        JSONObject requestObejct = new JSONObject();
        requestObejct.put("exchange", "NSE");
        requestObejct.put("oldproducttype", "DELIVERY");
        requestObejct.put("newproducttype", Constants.MARGIN);
        requestObejct.put("tradingsymbol", Constants.SYMBOL_SBINEQ);
        requestObejct.put("transactiontype", "BUY");
        requestObejct.put("quantity", 1);
        requestObejct.put("type", "DAY");

        JSONObject response = smartConnect.convertPosition(requestObejct);
        logger.debug("convertPosition {}", response);
    }

    /**
     * Create Gtt Rule
     */
    public String createRule(SmartConnect smartConnect) throws SmartAPIException, IOException {
        GttParams gttParams = new GttParams();

        gttParams.setTradingSymbol(Constants.SYMBOL_SBINEQ);
        gttParams.setSymbolToken("3045");
        gttParams.setExchange("NSE");
        gttParams.setProductType(Constants.MARGIN);
        gttParams.setTransactionType("BUY");
        gttParams.setPrice(100000.01);
        gttParams.setQty(10);
        gttParams.setDisclosedQty(10);
        gttParams.setTriggerPrice(20000.1);
        gttParams.setTimePeriod(300);
        return smartConnect.gttCreateRule(gttParams);
    }

    /**
     * Modify Gtt Rule
     */
    public String modifyRule(SmartConnect smartConnect, String ruleID) throws SmartAPIException, IOException {
        GttParams gttParams = new GttParams();
        gttParams.setTradingSymbol(Constants.SYMBOL_SBINEQ);
        gttParams.setSymbolToken("3045");
        gttParams.setExchange("NSE");
        gttParams.setProductType(Constants.MARGIN);
        gttParams.setTransactionType("BUY");
        gttParams.setPrice(100000.1);
        gttParams.setQty(11);
        gttParams.setDisclosedQty(11);
        gttParams.setTriggerPrice(20000.1);
        gttParams.setTimePeriod(300);
        return smartConnect.gttModifyRule(Integer.valueOf(ruleID), gttParams);
    }

    /**
     * Cancel Gtt Rule
     */
    public void cancelRule(SmartConnect smartConnect, String modifyRuleID) throws SmartAPIException, IOException {

        String symboltoken = "3045";
        String exchange = "NSE";

        Gtt gtt = smartConnect.gttCancelRule(Integer.valueOf(modifyRuleID), symboltoken, exchange);
        logger.debug("cancelRule {}", gtt);

    }

    /**
     * Gtt Rule Details
     */
    public void ruleDetails(SmartConnect smartConnect, String modifyRuleID) throws SmartAPIException, IOException {


        JSONObject gtt = smartConnect.gttRuleDetails(Integer.valueOf(modifyRuleID));
        logger.debug("ruleDetails {}", gtt);
    }

    /**
     * Gtt Rule Lists
     */
    @SuppressWarnings("serial")
    public void ruleList(SmartConnect smartConnect) throws SmartAPIException, IOException {

        List<String> status = new ArrayList<>();
        status.add("NEW");
        status.add("CANCELLED");
        status.add("ACTIVE");
        status.add("SENTTOEXCHANGE");
        status.add("FORALL");


        Integer page = 1;
        Integer count = 10;

        JSONArray gtt = smartConnect.gttRuleList(status, page, count);
        logger.debug("ruleList {}", gtt);

    }

    /**
     * Historic Data
     */
    public void getCandleData(SmartConnect smartConnect) throws SmartAPIException, IOException {

        JSONObject obj = new JSONObject();
        obj.put("todate", "2021-03-09 09:20");
        obj.put("exchange", "NSE");
        obj.put("interval", "ONE_MINUTE");
        obj.put("symboltoken", "3045");
        obj.put("fromdate", "2021-03-08 09:00");

        String response = smartConnect.candleData(obj);
        logger.debug("getCandleData {}", response);
    }

    /**
     * Logout user.
     */
    public void logout(SmartConnect smartConnect) throws SmartAPIException, IOException {
        /** Logout user and kill session. */
        JSONObject jsonObject = smartConnect.logout();
        logger.debug("logout {}", jsonObject);
    }

}
