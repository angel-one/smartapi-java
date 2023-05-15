package com.angelbroking.smartapi.smartTicker;

import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SmartWebsocketTest {

    @Mock
    private WebSocket mockWebSocket;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
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
        smartWebsocket.ws = mockWebSocket;

        smartWebsocket.connect();

        verify(mockWebSocket).connect();
    }

    @Test
    public void disconnect() {
        SmartWebsocket smartWebsocket = new SmartWebsocket("clientId", "jwtToken", "apiKey", "actionType", "feedType");
        smartWebsocket.ws = mockWebSocket;

        when(mockWebSocket.isOpen()).thenReturn(true);

        smartWebsocket.disconnect();

        verify(mockWebSocket).isOpen();
        verify(mockWebSocket).disconnect();
    }

    @Test
    public void isConnectionOpen() {
        SmartWebsocket smartWebsocket = new SmartWebsocket("clientId", "jwtToken", "apiKey", "actionType", "feedType");
        smartWebsocket.ws = mockWebSocket;

        when(mockWebSocket.isOpen()).thenReturn(true);

        assertTrue(smartWebsocket.isConnectionOpen());

        when(mockWebSocket.isOpen()).thenReturn(false);

        assertFalse(smartWebsocket.isConnectionOpen());
    }

    @Test
    public void runscript_whenConnected() {
        SmartWebsocket smartWebsocket = new SmartWebsocket("clientId", "jwtToken", "apiKey", "actionType", "feedType");
        smartWebsocket.ws = mockWebSocket;

        when(mockWebSocket.isOpen()).thenReturn(true);

        smartWebsocket.runscript();

        verify(mockWebSocket).isOpen();
        verify(mockWebSocket).sendText(anyString());
    }

    @Test
    public void runscript_whenNotConnected() {
                SmartWebsocket smartWebsocket = new SmartWebsocket("clientId", "jwtToken", "apiKey", "actionType", "feedType");
        smartWebsocket.ws = mockWebSocket;

        when(mockWebSocket.isOpen()).thenReturn(false);

        SmartWSOnError mockOnErrorListener = mock(SmartWSOnError.class);
        smartWebsocket.setOnErrorListener(mockOnErrorListener);

        smartWebsocket.runscript();

        verify(mockWebSocket).isOpen();
        verify(mockOnErrorListener).onError(any(SmartAPIException.class));
    }
}

