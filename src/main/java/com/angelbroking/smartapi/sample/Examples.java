package com.angelbroking.smartapi.sample;

import com.angelbroking.smartapi.SmartConnect;
import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import com.angelbroking.smartapi.http.response.HttpResponse;
import com.angelbroking.smartapi.models.GttParams;
import com.angelbroking.smartapi.models.OrderParams;
import com.angelbroking.smartapi.dto.StockHistoryRequestDTO;
import com.angelbroking.smartapi.dto.TradeRequestDTO;
import com.angelbroking.smartapi.models.User;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.angelbroking.smartapi.utils.Constants.MARGIN;
import static com.angelbroking.smartapi.utils.Constants.SYMBOL_SBINEQ;


@Slf4j
public class Examples {

    public void getProfile(SmartConnect smartConnect) throws SmartAPIException, IOException {

        User profile = smartConnect.getProfile();
        log.info(profile.toString());
    }

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
    public HttpResponse placeOrder(SmartConnect smartConnect) throws SmartAPIException, IOException {

        OrderParams orderParams = new OrderParams();
        orderParams.setDuration("DAY");
        orderParams.setQuantity(1);
        orderParams.setVariety("NORMAL");
        orderParams.setPrice(19500.0);
        orderParams.setTradingSymbol("SBIN-EQ");
        orderParams.setExchange("NSE");
        orderParams.setTransactionType("BUY");
        orderParams.setSymbolToken("3045");
        orderParams.setProductType("INTRADAY");
        orderParams.setOrderType("LIMIT");
        HttpResponse order = smartConnect.placeOrder(orderParams, "NORMAL");
        log.info("placeOrder: {}", order.toString());

        return order;
    }

    /**
     * Modify order.
     */
    public HttpResponse modifyOrder(SmartConnect smartConnect, String orderid) throws SmartAPIException, IOException {
        // Order modify request will return order model which will contain only

        OrderParams orderParams = new OrderParams();
        orderParams.setDuration("DAY");
        orderParams.setQuantity(2);
        orderParams.setVariety("NORMAL");
        orderParams.setPrice(19500.0);
        orderParams.setTradingSymbol("SBIN-EQ");
        orderParams.setExchange("NSE");
        orderParams.setTransactionType("BUY");
        orderParams.setSymbolToken("3045");
        orderParams.setProductType("INTRADAY");
        orderParams.setOrderType("LIMIT");
        HttpResponse order = smartConnect.modifyOrder(orderid, orderParams, "NORMAL");

        log.info("modifyOrder {}", order);
        return order;

    }

    /**
     * Cancel an order
     *
     * @return order
     */
    public HttpResponse cancelOrder(SmartConnect smartConnect, String orderid) throws SmartAPIException, IOException {
        // Order modify request will return order model which will contain only
        // order_id.
        // Cancel order will return order model which will only have orderId.
        HttpResponse order = smartConnect.cancelOrder(orderid, "NORMAL");
        log.info("cancelOrder {}", order);
        return order;
    }

    /**
     * Get order details
     */
    public void getOrder(SmartConnect smartConnect) throws SmartAPIException, IOException {
        HttpResponse orders = smartConnect.getOrderHistory();
        log.info("getOrder {}", orders);
    }

    /**
     * Get last price for multiple instruments at once. USers can either pass
     * exchange with tradingsymbol or instrument token only. For example {NSE:NIFTY
     * 50, BSE:SENSEX} or {256265, 265}
     */
    public void getLTP(SmartConnect smartConnect) throws SmartAPIException, IOException {

        String exchange = "NSE";
        String symboltoken = "3045";
        HttpResponse ltpData = smartConnect.getLTP(exchange, SYMBOL_SBINEQ, symboltoken);
        log.info("getLTP {}", ltpData);
    }

    /**
     * Get tradebook
     */
    public void getTrades(SmartConnect smartConnect) throws SmartAPIException, IOException {
        HttpResponse trades = smartConnect.getTrades();
        log.info("getTrades {}", trades);

    }

    /**
     * Get RMS
     */
    public void getRMS(SmartConnect smartConnect) throws SmartAPIException, IOException {
        HttpResponse response = smartConnect.getRMS();
        log.info("getRMS {}", response);
    }

    /**
     * Get Holdings
     */
    public void getHolding(SmartConnect smartConnect) throws SmartAPIException, IOException {
        HttpResponse response = smartConnect.getHolding();
        log.info("getHolding {}", response);
    }

