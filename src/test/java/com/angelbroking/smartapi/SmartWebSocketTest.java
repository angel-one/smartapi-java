package com.angelbroking.smartapi;

import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import com.angelbroking.smartapi.smartTicker.*;
import org.json.JSONArray;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SmartWebSocketTest {
    @Test
    public void testWebSocketWithAllFields() {
        // Arrange
        String clientId = "123";
        String jwtToken = "token";
        String apiKey = "key";
        String actionType = "action";
        String feedType = "feed";

        // Act
        SmartWebsocket websocket = new SmartWebsocket(clientId, jwtToken, apiKey, actionType, feedType);

        // Assert
        assertNotNull(websocket);
    }

    @Test
    public void testSetListener() {
        // Arrange
        SmartWebsocket websocket = new SmartWebsocket("123", "token", "key", "action", "feed");

        // Act
        websocket.setOnErrorListener(new SmartWSOnError() {
            @Override
            public void onError(Exception exception) {}

            @Override
            public void onError(SmartAPIException smartAPIException) {}

            @Override
            public void onError(String error) {}
        });

        websocket.setOnTickerArrivalListener(new SmartWSOnTicks() {
            @Override
            public void onTicks(JSONArray ticks) {}
        });

        websocket.setOnConnectedListener(new SmartWSOnConnect() {
            @Override
            public void onConnected() {}
        });

        websocket.setOnDisconnectedListener(new SmartWSOnDisconnect() {
            @Override
            public void onDisconnected() {}
        });

        // Assert
        assertNotNull(websocket);
    }


}
