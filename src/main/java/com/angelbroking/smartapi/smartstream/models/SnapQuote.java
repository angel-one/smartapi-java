package com.angelbroking.smartapi.smartstream.models;

import static com.angelbroking.smartapi.utils.Constants.AVG_TRADED_PRICE_OFFSET;
import static com.angelbroking.smartapi.utils.Constants.CLOSE_PRICE_OFFSET;
import static com.angelbroking.smartapi.utils.Constants.EXCHANGE_FEED_TIME_OFFSET;
import static com.angelbroking.smartapi.utils.Constants.HIGH_PRICE_OFFSET;
import static com.angelbroking.smartapi.utils.Constants.LAST_TRADED_PRICE_OFFSET;
import static com.angelbroking.smartapi.utils.Constants.LAST_TRADED_QTY_OFFSET;
import static com.angelbroking.smartapi.utils.Constants.LAST_TRADED_TIMESTAMP_OFFSET;
import static com.angelbroking.smartapi.utils.Constants.LOWER_CIRCUIT_OFFSET;
import static com.angelbroking.smartapi.utils.Constants.LOW_PRICE_OFFSET;
import static com.angelbroking.smartapi.utils.Constants.OPEN_INTEREST_CHANGE_PERC_OFFSET;
import static com.angelbroking.smartapi.utils.Constants.OPEN_INTEREST_OFFSET;
import static com.angelbroking.smartapi.utils.Constants.OPEN_PRICE_OFFSET;
import static com.angelbroking.smartapi.utils.Constants.SEQUENCE_NUMBER_OFFSET;
import static com.angelbroking.smartapi.utils.Constants.SUBSCRIPTION_MODE;
import static com.angelbroking.smartapi.utils.Constants.TOTAL_BUY_QTY_OFFSET;
import static com.angelbroking.smartapi.utils.Constants.TOTAL_SELL_QTY_OFFSET;
import static com.angelbroking.smartapi.utils.Constants.UPPER_CIRCUIT_OFFSET;
import static com.angelbroking.smartapi.utils.Constants.VOLUME_TRADED_TODAY_OFFSET;
import static com.angelbroking.smartapi.utils.Constants.YEARLY_HIGH_PRICE_OFFSET;
import static com.angelbroking.smartapi.utils.Constants.YEARLY_LOW_PRICE_OFFSET;

import java.nio.ByteBuffer;

import com.angelbroking.smartapi.utils.ByteUtils;

// import lombok.Getter; // Removed
// import lombok.Setter; // Removed
public class SnapQuote {
	private byte subscriptionMode;
    private ExchangeType exchangeType;
	private TokenID token;
	private long sequenceNumber;
	private long exchangeFeedTimeEpochMillis;
	private long lastTradedPrice;
	private long lastTradedQty;
	private long avgTradedPrice;
	private long volumeTradedToday;
	private double totalBuyQty;
	private double totalSellQty;
	private long openPrice;
	private long highPrice;
	private long lowPrice;
	private long closePrice;
	private long lastTradedTimestamp = 0;
	private long openInterest = 0;
	private double openInterestChangePerc = 0;
	private SmartApiBBSInfo[] bestFiveBuy;
	private SmartApiBBSInfo[] bestFiveSell;
	private long upperCircuit = 0;
	private long lowerCircuit = 0;
	private long yearlyHighPrice = 0;
	private long yearlyLowPrice = 0;

	public SnapQuote(ByteBuffer buffer) {
        this.subscriptionMode = buffer.get(SUBSCRIPTION_MODE);
        this.token = ByteUtils.getTokenID(buffer);
        this.exchangeType = this.token.getExchangeType();
        this.sequenceNumber = buffer.getLong(SEQUENCE_NUMBER_OFFSET);
        this.exchangeFeedTimeEpochMillis = buffer.getLong(EXCHANGE_FEED_TIME_OFFSET);
        this.lastTradedPrice = buffer.getLong(LAST_TRADED_PRICE_OFFSET);
        this.lastTradedQty = buffer.getLong(LAST_TRADED_QTY_OFFSET);
        this.avgTradedPrice = buffer.getLong(AVG_TRADED_PRICE_OFFSET);
        this.volumeTradedToday = buffer.getLong(VOLUME_TRADED_TODAY_OFFSET);
        this.totalBuyQty = buffer.getDouble(TOTAL_BUY_QTY_OFFSET);
        this.totalSellQty = buffer.getDouble(TOTAL_SELL_QTY_OFFSET);
        this.openPrice = buffer.getLong(OPEN_PRICE_OFFSET);
        this.highPrice = buffer.getLong(HIGH_PRICE_OFFSET);
        this.lowPrice = buffer.getLong(LOW_PRICE_OFFSET);
        this.closePrice = buffer.getLong(CLOSE_PRICE_OFFSET);
        this.lastTradedTimestamp = buffer.getLong(LAST_TRADED_TIMESTAMP_OFFSET);
        this.openInterest = buffer.getLong(OPEN_INTEREST_OFFSET);
        this.openInterestChangePerc = buffer.getDouble(OPEN_INTEREST_CHANGE_PERC_OFFSET);
        this.bestFiveBuy = ByteUtils.getBestFiveBuyData(buffer);
        this.bestFiveSell = ByteUtils.getBestFiveSellData(buffer);
        this.upperCircuit = buffer.getLong(UPPER_CIRCUIT_OFFSET);
        this.lowerCircuit = buffer.getLong(LOWER_CIRCUIT_OFFSET);
        this.yearlyHighPrice = buffer.getLong(YEARLY_HIGH_PRICE_OFFSET);
        this.yearlyLowPrice = buffer.getLong(YEARLY_LOW_PRICE_OFFSET);
    }

