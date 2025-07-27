package com.angelbroking.smartapi.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;

public class Candle {
    private Date timestamp;
    private double open;
    private double high;
    private double low;
    private double close;
    private long volume;

    // Angel One API timestamp format: "yyyy-MM-dd'T'HH:mm:ss+05:30" or similar with timezone
    private static final SimpleDateFormat API_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

    public Candle(Date timestamp, double open, double high, double low, double close, long volume) {
        this.timestamp = timestamp;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
    }

    // Constructor to parse from JSONArray (as returned by smartConnect.candleData())
    public Candle(JSONArray candleArray) throws JSONException, ParseException {
        if (candleArray == null || candleArray.length() < 6) {
            throw new IllegalArgumentException("Candle data array must have at least 6 elements.");
        }
        this.timestamp = API_DATE_FORMAT.parse(candleArray.getString(0));
        this.open = candleArray.getDouble(1);
        this.high = candleArray.getDouble(2);
        this.low = candleArray.getDouble(3);
        this.close = candleArray.getDouble(4);
        this.volume = candleArray.getLong(5);
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public double getOpen() {
        return open;
    }

    public double getHigh() {
        return high;
    }

    public double getLow() {
        return low;
    }

    public double getClose() {
        return close;
    }

    public long getVolume() {
        return volume;
    }

    @Override
    public String toString() {
        return "Candle{" +
                "timestamp=" + (timestamp != null ? API_DATE_FORMAT.format(timestamp) : "null") +
                ", open=" + open +
                ", high=" + high +
                ", low=" + low +
                ", close=" + close +
                ", volume=" + volume +
                '}';
    }
}