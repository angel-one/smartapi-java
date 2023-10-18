package com.angelbroking.smartapi;

import com.angelbroking.smartapi.ticker.OnConnect;
import com.angelbroking.smartapi.ticker.OnTicks;
import com.angelbroking.smartapi.ticker.SmartAPITicker;
import org.json.JSONArray;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

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


}
