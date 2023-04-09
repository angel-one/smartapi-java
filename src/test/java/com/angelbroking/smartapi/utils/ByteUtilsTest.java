package com.angelbroking.smartapi.utils;

import com.angelbroking.smartapi.smartstream.models.*;
import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;

public class ByteUtilsTest {

    @Test
    public void testGetTokenID() {
        // create a ByteBuffer with sample data
        ByteBuffer byteBuffer = ByteBuffer.allocate(28);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 1);  // ExchangeType value
        byteBuffer.put((byte) 't');
        byteBuffer.put((byte) 'o');
        byteBuffer.put((byte) 'k');
        byteBuffer.put((byte) 'e');
        byteBuffer.put((byte) 'n');
        byteBuffer.put((byte) '1');
        byteBuffer.put((byte) '2');
        byteBuffer.put((byte) '3');
        byteBuffer.put((byte) '4');
        byteBuffer.put((byte) '5');
        byteBuffer.put((byte) '6');
        byteBuffer.put((byte) '7');
        byteBuffer.put((byte) '8');
        byteBuffer.put((byte) '9');
        byteBuffer.put((byte) '0');
        byteBuffer.put((byte) 'a');
        byteBuffer.put((byte) 'b');
        byteBuffer.put((byte) 'c');
        byteBuffer.put((byte) 'd');
        byteBuffer.put((byte) 'e');
        byteBuffer.put((byte) 'f');
        byteBuffer.put((byte) 'g');
        byteBuffer.put((byte) 'h');
        byteBuffer.put((byte) 'i');
        byteBuffer.put((byte) 'j');
        byteBuffer.put((byte) 'k');
        // call the method under test
        TokenID tokenID = ByteUtils.getTokenID(byteBuffer);
        // assert the expected result
        assertEquals("token1234567890abcdefghij", tokenID.getToken());
    }

    @Test
    public void testMapByteBufferToSnapQuote() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(51);

        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 2); // ExchangeType value
        byteBuffer.putLong(27, 123456789); // Sequence Number
        byteBuffer.putLong(35, 987654321); // Exchange Feed Time Epoch Millis
        byteBuffer.putLong(43, 34567890); // Last Traded Price

        SnapQuote snapQuote = ByteUtils.mapByteBufferToSnapQuote(byteBuffer);
        assertEquals(ExchangeType.NSE_FO, snapQuote.getToken().getExchangeType());
        assertEquals(123456789, snapQuote.getSequenceNumber());
        assertEquals(987654321, snapQuote.getExchangeFeedTimeEpochMillis());
        assertEquals(34567890, snapQuote.getLastTradedPrice());
    }

    @Test
    public void testMapByteBufferToQuote() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(123);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 2); // ExchangeType value
        byteBuffer.putLong(27,123456789); // Sequence Number
        byteBuffer.putLong(35,987654321); // Exchange Feed Time Epoch Millis
        byteBuffer.putLong(43,34567890); // Last Traded Price
        byteBuffer.putLong(51,23456789); // Last Traded Qty
        byteBuffer.putLong(59,98765432); // Avg Traded Price
        byteBuffer.putLong(67,345678901); // Volume Traded Today
        byteBuffer.putLong (75,234); // Total Buy Qty
        byteBuffer.putLong(83,987); // Total Sell Qty
        byteBuffer.putLong(91,1234567890); // Open Price
        byteBuffer.putLong(99,234567890 ); // High Price
        byteBuffer.putLong(107,345678901); // Low Price
        byteBuffer.putLong(115,456789012); // Close Price

        Quote quote = ByteUtils.mapByteBufferToQuote(byteBuffer);



        assertEquals(ExchangeType.NSE_FO, quote.getToken().getExchangeType());
        assertEquals(123456789, quote.getSequenceNumber());
        assertEquals(987654321, quote.getExchangeFeedTimeEpochMillis());
        assertEquals(34567890, quote.getLastTradedPrice());
        assertEquals(23456789, quote.getLastTradedQty());
        assertEquals(98765432, quote.getAvgTradedPrice());
        assertEquals(345678901, quote.getVolumeTradedToday());
        assertEquals( 234, quote.getTotalBuyQty(), 0.0);
        assertEquals(987, quote.getTotalSellQty(), 0.0);
        assertEquals(1234567890, quote.getOpenPrice());
        assertEquals(234567890, quote.getHighPrice());
        assertEquals(345678901, quote.getLowPrice());
        assertEquals(456789012, quote.getClosePrice());
    }

    @Test
    public void testMapByteBufferToLTP() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(51);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 2); // ExchangeType value
        byteBuffer.putLong(27, 123456789); // Sequence Number
        byteBuffer.putLong(35, 987654321); // Exchange Feed Time Epoch Millis
        byteBuffer.putLong(43, 34567890); // Last Traded Price

        LTP ltp = ByteUtils.mapByteBufferToLTP(byteBuffer);

        assertEquals(ExchangeType.NSE_FO, ltp.getToken().getExchangeType());
        assertEquals(123456789, ltp.getSequenceNumber());
        assertEquals(987654321, ltp.getExchangeFeedTimeEpochMillis());
        assertEquals(34567890, ltp.getLastTradedPrice());
    }

}
