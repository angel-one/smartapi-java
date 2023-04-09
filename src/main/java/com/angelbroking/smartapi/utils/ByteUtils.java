package com.angelbroking.smartapi.utils;

import com.angelbroking.smartapi.smartstream.models.*;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class ByteUtils {
	
	private static final int CHAR_ARRAY_SIZE = 25;
	
	private ByteUtils() {
		
	}

	public static LTP mapByteBufferToLTP(ByteBuffer buffer) {
		LTP ltp = new LTP();
		ltp.setToken(getTokenID(buffer));
		ltp.setSequenceNumber(buffer.getLong(27));
		ltp.setExchangeFeedTimeEpochMillis(buffer.getLong(35));
		ltp.setLastTradedPrice(buffer.getLong(43));
		return ltp;
	}

	public static Quote mapByteBufferToQuote(ByteBuffer buffer) {
		Quote quote = new Quote();
		quote.setToken(getTokenID(buffer));
		quote.setSequenceNumber(buffer.getLong(27));
		quote.setExchangeFeedTimeEpochMillis(buffer.getLong(35));
		quote.setLastTradedPrice(buffer.getLong(43));
		quote.setLastTradedQty(buffer.getLong(51));
		quote.setAvgTradedPrice(buffer.getLong(59));
		quote.setVolumeTradedToday(buffer.getLong(67));
		quote.setTotalBuyQty(buffer.getLong(75));
		quote.setTotalSellQty(buffer.getLong(83));
		quote.setOpenPrice(buffer.getLong(91));
		quote.setHighPrice(buffer.getLong(99));
		quote.setLowPrice(buffer.getLong(107));
		quote.setClosePrice(buffer.getLong(115));
		return quote;
	}


	public static SnapQuote mapByteBufferToSnapQuote(ByteBuffer buffer) {
		SnapQuote snapQuote = new SnapQuote();
		snapQuote.setToken(getTokenID(buffer));
		snapQuote.setSequenceNumber(buffer.getLong(27));
		snapQuote.setExchangeFeedTimeEpochMillis(buffer.getLong(35));
		snapQuote.setLastTradedPrice(buffer.getLong(43));
		return snapQuote;
	}


	public static TokenID getTokenID(ByteBuffer byteBuffer) {
		byte[] token = new byte[CHAR_ARRAY_SIZE];

		for(int i=0; i<CHAR_ARRAY_SIZE; i++) {
			token[i] = byteBuffer.get(2+i);
		}
		return new TokenID(ExchangeType.findByValue(byteBuffer.get(1)), new String(token, StandardCharsets.UTF_8));
	}
}
