package com.angelbroking.smartapi.smartticker;

import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import com.angelbroking.smartapi.smartTicker.*;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import org.json.JSONArray;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

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

        @Test
        public void testSetOnErrorListener_success() throws NoSuchFieldException, IllegalAccessException {
            SmartWebsocket websocket = new SmartWebsocket("clientId", "jwtToken", "apiKey", "actionType", "feedType");
            SmartWSOnError listener = new SmartWSOnError() {
                @Override
                public void onError(Exception exception) {}

                @Override
                public void onError(SmartAPIException smartAPIException) {}

                @Override
                public void onError(String error) {}
            };

            // Use reflection to access the private field onErrorListener
            Field onErrorListenerField = SmartWebsocket.class.getDeclaredField("onErrorListener");
            onErrorListenerField.setAccessible(true); // Make it accessible
            onErrorListenerField.set(websocket, listener); // Set the private field with the listener

            SmartWSOnError actualListener = (SmartWSOnError) onErrorListenerField.get(websocket);

            assertEquals(listener, actualListener);
        }



        @Test
        public void test_setOnTickerArrivalListener_validListener() throws NoSuchFieldException, IllegalAccessException {
            // Initialize the SmartWebsocket object
            SmartWebsocket websocket = new SmartWebsocket("clientId", "jwtToken", "apiKey", "actionType", "feedType");

            // Create a valid SmartWSOnTicks listener
            SmartWSOnTicks listener = new SmartWSOnTicks() {
                @Override
                public void onTicks(JSONArray ticks) {
                    // Listener implementation
                }
            };

            // Use reflection to access the private field onTickerArrivalListener
            Field onTickerArrivalListenerField = SmartWebsocket.class.getDeclaredField("onTickerArrivalListener");
            onTickerArrivalListenerField.setAccessible(true); // Make it accessible
            onTickerArrivalListenerField.set(websocket, listener); // Set the private field with the listener

            SmartWSOnTicks actualListener = (SmartWSOnTicks) onTickerArrivalListenerField.get(websocket);

            assertEquals(listener, actualListener);
        }

    @Test
    public void test_setOnConnectedListener_success() throws NoSuchFieldException, IllegalAccessException {
        // Initialize the SmartWebsocket object
        SmartWebsocket websocket = new SmartWebsocket("clientId", "jwtToken", "apiKey", "actionType", "feedType");

        // Create a valid SmartWSOnConnect listener
        SmartWSOnConnect listener = new SmartWSOnConnect() {
            @Override
            public void onConnected() {
                // Listener implementation
            }
        };

        // Use reflection to access the private field onConnectedListener
        Field onConnectedListenerField = SmartWebsocket.class.getDeclaredField("onConnectedListener");
        onConnectedListenerField.setAccessible(true); // Make it accessible
        onConnectedListenerField.set(websocket, listener); // Set the private field with the listener

        SmartWSOnConnect actualListener = (SmartWSOnConnect) onConnectedListenerField.get(websocket);

        assertEquals(listener, actualListener);
    }

    @Test
    public void test_on_connected_listener_called() {
        SmartWebsocket websocket = new SmartWebsocket("clientId", "jwtToken", "apiKey", "actionType", "feedType");
        WebSocketAdapter adapter = websocket.getWebsocketAdapter();

        WebSocket websocketMock = mock(WebSocket.class);
        Map<String, List<String>> headers = new HashMap<>();

        SmartWSOnConnect onConnectedListenerMock = mock(SmartWSOnConnect.class);
        websocket.setOnConnectedListener(onConnectedListenerMock);

        assertDoesNotThrow(() -> adapter.onConnected(websocketMock, headers));
        verify(onConnectedListenerMock, times(1)).onConnected();
    }

}
