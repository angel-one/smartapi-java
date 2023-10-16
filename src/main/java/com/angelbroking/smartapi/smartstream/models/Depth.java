package com.angelbroking.smartapi.smartstream.models;

import com.angelbroking.smartapi.utils.ByteUtils;
import lombok.Getter;
import lombok.Setter;

import java.nio.ByteBuffer;

import static com.angelbroking.smartapi.utils.Constants.*;

@Getter
@Setter
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
}
