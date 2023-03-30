package com.angelbroking.smartapi.smartstream.models;

import lombok.Data;

@Data
public class SnapQuote {
	public static final int PACKET_SIZE_IN_BYTES = 379;

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


	
}
