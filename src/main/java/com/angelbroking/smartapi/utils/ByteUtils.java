package com.angelbroking.smartapi.utils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import com.angelbroking.smartapi.smartstream.models.*;

import static com.angelbroking.smartapi.utils.Constants.*;

public class ByteUtils {
	
	private static final int CHAR_ARRAY_SIZE = 25;
	
	private ByteUtils() {
		
	}

	public static LTP mapToLTP(ByteBuffer packet) {
		return new LTP(packet);
	}

	public static Quote mapToQuote(ByteBuffer packet) {
		return new Quote(packet);
	}
	
	public static SnapQuote mapToSnapQuote(ByteBuffer packet) {
		return new SnapQuote(packet);
	}
    public static Depth mapToDepth20(ByteBuffer packet) {
        return new Depth(packet);
    }
	public static TokenID getTokenID(ByteBuffer byteBuffer) {
		byte[] token = new byte[CHAR_ARRAY_SIZE];
		for (int i = 0; i < CHAR_ARRAY_SIZE; i++) {
			token[i] = byteBuffer.get(2 + i);
		}
		return new TokenID(ExchangeType.findByValue(byteBuffer.get(1)), new String(token, StandardCharsets.UTF_8));
	}
	
	public static SmartApiBBSInfo[] getBestFiveBuyData(ByteBuffer buffer) {
        SmartApiBBSInfo[] bestFiveBuyData = new SmartApiBBSInfo[NUM_PACKETS];

        for (int i = 0; i < NUM_PACKETS; i++) {
            int offset = BUY_START_POSITION + (i * PACKET_SIZE);
            short buySellFlag = buffer.getShort(offset + BUY_SELL_FLAG_OFFSET);
            long quantity = buffer.getLong(offset + QUANTITY_OFFSET);
            long price = buffer.getLong(offset + PRICE_OFFSET);
            short numberOfOrders = buffer.getShort(offset + NUMBER_OF_ORDERS_OFFSET);
            bestFiveBuyData[i] = new SmartApiBBSInfo(buySellFlag, quantity, price, numberOfOrders);
        }

        return bestFiveBuyData;
    }

    public static SmartApiBBSInfo[] getBestFiveSellData(ByteBuffer buffer) {
        SmartApiBBSInfo[] bestFiveSellData = new SmartApiBBSInfo[NUM_PACKETS];
        for (int i = 0; i < NUM_PACKETS; i++) {
            int offset = SELL_START_POSITION + (i * PACKET_SIZE);
            short buySellFlag = buffer.getShort(offset + BUY_SELL_FLAG_OFFSET);
            long quantity = buffer.getLong(offset + QUANTITY_OFFSET);
            long price = buffer.getLong(offset + PRICE_OFFSET);
            short numberOfOrders = buffer.getShort(offset + NUMBER_OF_ORDERS_OFFSET);
            bestFiveSellData[i] = new SmartApiBBSInfo(buySellFlag, quantity, price, numberOfOrders);
        }
        return bestFiveSellData;
    }

    public static BestTwentyData[] getBestTwentyBuyData(ByteBuffer buffer) {
        BestTwentyData[] bestTwentyBuyData = new BestTwentyData[NUM_PACKETS_FOR_DEPTH];

        for (int i = 0; i < NUM_PACKETS_FOR_DEPTH; i++) {
            int offset = BEST_TWENTY_BUY_DATA_POSITION + (i * PACKET_SIZE_FOR_DEPTH20);
            long quantity = buffer.getInt(offset + QUANTITY_OFFSET_FOR_DEPTH20);
            long price = buffer.getInt(offset + PRICE_OFFSET_FOR_DEPTH20);
            short numberOfOrders = buffer.getShort(offset + NUMBER_OF_ORDERS_OFFSET_FOR_DEPTH20);
            bestTwentyBuyData[i] = new BestTwentyData(quantity, price, numberOfOrders);
        }

        return bestTwentyBuyData;
    }

    public static BestTwentyData[] getBestTwentySellData(ByteBuffer buffer) {
        BestTwentyData[] bestTwentyBuyData = new BestTwentyData[NUM_PACKETS_FOR_DEPTH];

        for (int i = 0; i < NUM_PACKETS_FOR_DEPTH; i++) {
            int offset = BEST_TWENTY_SELL_DATA_POSITION + (i * PACKET_SIZE_FOR_DEPTH20);
            long quantity = buffer.getInt(offset + QUANTITY_OFFSET_FOR_DEPTH20);
            long price = buffer.getInt(offset + PRICE_OFFSET_FOR_DEPTH20);
            short numberOfOrders = buffer.getShort(offset + NUMBER_OF_ORDERS_OFFSET_FOR_DEPTH20);
            bestTwentyBuyData[i] = new BestTwentyData(quantity, price, numberOfOrders);
        }

        return bestTwentyBuyData;
    }
}
