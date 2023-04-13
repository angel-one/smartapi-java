package com.angelbroking.smartapi.smartstream.models;

import com.angelbroking.smartapi.utils.ByteUtils;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.ByteBuffer;

@Data
@NoArgsConstructor
public class Quote {
	public static final int PACKET_SIZE_IN_BYTES = 123;

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

	public Quote(ByteBuffer buffer) {
		this.token = ByteUtils.getTokenID(buffer);
		this.sequenceNumber = buffer.getLong(27);
		this.exchangeFeedTimeEpochMillis = buffer.getLong(35);
		this.lastTradedPrice = buffer.getLong(43);
		this.lastTradedQty = buffer.getLong(51);
		this.avgTradedPrice = buffer.getLong(59);
		this.volumeTradedToday = buffer.getLong(67);
		this.totalBuyQty = buffer.getLong(75);
		this.totalSellQty = buffer.getLong(83);
		this.openPrice = buffer.getLong(91);
		this.highPrice = buffer.getLong(99);
		this.lowPrice = buffer.getLong(107);
		this.closePrice = buffer.getLong(115);
	}


}
