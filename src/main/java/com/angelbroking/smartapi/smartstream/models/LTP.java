package com.angelbroking.smartapi.smartstream.models;

import lombok.Data;

@Data
public class LTP {
	public static final int PACKET_SIZE_IN_BYTES = 51;
	
	private TokenID token;
	private long sequenceNumber;
	private long exchangeFeedTimeEpochMillis;
	private long lastTradedPrice;
}
