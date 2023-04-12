package com.angelbroking.smartapi.smartstream.models;

import com.angelbroking.smartapi.utils.ByteUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.ByteBuffer;

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
		this.sequenceNumber = buffer.getLong(27);
		this.exchangeFeedTimeEpochMillis = buffer.getLong(35);
		this.lastTradedPrice = buffer.getLong(43);
	}

}
