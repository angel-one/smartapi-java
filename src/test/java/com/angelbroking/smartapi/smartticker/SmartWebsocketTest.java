package com.angelbroking.smartapi.smartticker;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;


 class SmartWebsocketTest {

    private static final String CLIENT_ID = "clientId";
    private static final String JWT_TOKEN = "jwtToken";
    private static final String API_KEY = "apiKey";
    private static final String ACTION_TYPE = "actionType";
    private static final String FEED_TYPE = "feedType";

    @Mock
    private SmartWebsocket smartWebsocket;

    private SmartWSOnTicks onTicksListener;

    @BeforeEach
     void setUp() {
        smartWebsocket = new SmartWebsocket(CLIENT_ID, JWT_TOKEN, API_KEY, ACTION_TYPE, FEED_TYPE);
        onTicksListener = mock(SmartWSOnTicks.class);
       SmartWSOnError  onErrorListener = mock(SmartWSOnError.class);
        smartWebsocket.setOnTickerArrivalListener(onTicksListener);
        smartWebsocket.setOnErrorListener(onErrorListener);
    }

    @Test
     void testConstructor() {
        Assertions.assertNotNull(smartWebsocket);
        Assertions.assertNotNull(smartWebsocket.getWebsocketAdapter());
    }

    @Test
     void testOnConnected() throws Exception {
        WebSocketAdapter adapter = smartWebsocket.getWebsocketAdapter();
        WebSocket webSocket = mock(WebSocket.class);
        Map<String, List<String>> headers = new HashMap<>();

        // set mock implementation of SmartWSOnConnect
        smartWebsocket.setOnConnectedListener(mock(SmartWSOnConnect.class));
        adapter.onConnected(webSocket, headers);
        Assertions.assertNotNull(onTicksListener);
    }

}
