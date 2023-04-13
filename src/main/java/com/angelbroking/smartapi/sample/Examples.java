package com.angelbroking.smartapi.sample;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.angelbroking.smartapi.http.SmartAPIRequestHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import com.angelbroking.smartapi.SmartConnect;
import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import com.angelbroking.smartapi.models.Gtt;
import com.angelbroking.smartapi.models.GttParams;
import com.angelbroking.smartapi.models.Order;
import com.angelbroking.smartapi.models.OrderParams;
import com.angelbroking.smartapi.models.User;
import com.angelbroking.smartapi.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
public class Examples {

	private static final Logger logger = LoggerFactory.getLogger(Examples.class);

	public void getProfile(SmartConnect smartConnect) throws SmartAPIException {
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

	/** Place order. */
	public Order placeOrder(SmartConnect smartConnect) throws SmartAPIException, IOException {

		OrderParams orderParams = new OrderParams();
		orderParams.variety = Constants.VARIETY_STOPLOSS;
		orderParams.quantity = 323;
		orderParams.symbolToken = "1660";
		orderParams.exchange = Constants.EXCHANGE_NSE;
		orderParams.orderType = Constants.ORDER_TYPE_STOPLOSS_LIMIT;
		orderParams.tradingSymbol = "ITC-EQ";
		orderParams.productType = Constants.PRODUCT_INTRADAY;
		orderParams.duration = Constants.DURATION_DAY;
		orderParams.transactionType = Constants.TRANSACTION_TYPE_BUY;
		orderParams.price = 122.2;
		orderParams.triggerPrice = "209";

		Order order = smartConnect.placeOrder(orderParams, "STOPLOSS");
		logger.debug("placeOrder: {}", order);

		return order;
	}

	/** Modify order. */
	public Order modifyOrder(SmartConnect smartConnect, Order orderInput) throws SmartAPIException {
		// Order modify request will return order model which will contain only

		OrderParams orderParams = new OrderParams();
		orderParams.variety = Constants.VARIETY_STOPLOSS;
		orderParams.quantity = 324;
		orderParams.symbolToken = "1660";
		orderParams.exchange = Constants.EXCHANGE_NSE;
		orderParams.orderType = Constants.ORDER_TYPE_STOPLOSS_LIMIT;
		orderParams.tradingSymbol = "ITC-EQ";
		orderParams.productType = Constants.PRODUCT_INTRADAY;
		orderParams.duration = Constants.DURATION_DAY;
		orderParams.transactionType = Constants.TRANSACTION_TYPE_BUY;
		orderParams.price = 122.2;
		orderParams.triggerPrice = "209";
		Order order = smartConnect.modifyOrder(orderInput.orderId, orderParams, "STOPLOSS");

		logger.debug("modifyOrder {}",order);
		return order;

	}

	/**
	 * Cancel an order
	 *
	 * @return
	 */
	public Order cancelOrder(SmartConnect smartConnect, Order modifyOrder) throws SmartAPIException {
		// Order modify request will return order model which will contain only
		// order_id.
		// Cancel order will return order model which will only have orderId.
		Order order = smartConnect.cancelOrder(modifyOrder.getOrderId(), Constants.VARIETY_STOPLOSS);
		logger.debug("cancelOrder {}",order);
		return order;
	}

	/** Get order details */
	public void getOrder(SmartConnect smartConnect) {
		JSONObject orders = smartConnect.getOrderHistory();
		logger.debug("getOrder {}",orders);
	}

	/**
	 * Get last price for multiple instruments at once. USers can either pass
	 * exchange with tradingsymbol or instrument token only. For example {NSE:NIFTY
	 * 50, BSE:SENSEX} or {256265, 265}
	 */
	public void getLTP(SmartConnect smartConnect){
		String exchange = "NSE";
		String symboltoken = "3045";
		JSONObject ltpData = smartConnect.getLTP(exchange, Constants.SYMBOL_SBINEQ, symboltoken);
		logger.debug("getLTP {}",ltpData);
	}

	/** Get tradebook */
	public void getTrades(SmartConnect smartConnect) throws SmartAPIException {
		// Returns tradebook.
		JSONObject trades = smartConnect.getTrades();

		logger.debug("getTrades {}",trades);

	}

	/** Get RMS */
	public void getRMS(SmartConnect smartConnect) throws SmartAPIException {
		// Returns RMS.
		JSONObject response = smartConnect.getRMS();
		logger.debug("getRMS {}",response);
	}

	/** Get Holdings */
	public void getHolding(SmartConnect smartConnect) throws SmartAPIException {
		// Returns Holding.
		JSONObject response = smartConnect.getHolding();
		logger.debug("getHolding {}",response);
	}

	/** Get Position */
	public void getPosition(SmartConnect smartConnect) throws SmartAPIException {
		// Returns Position.
		JSONObject response = smartConnect.getPosition();
		logger.debug("getPosition {}",response);
	}

	/** convert Position */
	public void convertPosition(SmartConnect smartConnect) throws SmartAPIException {

		JSONObject requestObejct = new JSONObject();
		requestObejct.put("exchange", "NSE");
		requestObejct.put("oldproducttype", "DELIVERY");
		requestObejct.put("newproducttype", Constants.MARGIN);
		requestObejct.put("tradingsymbol", Constants.SYMBOL_SBINEQ);
		requestObejct.put("transactiontype", "BUY");
		requestObejct.put("quantity", 1);
		requestObejct.put("type", "DAY");

		JSONObject response = smartConnect.convertPosition(requestObejct);
		logger.debug("convertPosition {}",response);
	}

	/** Create Gtt Rule */
	public String createRule(SmartConnect smartConnect) {
		GttParams gttParams = new GttParams();

		gttParams.tradingSymbol = Constants.SYMBOL_SBINEQ;
		gttParams.symbolToken = "3045";
		gttParams.exchange = "NSE";
		gttParams.productType = Constants.MARGIN;
		gttParams.transactionType = "BUY";
		gttParams.price = 100000.01;
		gttParams.qty = 10;
		gttParams.disclosedQty = 10;
		gttParams.triggerPrice = 20000.1;
		gttParams.timePeriod = 300;

		return smartConnect.gttCreateRule(gttParams);
	}

	/** Modify Gtt Rule */
	public String modifyRule(SmartConnect smartConnect, String ruleID){
		GttParams gttParams = new GttParams();

		gttParams.tradingSymbol = Constants.SYMBOL_SBINEQ;
		gttParams.symbolToken = "3045";
		gttParams.exchange = "NSE";
		gttParams.productType = Constants.MARGIN;
		gttParams.transactionType = "BUY";
		gttParams.price = 100000.1;
		gttParams.qty = 11;
		gttParams.disclosedQty = 11;
		gttParams.triggerPrice = 20000.1;
		gttParams.timePeriod = 300;



		return smartConnect.gttModifyRule(Integer.valueOf(ruleID), gttParams);
	}

	/** Cancel Gtt Rule */
	public void cancelRule(SmartConnect smartConnect, String modifyRuleID) {

		String symboltoken = "3045";
		String exchange = "NSE";

		Gtt gtt = smartConnect.gttCancelRule(Integer.valueOf(modifyRuleID), symboltoken, exchange);
		logger.debug("cancelRule {}",gtt);

	}

	/** Gtt Rule Details */
	public void ruleDetails(SmartConnect smartConnect, String modifyRuleID) {


		JSONObject gtt = smartConnect.gttRuleDetails(Integer.valueOf(modifyRuleID));
		logger.debug("ruleDetails {}",gtt);
	}

	/** Gtt Rule Lists */
	@SuppressWarnings("serial")
	public void ruleList(SmartConnect smartConnect){

		List<String> status = new ArrayList<>();
		status.add("NEW");
		status.add("CANCELLED");
		status.add("ACTIVE");
		status.add("SENTTOEXCHANGE");
		status.add("FORALL");


		Integer page = 1;
		Integer count = 10;

		JSONArray gtt = smartConnect.gttRuleList(status, page, count);
		logger.debug("ruleList {}",gtt);

	}

	/** Historic Data */
	public void getCandleData(SmartConnect smartConnect){

		JSONObject obj = new JSONObject();
		obj.put("todate", "2021-03-09 09:20");
		obj.put("exchange", "NSE");
		obj.put("interval", "ONE_MINUTE");
		obj.put("symboltoken", "3045");
		obj.put("fromdate", "2021-03-08 09:00");

		String response = smartConnect.candleData(obj);
		logger.debug("getCandleData {}",response);
	}

	/** Logout user. */
	public void logout(SmartConnect smartConnect) throws SmartAPIException {
		/** Logout user and kill session. */
		JSONObject jsonObject = smartConnect.logout();
		logger.debug("logout {}",jsonObject);
	}

}
