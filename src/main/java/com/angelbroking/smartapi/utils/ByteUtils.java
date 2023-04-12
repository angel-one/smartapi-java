package com.angelbroking.smartapi.utils;

import com.angelbroking.smartapi.smartstream.models.*;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import lombok.Data;

@Data
public class ByteUtils {
	
	private static final int CHAR_ARRAY_SIZE = 25;

	public static LTP mapByteBufferToLTP(ByteBuffer buffer) {
		LTP ltp = new LTP(buffer);
		return ltp;
	}

	public static Quote mapByteBufferToQuote(ByteBuffer buffer) {
		Quote quote = new Quote(buffer);
		return quote;
	}


	public static SnapQuote mapByteBufferToSnapQuote(ByteBuffer buffer) {
		SnapQuote snapQuote = new SnapQuote(buffer);
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
