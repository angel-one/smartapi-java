package com.angelbroking.smartapi.ticker;

import com.angelbroking.smartapi.smartstream.models.BestTwentyData;
import com.angelbroking.smartapi.ticker.OnConnect;
import com.angelbroking.smartapi.ticker.OnTicks;
import com.angelbroking.smartapi.ticker.SmartAPITicker;
import com.angelbroking.smartapi.utils.ByteUtils;
import org.json.JSONArray;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

import static com.angelbroking.smartapi.utils.Constants.*;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SmartApiTickerTest {

    @Test
    public void testSmartApiTickerWithAllFields() {
        // Arrange
        String clientId = "client_id";
        String feedToken = "feed_token";
        String script = "script";
        String task = "task";

        // Act
        SmartAPITicker ticker = new SmartAPITicker(clientId, feedToken, script, task);

        // Assert
        assertNotNull(ticker);
    }

    @Test
    public void testSetListenerOfSmartAPITicker() {
        // Arrange
        String clientId = "client_id";
        String feedToken = "feed_token";
        String script = "script";
        String task = "task";

        SmartAPITicker ticker = new SmartAPITicker(clientId, feedToken, script, task);

        // Act
        ticker.setOnTickerArrivalListener(new OnTicks() {
            @Override
            public void onTicks(JSONArray ticks) {
                // Test implementation
            }
        });

        ticker.setOnConnectedListener(new OnConnect() {
            @Override
            public void onConnected() {
                // Test implementation
            }
        });

        // Assert
        assertNotNull(ticker);
    }


    @Test
    public void testUnableToCloseWebSocketConnectionOfSmartAPITicker() {
        // Arrange
        String clientId = "client_id";
        String feedToken = "feed_token";
        String script = "script";
        String task = "task";

        SmartAPITicker ticker = new SmartAPITicker(clientId, feedToken, script, task);

        // Act
        ticker.disconnect();

        // Assert
        assertFalse(ticker.isConnectionOpen());
    }


    @Test
    public void testBestTwentyDataMethod() {
        ByteBuffer buffer = ByteBuffer.allocate(100000); // for testing
        for (int i = 0; i < NUM_PACKETS_FOR_DEPTH; i++) {
            int offset = BEST_TWENTY_BUY_DATA_POSITION + (i * PACKET_SIZE_FOR_DEPTH20);
            buffer.putInt(offset + QUANTITY_OFFSET_FOR_DEPTH20, 100);
            buffer.putInt(offset + PRICE_OFFSET_FOR_DEPTH20, 200);
            buffer.putShort(offset + NUMBER_OF_ORDERS_OFFSET_FOR_DEPTH20, (short) 5);
        }

        BestTwentyData[] result = ByteUtils.getBestTwentyBuyData(buffer);

        assertEquals(NUM_PACKETS_FOR_DEPTH, result.length);
        for (BestTwentyData data : result) {
            assertEquals(100, data.getQuantity());
            assertEquals(200, data.getPrice());
            assertEquals(5, data.getNumberOfOrders());
        }
    }

    @Test
    public void testBestTwentyDataWithNoValues() {
        ByteBuffer buffer = ByteBuffer.allocate(10000);

        BestTwentyData[] result = ByteUtils.getBestTwentyBuyData(buffer);

        assertEquals(NUM_PACKETS_FOR_DEPTH, result.length);
        for (BestTwentyData data : result) {
            assertEquals(0, data.getQuantity());
            assertEquals(0, data.getPrice());
            assertEquals(0, data.getNumberOfOrders());
        }
    }
}
