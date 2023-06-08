package com.angelbroking.smartapi.smartstream.models;

import com.angelbroking.smartapi.utils.ByteUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;

import static com.angelbroking.smartapi.utils.Constants.AVG_TRADED_PRICE_OFFSET;
import static com.angelbroking.smartapi.utils.Constants.CLOSE_PRICE_OFFSET;
import static com.angelbroking.smartapi.utils.Constants.EXCHANGE_FEED_TIME_OFFSET;
import static com.angelbroking.smartapi.utils.Constants.EXCHANGE_TYPE;
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

@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class SnapQuote {

	public static final int PACKET_SIZE_IN_BYTES = 379;

    private byte subscriptionMode;
    private byte exchangeType;
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
        this.exchangeType = buffer.get(EXCHANGE_TYPE);
        this.token = ByteUtils.getTokenID(buffer);
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
        this.upperCircuit = buffer.getLong(UPPER_CIRCUIT_OFFSET);
        this.lowerCircuit = buffer.getLong(LOWER_CIRCUIT_OFFSET);
        this.yearlyHighPrice = buffer.getLong(YEARLY_HIGH_PRICE_OFFSET);
        this.yearlyLowPrice = buffer.getLong(YEARLY_LOW_PRICE_OFFSET);
        this.bestFiveBuy = ByteUtils.getBestFiveBuyData(buffer);
        this.bestFiveSell = ByteUtils.getBestFiveSellData(buffer);

    }


}
