package com.angelbroking.smartapi.smartstream.models;

import com.angelbroking.smartapi.utils.ByteUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.ByteBuffer;

import static com.angelbroking.smartapi.utils.Constants.EXCHANGE_FEED_TIME_OFFSET;
import static com.angelbroking.smartapi.utils.Constants.LAST_TRADED_PRICE_OFFSET;
import static com.angelbroking.smartapi.utils.Constants.SEQUENCE_NUMBER_OFFSET;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LTP {
	public static final int PACKET_SIZE_IN_BYTES = 51;
	
	private TokenID token;
	private long sequenceNumber;
	private long exchangeFeedTimeEpochMillis;
	private long lastTradedPrice;

	public LTP(ByteBuffer buffer) {
		this.token = ByteUtils.getTokenID(buffer);
		this.sequenceNumber = buffer.getLong(SEQUENCE_NUMBER_OFFSET);
		this.exchangeFeedTimeEpochMillis = buffer.getLong(EXCHANGE_FEED_TIME_OFFSET);
		this.lastTradedPrice = buffer.getLong(LAST_TRADED_PRICE_OFFSET);
	}

}
