package com.angelbroking.smartapi.smartticker;

import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class SmartWebsocketTest {

    private static final String CLIENT_ID = "clientId";
    private static final String JWT_TOKEN = "jwtToken";
    private static final String API_KEY = "apiKey";
    private static final String ACTION_TYPE = "actionType";
    private static final String FEED_TYPE = "feedType";

    @Mock
    private SmartWSOnConnect onConnectedListener;

    @Mock
    private WebSocket mockWebSocket;

    @Mock
    private ScheduledExecutorService service;

    private SmartWebsocket smartWebsocket;

    private SmartWSOnTicks onTicksListener;

    @BeforeEach
     void setUp() {
        MockitoAnnotations.initMocks(this);
        smartWebsocket = new SmartWebsocket(CLIENT_ID, JWT_TOKEN, API_KEY, ACTION_TYPE, FEED_TYPE);
        onTicksListener = mock(SmartWSOnTicks.class);
       SmartWSOnError  onErrorListener = mock(SmartWSOnError.class);
        smartWebsocket.setOnTickerArrivalListener(onTicksListener);
        smartWebsocket.setOnErrorListener(onErrorListener);
}

    @Test
     void testConstructor() {
        assertNotNull(smartWebsocket);
        assertNotNull(smartWebsocket.getWebsocketAdapter());
    }

    @Test
     void testOnConnected() throws Exception {
        WebSocketAdapter adapter = smartWebsocket.getWebsocketAdapter();
        WebSocket webSocket = mock(WebSocket.class);
        Map<String, List<String>> headers = new HashMap<>();

        // set mock implementation of SmartWSOnConnect
        smartWebsocket.setOnConnectedListener(mock(SmartWSOnConnect.class));
        adapter.onConnected(webSocket, headers);
        assertNotNull(onTicksListener);
    }
    @Test
    void testGetWebsocketAdapter() {
        assertNotNull(smartWebsocket.getWebsocketAdapter());
    }


    @Test
    public void setOnErrorListener() {
        SmartWebsocket smartWebsocket = new SmartWebsocket("clientId", "jwtToken", "apiKey", "actionType", "feedType");

        SmartWSOnError mockOnErrorListener = mock(SmartWSOnError.class);
        smartWebsocket.setOnErrorListener(mockOnErrorListener);

        assertSame(mockOnErrorListener, smartWebsocket.onErrorListener);
    }

    @Test
    public void setOnTickerArrivalListener() {
        SmartWebsocket smartWebsocket = new SmartWebsocket("clientId", "jwtToken", "apiKey", "actionType", "feedType");

        SmartWSOnTicks mockOnTickerArrivalListener = mock(SmartWSOnTicks.class);
        smartWebsocket.setOnTickerArrivalListener(mockOnTickerArrivalListener);

        assertSame(mockOnTickerArrivalListener, smartWebsocket.onTickerArrivalListener);
    }

    @Test
    public void setOnConnectedListener() {
        SmartWebsocket smartWebsocket = new SmartWebsocket("clientId", "jwtToken", "apiKey", "actionType", "feedType");

        SmartWSOnConnect mockOnConnectedListener = mock(SmartWSOnConnect.class);
        smartWebsocket.setOnConnectedListener(mockOnConnectedListener);

        assertSame(mockOnConnectedListener, smartWebsocket.onConnectedListener);
    }

    @Test
    public void setOnDisconnectedListener() {
        SmartWebsocket smartWebsocket = new SmartWebsocket("clientId", "jwtToken", "apiKey", "actionType", "feedType");

        SmartWSOnDisconnect mockOnDisconnectedListener = mock(SmartWSOnDisconnect.class);
        smartWebsocket.setOnDisconnectedListener(mockOnDisconnectedListener);

        assertSame(mockOnDisconnectedListener, smartWebsocket.onDisconnectedListener);
    }

    @Test
    public void getWebsocketAdapter_onDisconnected() throws Exception {
        SmartWebsocket smartWebsocket = new SmartWebsocket("clientId", "jwtToken", "apiKey", "actionType", "feedType");

        SmartWSOnDisconnect mockOnDisconnectedListener = mock(SmartWSOnDisconnect.class);
        smartWebsocket.setOnDisconnectedListener(mockOnDisconnectedListener);

        smartWebsocket.getWebsocketAdapter().onDisconnected(mockWebSocket, null, null, true);

        verify(mockOnDisconnectedListener).onDisconnected();
    }

    @Test
    public void connect() throws WebSocketException {
        SmartWebsocket smartWebsocket = new SmartWebsocket("clientId", "jwtToken", "apiKey", "actionType", "feedType");
        smartWebsocket.webSocket = mockWebSocket;

        smartWebsocket.connect();

        verify(mockWebSocket).connect();
    }

    @Test
    public void disconnect() {
        SmartWebsocket smartWebsocket = new SmartWebsocket("clientId", "jwtToken", "apiKey", "actionType", "feedType");
        smartWebsocket.webSocket = mockWebSocket;

        when(mockWebSocket.isOpen()).thenReturn(true);

        smartWebsocket.disconnect();

        verify(mockWebSocket).isOpen();
        verify(mockWebSocket).disconnect();
    }

    @Test
    public void isConnectionOpen() {
        SmartWebsocket smartWebsocket = new SmartWebsocket("clientId", "jwtToken", "apiKey", "actionType", "feedType");
        smartWebsocket.webSocket = mockWebSocket;

        when(mockWebSocket.isOpen()).thenReturn(true);

        assertTrue(smartWebsocket.isConnectionOpen());

        when(mockWebSocket.isOpen()).thenReturn(false);

        assertFalse(smartWebsocket.isConnectionOpen());
    }

    @Test
    public void runscript_whenConnected() {
        SmartWebsocket smartWebsocket = new SmartWebsocket("clientId", "jwtToken", "apiKey", "actionType", "feedType");
        smartWebsocket.webSocket = mockWebSocket;

        when(mockWebSocket.isOpen()).thenReturn(true);

        smartWebsocket.runscript();

        verify(mockWebSocket).isOpen();
        verify(mockWebSocket).sendText(anyString());
    }

    @Test
    public void runscript_whenNotConnected() {
        SmartWebsocket smartWebsocket = new SmartWebsocket("clientId", "jwtToken", "apiKey", "actionType", "feedType");
        smartWebsocket.webSocket = mockWebSocket;

        when(mockWebSocket.isOpen()).thenReturn(false);

        SmartWSOnError mockOnErrorListener = mock(SmartWSOnError.class);
        smartWebsocket.setOnErrorListener(mockOnErrorListener);

        smartWebsocket.runscript();

        verify(mockWebSocket).isOpen();
        verify(mockOnErrorListener).onError(any(SmartAPIException.class));
    }
}

