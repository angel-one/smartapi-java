package com.angelbroking.smartapi.models;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TradeLog {
    private String action; // "BUY" or "SELL"
    private Date timestamp;
    private int quantity;
    private double price;
    private double value; // quantity * price
    private double charges; // Transaction charges, if applicable

    public TradeLog(String action, Date timestamp, int quantity, double price, double charges) {
        this.action = action;
        this.timestamp = timestamp;
        this.quantity = quantity;
        this.price = price;
        this.value = quantity * price;
        this.charges = charges; // Store transaction charges if applicable
    }

    public String getAction() {
        return action;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public double getValue() {
        return value;
    }

    public double getCharges() {
        return charges; // Getter for transaction charges
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return "TradeLog{" + "action='" + action + '\'' + ", timestamp=" + sdf.format(timestamp) + ", quantity=" + quantity + ", price=" + price + ", value=" + value + ", charges=" + String.format("%.4f", charges) + '}';
    }
}