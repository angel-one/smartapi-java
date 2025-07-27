package com.angelbroking.smartapi.algos;

import java.util.Date;

public class TradingAction extends Signal {
    private ActionType actionType;
    private String symbolToken;
    private String tradingSymbol;
    private String exchange;
    private double triggerPrice;
    private String orderType;
    private String productType;
    private String variety;
    private String duration;

    // Constructor for HOLD signals
    public TradingAction(Date timestamp, ActionType actionType, String symbolToken) {
        super(timestamp, actionType.toString(), 0, 0); // price and quantity are 0 for HOLD
        this.actionType = actionType;
        this.symbolToken = symbolToken;
        // Initialize other fields to defaults or null as appropriate
        this.orderType = "MARKET"; 
        this.productType = "INTRADAY"; 
        this.variety = "NORMAL";
        this.duration = "DAY";
    }

    // Constructor for BUY/SELL signals with full order details
    public TradingAction(Date timestamp, ActionType actionType, String symbolToken, String tradingSymbol, String exchange, int quantity,
                         double price, double triggerPrice, String orderType, String productType,
                         String variety, String duration) {
        super(timestamp, actionType.toString(), price, quantity); 
        
        this.actionType = actionType;
        this.symbolToken = symbolToken;
        this.tradingSymbol = tradingSymbol;
        this.exchange = exchange;
        this.triggerPrice = triggerPrice;
        this.orderType = orderType;
        this.productType = productType;
        this.variety = variety;
        this.duration = duration;
    }

    public ActionType getActionType() {
        return this.actionType;
    }

    // Getters for additional fields
    public String getSymbolToken() { return symbolToken; }
    public String getTradingSymbol() { return tradingSymbol; }
    public String getExchange() { return exchange; }
    public double getTriggerPrice() { return triggerPrice; }
    public String getOrderType() { return orderType; }
    public String getProductType() { return productType; }
    public String getVariety() { return variety; }
    public String getDuration() { return duration; }
}