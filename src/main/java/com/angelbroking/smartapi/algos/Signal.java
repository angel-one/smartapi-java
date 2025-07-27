package com.angelbroking.smartapi.algos;

import java.util.Date;

public class Signal {
    private Date timestamp;
    private String type; // e.g., "BUY", "SELL", "HOLD"
    private double price;
    private int quantity;

    public Signal(Date timestamp, String type, double price, int quantity) {
        this.timestamp = timestamp;
        this.type = type;
        this.price = price;
        this.quantity = quantity;
    }

    // Getters
    public Date getTimestamp() {
        return timestamp;
    }

    public String getType() {
        return type;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return "Signal{" +
                "timestamp=" + timestamp +
                ", type='" + type + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }
}