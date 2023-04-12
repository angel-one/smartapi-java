package com.angelbroking.smartapi.smartticker.ticker;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SmartAPITickerTest {

    @Test
    public void testSmartAPITickerInitialization() {
        SmartApiTickerParams params = mock(SmartApiTickerParams.class);
        OnTicks onTickerArrivalListener = mock(OnTicks.class);
        OnConnect onConnectedListener = mock(OnConnect.class);
        OnError onErrorListener = mock(OnError.class);

        SmartAPITicker ticker = new SmartAPITicker(params, onTickerArrivalListener, onConnectedListener, onErrorListener);
        assertNotNull(ticker);
        assertNotNull(ticker.getWebsocketAdapter());
        assertNotNull(ticker.isConnectionOpen());
    }


}
