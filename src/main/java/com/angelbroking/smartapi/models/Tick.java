package com.angelbroking.smartapi.models;

import java.util.Date;

public class Tick {
    private String symbolToken;
    private String tradingSymbol; // Optional
    private String exchange;      // Optional
    private Date timestamp;
    private double ltp; // Last Traded Price
    private double open;
    private double high;
    private double low;
    private double close; // Previous day's close or day's close if available
    private long volume;
    private double bidPrice;
    private long bidQty;
    private double askPrice;
    private long askQty;
    // Add other fields as per Angel One's WebSocket tick data structure
    // e.g., open interest, average traded price, etc.

    public Tick() {}

    // Getters and Setters
    public String getSymbolToken() { return symbolToken; }
    public void setSymbolToken(String symbolToken) { this.symbolToken = symbolToken; }

    public String getTradingSymbol() { return tradingSymbol; }
    public void setTradingSymbol(String tradingSymbol) { this.tradingSymbol = tradingSymbol; }

    public String getExchange() { return exchange; }
    public void setExchange(String exchange) { this.exchange = exchange; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    public double getLtp() { return ltp; }
    public void setLtp(double ltp) { this.ltp = ltp; }

    public double getOpen() { return open; }
    public void setOpen(double open) { this.open = open; }

    public double getHigh() { return high; }
    public void setHigh(double high) { this.high = high; }

    public double getLow() { return low; }
    public void setLow(double low) { this.low = low; }

    public double getClose() { return close; }
    public void setClose(double close) { this.close = close; }

    public long getVolume() { return volume; }
    public void setVolume(long volume) { this.volume = volume; }

    public double getBidPrice() { return bidPrice; }
    public void setBidPrice(double bidPrice) { this.bidPrice = bidPrice; }

    public long getBidQty() { return bidQty; }
    public void setBidQty(long bidQty) { this.bidQty = bidQty; }

    public double getAskPrice() { return askPrice; }
    public void setAskPrice(double askPrice) { this.askPrice = askPrice; }

    public long getAskQty() { return askQty; }
    public void setAskQty(long askQty) { this.askQty = askQty; }
}