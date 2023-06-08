package com.angelbroking.smartapi.smartstream.models;

import com.angelbroking.smartapi.utils.ByteUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;

import static com.angelbroking.smartapi.utils.Constants.EXCHANGE_FEED_TIME_OFFSET;
import static com.angelbroking.smartapi.utils.Constants.EXCHANGE_TYPE;
import static com.angelbroking.smartapi.utils.Constants.LAST_TRADED_PRICE_OFFSET;
import static com.angelbroking.smartapi.utils.Constants.SEQUENCE_NUMBER_OFFSET;
import static com.angelbroking.smartapi.utils.Constants.SUBSCRIPTION_MODE;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class LTP {
    public static final int PACKET_SIZE_IN_BYTES = 51;

	private byte subscriptionMode;
	private byte exchangeType;
    private TokenID token;
    private long sequenceNumber;
    private long exchangeFeedTimeEpochMillis;
    private long lastTradedPrice;



    public LTP(ByteBuffer buffer) {
        this.subscriptionMode = buffer.get(SUBSCRIPTION_MODE);
        this.exchangeType = buffer.get(EXCHANGE_TYPE);
		this.token = ByteUtils.getTokenID(buffer);
        this.sequenceNumber = buffer.getLong(SEQUENCE_NUMBER_OFFSET);
        this.exchangeFeedTimeEpochMillis = buffer.getLong(EXCHANGE_FEED_TIME_OFFSET);
        this.lastTradedPrice = buffer.getLong(LAST_TRADED_PRICE_OFFSET);
    }

}