    /**
     * Get Position
     */
    public void getPosition(SmartConnect smartConnect) throws SmartAPIException, IOException {
        HttpResponse response = smartConnect.getPosition();
        log.info("getPosition {}", response);
    }

    /**
     * convert Position
     */
    public void convertPosition(SmartConnect smartConnect) throws SmartAPIException, IOException {

        TradeRequestDTO requestDTO = new TradeRequestDTO();
        requestDTO.setExchange("NSE");
        requestDTO.setOldProductType("DELIVERY");
        requestDTO.setNewProductType("MARGIN");
        requestDTO.setTradingSymbol(SYMBOL_SBINEQ);
        requestDTO.setTransactionType("BUY");
        requestDTO.setQuantity(1);
        requestDTO.setType("DAY");


        HttpResponse response = smartConnect.convertPosition(requestDTO);
        log.info("convertPosition {}", response);
    }

    /**
     * Create Gtt Rule
     */
    public HttpResponse createRule(SmartConnect smartConnect) throws SmartAPIException, IOException {
        GttParams gttParams = new GttParams();

        gttParams.setTradingSymbol(SYMBOL_SBINEQ);
        gttParams.setSymbolToken("3045");
        gttParams.setExchange("NSE");
        gttParams.setProductType(MARGIN);
        gttParams.setTransactionType("BUY");
        gttParams.setPrice(100000.01);
        gttParams.setQty(10);
        gttParams.setDisclosedQty(10);
        gttParams.setTriggerPrice(20000.1);
        gttParams.setTimePeriod(300);

        HttpResponse response = smartConnect.gttCreateRule(gttParams);
        log.info("createRule {}", response);
        return response;
    }

    /**
     * Modify Gtt Rule
     */
    public HttpResponse modifyRule(SmartConnect smartConnect, String ruleID) throws SmartAPIException, IOException {
        GttParams gttParams = new GttParams();
        gttParams.setTradingSymbol(SYMBOL_SBINEQ);
        gttParams.setSymbolToken("3045");
        gttParams.setExchange("NSE");
        gttParams.setProductType(MARGIN);
        gttParams.setTransactionType("BUY");
        gttParams.setPrice(100000.1);
        gttParams.setQty(11);
        gttParams.setDisclosedQty(11);
        gttParams.setTriggerPrice(20000.1);
        gttParams.setTimePeriod(300);

        HttpResponse response = smartConnect.gttModifyRule(Integer.valueOf(ruleID), gttParams);
        log.info("modifyRule {}", response);
        return response;
    }

    /**
     * Cancel Gtt Rule
     */
    public void cancelRule(SmartConnect smartConnect, String modifyRuleID) throws SmartAPIException, IOException {

        String symboltoken = "3045";
        String exchange = "NSE";

        HttpResponse gtt = smartConnect.gttCancelRule(Integer.valueOf(modifyRuleID), symboltoken, exchange);
        log.info("cancelRule {}", gtt);

    }

    /**
     * Gtt Rule Details
     */
    public void ruleDetails(SmartConnect smartConnect, String modifyRuleID) throws SmartAPIException, IOException {


        HttpResponse gtt = smartConnect.gttRuleDetails(Integer.valueOf(modifyRuleID));
        log.info("ruleDetails {}", gtt);
    }

    /**
     * Gtt Rule Lists
     */
    public void ruleList(SmartConnect smartConnect) throws SmartAPIException, IOException {

        List<String> status = new ArrayList<>();
        status.add("NEW");
        status.add("CANCELLED");
        status.add("ACTIVE");
        status.add("SENTTOEXCHANGE");
        status.add("FORALL");


        Integer page = 1;
        Integer count = 10;

        HttpResponse gtt = smartConnect.gttRuleList(status, page, count);
        log.info("ruleList {}", gtt);

    }

    /**
     * Historic Data
     */
    public void getCandleData(SmartConnect smartConnect) throws SmartAPIException, IOException {

        StockHistoryRequestDTO requestDTO = new StockHistoryRequestDTO();
        requestDTO.setToDate("2021-03-09 09:20");
        requestDTO.setExchange("NSE");
        requestDTO.setInterval("ONE_MINUTE");
        requestDTO.setSymbolToken("3045");
        requestDTO.setFromDate("2021-03-08 09:00");

        HttpResponse response = smartConnect.candleData(requestDTO);
        log.info("getCandleData {}", response);
    }

    /**
     * Logout user.
     */
    public void logout(SmartConnect smartConnect) throws SmartAPIException, IOException {
        HttpResponse httpResponse = smartConnect.logout();
        log.info("logout {}", httpResponse);
    }

}
