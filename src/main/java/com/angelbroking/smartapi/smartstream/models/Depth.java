package com.angelbroking.smartapi.smartstream.models;

import com.angelbroking.smartapi.utils.ByteUtils;

import java.nio.ByteBuffer;

import static com.angelbroking.smartapi.utils.Constants.*;

// import lombok.Getter; // Removed
// import lombok.Setter; // Removed
public class Depth {
    private byte subscriptionMode;
    private ExchangeType exchangeType;
    private TokenID token;
    private long exchangeTimeStamp;
    private long packetReceivedTime;
    private BestTwentyData[] bestTwentyBuyData;
    private BestTwentyData[] bestTwentySellData;


    public Depth(ByteBuffer buffer) {
        this.subscriptionMode = buffer.get(SUBSCRIPTION_MODE);
        this.token = ByteUtils.getTokenID(buffer);
        this.exchangeType = this.token.getExchangeType();
        this.exchangeTimeStamp = buffer.getLong(EXCHANGE_TIMESTAMP_FOR_DEPTH20);
        this.packetReceivedTime = buffer.getLong(PACKET_RECEIVED_TIME_FOR_DEPTH20);
        this.bestTwentyBuyData = ByteUtils.getBestTwentyBuyData(buffer);
        this.bestTwentySellData = ByteUtils.getBestTwentySellData(buffer);
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

    public long getExchangeTimeStamp() {
        return exchangeTimeStamp;
    }

    public void setExchangeTimeStamp(long exchangeTimeStamp) {
        this.exchangeTimeStamp = exchangeTimeStamp;
    }

    public long getPacketReceivedTime() {
        return packetReceivedTime;
    }

    public void setPacketReceivedTime(long packetReceivedTime) {
        this.packetReceivedTime = packetReceivedTime;
    }

    public BestTwentyData[] getBestTwentyBuyData() { return bestTwentyBuyData; }
    public void setBestTwentyBuyData(BestTwentyData[] bestTwentyBuyData) { this.bestTwentyBuyData = bestTwentyBuyData; }
    public BestTwentyData[] getBestTwentySellData() { return bestTwentySellData; }
    public void setBestTwentySellData(BestTwentyData[] bestTwentySellData) { this.bestTwentySellData = bestTwentySellData; }
}
