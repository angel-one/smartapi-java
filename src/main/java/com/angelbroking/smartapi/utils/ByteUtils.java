package com.angelbroking.smartapi.utils;

import com.angelbroking.smartapi.smartstream.models.ExchangeType;
import com.angelbroking.smartapi.smartstream.models.LTP;
import com.angelbroking.smartapi.smartstream.models.Quote;
import com.angelbroking.smartapi.smartstream.models.SnapQuote;
import com.angelbroking.smartapi.smartstream.models.TokenID;
import lombok.Data;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@Data
public class ByteUtils {


	private ByteUtils() {
	}
	
	private static final int CHAR_ARRAY_SIZE = 25;

	public static LTP mapByteBufferToLTP(ByteBuffer buffer) {
		return new LTP(buffer);
	}

	public static Quote mapByteBufferToQuote(ByteBuffer buffer) {
		return new Quote(buffer);
	}


	public static SnapQuote mapByteBufferToSnapQuote(ByteBuffer buffer) {
		return new SnapQuote(buffer);
	}


	public static TokenID getTokenID(ByteBuffer byteBuffer) {
		byte[] token = new byte[CHAR_ARRAY_SIZE];

		for(int i=0; i<CHAR_ARRAY_SIZE; i++) {
			token[i] = byteBuffer.get(2+i);
		}
		return new TokenID(ExchangeType.findByValue(byteBuffer.get(1)), new String(token, StandardCharsets.UTF_8));
	}
}