    // Manually added Getters and Setters
    public byte getSubscriptionMode() {
        return subscriptionMode;
    }

    public void setSubscriptionMode(byte subscriptionMode) {
        this.subscriptionMode = subscriptionMode;
    }

    public ExchangeType getExchangeType() {
        return exchangeType;
    }

    public void setExchangeType(ExchangeType exchangeType) {
        this.exchangeType = exchangeType;
    }

    public TokenID getToken() {
        return token;
    }

    public void setToken(TokenID token) {
        this.token = token;
    }

    public long getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public long getExchangeFeedTimeEpochMillis() {
        return exchangeFeedTimeEpochMillis;
    }

    public void setExchangeFeedTimeEpochMillis(long exchangeFeedTimeEpochMillis) {
        this.exchangeFeedTimeEpochMillis = exchangeFeedTimeEpochMillis;
    }

    public long getLastTradedPrice() {
        return lastTradedPrice;
    }

    public void setLastTradedPrice(long lastTradedPrice) {
        this.lastTradedPrice = lastTradedPrice;
    }

    public long getLastTradedQty() {
        return lastTradedQty;
    }

    public void setLastTradedQty(long lastTradedQty) {
        this.lastTradedQty = lastTradedQty;
    }

    public long getAvgTradedPrice() {
        return avgTradedPrice;
    }

    public void setAvgTradedPrice(long avgTradedPrice) {
        this.avgTradedPrice = avgTradedPrice;
    }

    public long getVolumeTradedToday() {
        return volumeTradedToday;
    }

    public void setVolumeTradedToday(long volumeTradedToday) {
        this.volumeTradedToday = volumeTradedToday;
    }

    public double getTotalBuyQty() {
        return totalBuyQty;
    }

    public void setTotalBuyQty(double totalBuyQty) {
        this.totalBuyQty = totalBuyQty;
    }

    public double getTotalSellQty() {
        return totalSellQty;
    }

    public void setTotalSellQty(double totalSellQty) {
        this.totalSellQty = totalSellQty;
    }

    public long getOpenPrice() { return openPrice; }
    public void setOpenPrice(long openPrice) { this.openPrice = openPrice; }
    public long getHighPrice() { return highPrice; }
    public void setHighPrice(long highPrice) { this.highPrice = highPrice; }
    public long getLowPrice() { return lowPrice; }
    public void setLowPrice(long lowPrice) { this.lowPrice = lowPrice; }
    public long getClosePrice() { return closePrice; }
    public void setClosePrice(long closePrice) { this.closePrice = closePrice; }
    public long getLastTradedTimestamp() { return lastTradedTimestamp; }
    public void setLastTradedTimestamp(long lastTradedTimestamp) { this.lastTradedTimestamp = lastTradedTimestamp; }
    public long getOpenInterest() { return openInterest; }
    public void setOpenInterest(long openInterest) { this.openInterest = openInterest; }
    public double getOpenInterestChangePerc() { return openInterestChangePerc; }
    public void setOpenInterestChangePerc(double openInterestChangePerc) { this.openInterestChangePerc = openInterestChangePerc; }
    public SmartApiBBSInfo[] getBestFiveBuy() { return bestFiveBuy; }
    public void setBestFiveBuy(SmartApiBBSInfo[] bestFiveBuy) { this.bestFiveBuy = bestFiveBuy; }
    public SmartApiBBSInfo[] getBestFiveSell() { return bestFiveSell; }
    public void setBestFiveSell(SmartApiBBSInfo[] bestFiveSell) { this.bestFiveSell = bestFiveSell; }
    public long getUpperCircuit() { return upperCircuit; }
    public void setUpperCircuit(long upperCircuit) { this.upperCircuit = upperCircuit; }
    public long getLowerCircuit() { return lowerCircuit; }
    public void setLowerCircuit(long lowerCircuit) { this.lowerCircuit = lowerCircuit; }
    public long getYearlyHighPrice() { return yearlyHighPrice; }
    public void setYearlyHighPrice(long yearlyHighPrice) { this.yearlyHighPrice = yearlyHighPrice; }
    public long getYearlyLowPrice() { return yearlyLowPrice; }
    public void setYearlyLowPrice(long yearlyLowPrice) { this.yearlyLowPrice = yearlyLowPrice; }
}
