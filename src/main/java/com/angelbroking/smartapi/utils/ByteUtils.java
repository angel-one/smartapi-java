package com.angelbroking.smartapi.utils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.angelbroking.smartapi.smartstream.models.*;

import static com.angelbroking.smartapi.utils.Constants.*;

public class ByteUtils {
    
    private static final int CHAR_ARRAY_SIZE = 25;

    // Utility methods for reading from ByteBuffer
    public static int readInt(ByteBuffer buffer) {
        return buffer.getInt();
    }

    public static short readShort(ByteBuffer buffer) {
        return buffer.getShort();
    }

    public static long readLong(ByteBuffer buffer) {
        return buffer.getLong();
    }

    public static String readString(ByteBuffer buffer) {
        // Example: read a fixed-length string (e.g., 25 bytes)
        byte[] strBytes = new byte[25];
        buffer.get(strBytes);
        return new String(strBytes, StandardCharsets.UTF_8).trim();
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
            // Assuming com.angelbroking.smartapi.smartstream.models.SmartApiBBSInfo has these setters
            SmartApiBBSInfo info = new com.angelbroking.smartapi.smartstream.models.SmartApiBBSInfo();
            info.setBuySellFlag(buySellFlag); // Example setter name
            info.setQuantity(quantity);       // Example setter name
            info.setPrice(price);             // Example setter name
            info.setNumberOfOrders(numberOfOrders); // Example setter name
            bestFiveBuyData[i] = info;
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
            // Assuming com.angelbroking.smartapi.smartstream.models.SmartApiBBSInfo has these setters
            SmartApiBBSInfo info = new com.angelbroking.smartapi.smartstream.models.SmartApiBBSInfo();
            info.setBuySellFlag(buySellFlag); // Example setter name
            info.setQuantity(quantity);       // Example setter name
            info.setPrice(price);             // Example setter name
            info.setNumberOfOrders(numberOfOrders); // Example setter name
            bestFiveSellData[i] = info;
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
            // Assuming com.angelbroking.smartapi.smartstream.models.BestTwentyData has these setters
            com.angelbroking.smartapi.smartstream.models.BestTwentyData data = new com.angelbroking.smartapi.smartstream.models.BestTwentyData();
            data.setQuantity(quantity);         // Example setter name
            data.setPrice(price);               // Example setter name
            data.setNumberOfOrders(numberOfOrders); // Example setter name
            bestTwentyBuyData[i] = data;
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
            // Assuming com.angelbroking.smartapi.smartstream.models.BestTwentyData has these setters
            com.angelbroking.smartapi.smartstream.models.BestTwentyData data = new com.angelbroking.smartapi.smartstream.models.BestTwentyData();
            data.setQuantity(quantity);         // Example setter name
            data.setPrice(price);               // Example setter name
            data.setNumberOfOrders(numberOfOrders); // Example setter name
            bestTwentyBuyData[i] = data;
        }

        return bestTwentyBuyData;
    }

    public static SmartApiBBSInfo mapSnapQuoteResponseToBBSInfo(ByteBuffer buffer) {
        int packetLength = readInt(buffer);
        String token = readString(buffer);
        int sequenceNumber = readInt(buffer);
        short exchangeType = readShort(buffer); // Exchange Type
        buffer.position(buffer.position() + 22); // Skip Last Traded Time (long), Last Traded Price (long), etc.
        // Assuming com.angelbroking.smartapi.smartstream.models.SmartApiBBSInfo has these setters
        com.angelbroking.smartapi.smartstream.models.SmartApiBBSInfo info = new com.angelbroking.smartapi.smartstream.models.SmartApiBBSInfo();
        info.setPacketLength(packetLength); // Example setter name
        info.setToken(token);               // Example setter name
        info.setSequenceNumber(sequenceNumber); // Example setter name
        info.setExchangeType(exchangeType); // Example setter name
        return info;
    }

    public static BestTwentyData mapToBestTwentyData(ByteBuffer buffer) {
        long quantity = readLong(buffer); // quantity
        long price = readLong(buffer); // price
        short numberOrders = readShort(buffer); // number of orders
        buffer.position(buffer.position() + 2); // skip 2 bytes
        // Assuming com.angelbroking.smartapi.smartstream.models.BestTwentyData has these setters
        com.angelbroking.smartapi.smartstream.models.BestTwentyData data = new com.angelbroking.smartapi.smartstream.models.BestTwentyData();
        data.setQuantity(quantity);         // Example setter name
        data.setPrice(price);               // Example setter name
        data.setNumberOfOrders(numberOrders); // Example setter name
        return data;
    }

    public static List<BestTwentyData> mapToBestTwentyBuySellDataList(ByteBuffer buffer) {
        List<com.angelbroking.smartapi.smartstream.models.BestTwentyData> bestTwentyDataList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            long quantity = readLong(buffer); // quantity
            long price = readLong(buffer); // price
            short numberOrders = readShort(buffer); // number of orders
            buffer.position(buffer.position() + 2); // skip 2 bytes
            com.angelbroking.smartapi.smartstream.models.BestTwentyData data = new com.angelbroking.smartapi.smartstream.models.BestTwentyData();
            data.setQuantity(quantity);         // Example setter name
            data.setPrice(price);               // Example setter name
            data.setNumberOfOrders(numberOrders); // Example setter name
            bestTwentyDataList.add(data);
        }
        return bestTwentyDataList;
    }
}
