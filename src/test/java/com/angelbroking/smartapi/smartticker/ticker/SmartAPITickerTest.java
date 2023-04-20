package com.angelbroking.smartapi.smartticker.ticker;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

class SmartAPITickerTest {

    @Test
    void testSmartAPITickerInitialization() throws NoSuchAlgorithmException {
        SmartApiTickerParams params = mock(SmartApiTickerParams.class);
        OnTicks onTickerArrivalListener = mock(OnTicks.class);
        OnConnect onConnectedListener = mock(OnConnect.class);
        OnError onErrorListener = mock(OnError.class);

        SmartAPITicker ticker = new SmartAPITicker(params, onTickerArrivalListener, onConnectedListener, onErrorListener);
        assertNotNull(ticker);
        assertNotNull(ticker.getWebsocketAdapter());
    }


}
