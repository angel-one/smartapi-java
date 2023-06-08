package com.angelbroking.smartapi.utils;

import com.angelbroking.smartapi.smartstream.models.ExchangeType;
import com.angelbroking.smartapi.smartstream.models.LTP;
import com.angelbroking.smartapi.smartstream.models.Quote;
import com.angelbroking.smartapi.smartstream.models.SmartApiBBSInfo;
import com.angelbroking.smartapi.smartstream.models.SnapQuote;
import com.angelbroking.smartapi.smartstream.models.TokenID;
import lombok.Data;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static com.angelbroking.smartapi.utils.Constants.BUY_SELL_FLAG_OFFSET;
import static com.angelbroking.smartapi.utils.Constants.BUY_START_POSITION;
import static com.angelbroking.smartapi.utils.Constants.NUMBER_OF_ORDERS_OFFSET;
import static com.angelbroking.smartapi.utils.Constants.NUM_PACKETS;
import static com.angelbroking.smartapi.utils.Constants.PACKET_SIZE;
import static com.angelbroking.smartapi.utils.Constants.PRICE_CONVERSION_FACTOR;
import static com.angelbroking.smartapi.utils.Constants.PRICE_OFFSET;
import static com.angelbroking.smartapi.utils.Constants.QUANTITY_OFFSET;
import static com.angelbroking.smartapi.utils.Constants.SELL_START_POSITION;

@Data
public class ByteUtils {


    private static final int CHAR_ARRAY_SIZE = 25;

    private ByteUtils() {
    }

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
            long price = buffer.getLong(offset + PRICE_OFFSET) / PRICE_CONVERSION_FACTOR;
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
            long price = buffer.getLong(offset + PRICE_OFFSET) / PRICE_CONVERSION_FACTOR;
            short numberOfOrders = buffer.getShort(offset + NUMBER_OF_ORDERS_OFFSET);
            bestFiveSellData[i] = new SmartApiBBSInfo(buySellFlag, quantity, price, numberOfOrders);
        }
        return bestFiveSellData;
    }


}
