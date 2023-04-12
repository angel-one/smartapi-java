package com.angelbroking.smartapi.smartticker;

import com.angelbroking.smartapi.smartticker.ticker.OnTicks;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify ;


public class SmartWebsocketTest {

    private static final String CLIENT_ID = "clientId";
    private static final String JWT_TOKEN = "jwtToken";
    private static final String API_KEY = "apiKey";
    private static final String ACTION_TYPE = "actionType";
    private static final String FEED_TYPE = "feedType";

    @Mock
    private SmartWebsocket smartWebsocket;

    @Mock
    private OnTicks smartWSOnTicks;

    private SmartWSOnTicks onTicksListener;
    private SmartWSOnError onErrorListener;

    @BeforeEach
    public void setUp() {
        smartWebsocket = new SmartWebsocket(CLIENT_ID, JWT_TOKEN, API_KEY, ACTION_TYPE, FEED_TYPE);
        onTicksListener = mock(SmartWSOnTicks.class);
        onErrorListener = mock(SmartWSOnError.class);
        smartWebsocket.setOnTickerArrivalListener(onTicksListener);
        smartWebsocket.setOnErrorListener(onErrorListener);
    }

    @Test
    public void testConstructor() {
        Assertions.assertNotNull(smartWebsocket);
        Assertions.assertNotNull(smartWebsocket.isConnectionOpen());
        Assertions.assertNotNull(smartWebsocket.getWebsocketAdapter());
    }

    @Test
    public void testOnConnected() throws Exception {
        WebSocketAdapter adapter = smartWebsocket.getWebsocketAdapter();
        WebSocket webSocket = mock(WebSocket.class);
        Map<String, List<String>> headers = new HashMap<>();

        // set mock implementation of SmartWSOnConnect
        smartWebsocket.setOnConnectedListener(mock(SmartWSOnConnect.class));
        adapter.onConnected(webSocket, headers);
        Assertions.assertNotNull(onTicksListener);
    }

}
