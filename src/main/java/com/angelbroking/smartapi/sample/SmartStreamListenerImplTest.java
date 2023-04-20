package com.angelbroking.smartapi.sample;

import com.angelbroking.smartapi.smartstream.models.LTP;
import com.angelbroking.smartapi.smartstream.models.Quote;
import com.angelbroking.smartapi.smartstream.models.SmartStreamError;
import com.angelbroking.smartapi.smartstream.models.SnapQuote;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;


@Slf4j
public class SmartStreamListenerImplTest {

    public static final ZoneId TZ_UTC = ZoneId.of("UTC");
    public static final ZoneId TZ_IST = ZoneId.of("Asia/Kolkata");
    public void onLTPArrival(LTP ltp) {
        String ltpData = String.format("token: %s sequenceNumber: %d ltp: %.2f exchangeTime: %s exchangeToClientLatency: %s",
                ltp.getToken().toString(),
                ltp.getSequenceNumber(),
                (ltp.getLastTradedPrice() / 100.0),
                getExchangeTime(ltp.getExchangeFeedTimeEpochMillis()),
                Instant.now().toEpochMilli() - ltp.getExchangeFeedTimeEpochMillis());
        log.info(ltpData);
    }

    private ZonedDateTime getExchangeTime(long exchangeFeedTimeEpochMillis) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(exchangeFeedTimeEpochMillis), TZ_IST);
    }

    public void onQuoteArrival(Quote quote) {
        String quoteData = String.format("token: %s sequenceNumber: %d ltp: %.2f open: %.2f high: %.2f low: %.2f close: %.2f exchangeTime: %s exchangeToClientLatency: %s",
                quote.getToken().toString(),
                quote.getSequenceNumber(),
                (quote.getLastTradedPrice() / 100.0),
                (quote.getOpenPrice() / 100.0),
                (quote.getHighPrice() / 100.0),
                (quote.getLowPrice() / 100.0),
                (quote.getClosePrice() / 100.0),
                getExchangeTime(quote.getExchangeFeedTimeEpochMillis()),
                Instant.now().toEpochMilli() - quote.getExchangeFeedTimeEpochMillis());
        log.info(quoteData);

    }

    public void onSnapQuoteArrival(SnapQuote snapQuote) {
        String snapQuoteData = String.format("token: %s sequenceNumber: %d ltp: %.2f open: %.2f high: %.2f low: %.2f close: %.2f exchangeTime: %s exchangeToClientLatency: %s",
                snapQuote.getToken().toString(),
                snapQuote.getSequenceNumber(),
                (snapQuote.getLastTradedPrice() / 100.0),
                (snapQuote.getOpenPrice() / 100.0),
                (snapQuote.getHighPrice() / 100.0),
                (snapQuote.getLowPrice() / 100.0),
                (snapQuote.getClosePrice() / 100.0),
                getExchangeTime(snapQuote.getExchangeFeedTimeEpochMillis()),
                Instant.now().toEpochMilli() - snapQuote.getExchangeFeedTimeEpochMillis());
        log.info(snapQuoteData);
    }

    public void onConnected() {
        log.info("web socket connected");

    }

    public void onError(SmartStreamError error) {
        log.error("An error occurred while processing the SmartStream: {}", error.getException().getMessage());
    }

    public void onPong() {
        log.info("pong received");
    }


}
