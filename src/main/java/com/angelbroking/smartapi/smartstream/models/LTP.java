package com.angelbroking.smartapi.smartstream.models;

import static com.angelbroking.smartapi.utils.Constants.EXCHANGE_FEED_TIME_OFFSET;
import static com.angelbroking.smartapi.utils.Constants.LAST_TRADED_PRICE_OFFSET;
import static com.angelbroking.smartapi.utils.Constants.SEQUENCE_NUMBER_OFFSET;
import static com.angelbroking.smartapi.utils.Constants.SUBSCRIPTION_MODE;

import java.nio.ByteBuffer;

import com.angelbroking.smartapi.utils.ByteUtils;

// import lombok.Getter; // Removed
// import lombok.Setter; // Removed
public class LTP {
	private byte subscriptionMode;
	private ExchangeType exchangeType;
	private TokenID token;
	private long sequenceNumber;
	private long exchangeFeedTimeEpochMillis;
	private long lastTradedPrice;

	public LTP(ByteBuffer buffer) {
        this.subscriptionMode = buffer.get(SUBSCRIPTION_MODE);
		this.token = ByteUtils.getTokenID(buffer);
		this.exchangeType = this.token.getExchangeType();
        this.sequenceNumber = buffer.getLong(SEQUENCE_NUMBER_OFFSET);
        this.exchangeFeedTimeEpochMillis = buffer.getLong(EXCHANGE_FEED_TIME_OFFSET);
        this.lastTradedPrice = buffer.getLong(LAST_TRADED_PRICE_OFFSET);
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
}
