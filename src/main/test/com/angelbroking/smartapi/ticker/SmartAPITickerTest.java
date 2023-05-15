package com.angelbroking.smartapi.ticker;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SmartAPITickerTest {

    private SmartAPITicker ticker;
    private OnTicks onTicksListenerMock;
    private OnConnect onConnectListenerMock;
    private OnError onErrorListenerMock;
    private WebSocket wsMock;

    @BeforeEach
    public void setUp() {
        // Create mocks for the listeners and WebSocket
        onTicksListenerMock = mock(OnTicks.class);
        onConnectListenerMock = mock(OnConnect.class);
        onErrorListenerMock = mock(OnError.class);
        wsMock = mock(WebSocket.class);

        // Create the SmartAPITicker instance with the necessary dependencies
        ticker = new SmartAPITicker("clientId", "feedToken", "script", "task");
        ticker.setOnTickerArrivalListener(onTicksListenerMock);
        ticker.setOnConnectedListener(onConnectListenerMock);

        // Set the WebSocket mock
        ticker.ws = wsMock;
    }

    @Test
    public void setOnTickerArrivalListener_ShouldSetListener() {
        // Verify that the listener is set correctly
        assertEquals(onTicksListenerMock, ticker.onTickerArrivalListener);
    }

    @Test
    public void setOnConnectedListener_ShouldSetListener() {
        // Verify that the listener is set correctly
        assertEquals(onConnectListenerMock, ticker.onConnectedListener);
    }

    @Test
    public void getWebsocketAdapter_ShouldReturnValidAdapter() {
        // Call the method
        WebSocketAdapter adapter = ticker.getWebsocketAdapter();

        // Verify that the returned adapter is not null
        assertNotNull(adapter);
    }

    @Test
    public void disconnect_WhenConnectionOpen_ShouldDisconnectWebSocket() {
        // Set up the WebSocket to be open
        when(wsMock.isOpen()).thenReturn(true);

        // Call the method
        ticker.disconnect();

        // Verify that the WebSocket's disconnect method is called
        verify(wsMock).disconnect();
    }

    @Test
    public void disconnect_WhenConnectionClosed_ShouldNotDisconnectWebSocket() {
        // Set up the WebSocket to be closed
        when(wsMock.isOpen()).thenReturn(false);

        // Call the method
        ticker.disconnect();

        // Verify that the WebSocket's disconnect method is not called
        verify(wsMock, never()).disconnect();
    }

    @Test
    public void isConnectionOpen_WhenConnectionOpen_ShouldReturnTrue() {
        // Set up the WebSocket to be open
        when(wsMock.isOpen()).thenReturn(true);

        // Call the method
        boolean isConnected = ticker.isConnectionOpen();

        // Verify that the method returns true
        assertTrue(isConnected);
    }
}
