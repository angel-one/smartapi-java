package com.angelbroking.smartapi.smartstream.ticker;

import com.angelbroking.smartapi.smartstream.models.LTP;
import com.angelbroking.smartapi.smartstream.models.Quote;
import com.angelbroking.smartapi.smartstream.models.SmartStreamError;
import com.angelbroking.smartapi.smartstream.models.SnapQuote;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SmartStreamTickerTest {

    private static final String CLIENT_ID = "client_id";
    private static final String FEED_TOKEN = "feed_token";

    private SmartStreamTicker ticker;

    private SmartStreamListener listener;

    @BeforeEach
    void setUp() {
        listener = new SmartStreamListener() {
            @Override
            public void onConnected() {}

            @Override
            public void onLTPArrival(LTP ltp) {}

            @Override
            public void onQuoteArrival(Quote quote) {}

            @Override
            public void onSnapQuoteArrival(SnapQuote snapQuote) {}

            @Override
            public void onPong() {}

            @Override
            public void onError(SmartStreamError error) {}
        };
    }

    @Test
    void testConstructor() {
        // Test that the constructor initializes the ticker and calls the init method
        ticker = new SmartStreamTicker(CLIENT_ID, FEED_TOKEN, listener);
        Assertions.assertNotNull(ticker);
    }

    @Test
    void testConstructorWithNullClientId() {
        // Test that the constructor throws an exception when the clientId is null
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ticker = new SmartStreamTicker(null, FEED_TOKEN, listener);
        });
    }

    @Test
    void testConstructorWithNullFeedToken() {
        // Test that the constructor throws an exception when the feedToken is null
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ticker = new SmartStreamTicker(CLIENT_ID, null, listener);
        });
    }

    @Test
    void testConstructorWithNullListener() {
        // Test that the constructor throws an exception when the listener is null
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ticker = new SmartStreamTicker(CLIENT_ID, FEED_TOKEN, null);
        });
    }

    @Test
    void testGetWebsocketAdapter() {
        // Test that the getWebsocketAdapter method returns a non-null adapter
        ticker = new SmartStreamTicker(CLIENT_ID, FEED_TOKEN, listener);
        Assertions.assertNotNull(ticker.getWebsocketAdapter());
    }
}
