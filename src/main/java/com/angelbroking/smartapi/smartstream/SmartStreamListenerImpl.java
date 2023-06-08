package com.angelbroking.smartapi.smartstream;

import com.angelbroking.smartapi.http.exceptions.SmartConnectException;
import com.angelbroking.smartapi.smartstream.models.ExchangeType;
import com.angelbroking.smartapi.smartstream.models.LTP;
import com.angelbroking.smartapi.smartstream.models.Quote;
import com.angelbroking.smartapi.smartstream.models.SmartStreamError;
import com.angelbroking.smartapi.smartstream.models.SmartStreamSubsMode;
import com.angelbroking.smartapi.smartstream.models.SnapQuote;
import com.angelbroking.smartapi.smartstream.ticker.SmartStreamListener;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;


@Slf4j
public class SmartStreamListenerImpl implements SmartStreamListener {
    public static final ZoneId TZ_IST = ZoneId.of("Asia/Kolkata");

    public void onLTPArrival(LTP ltp) {
        String ltpData = String.format("subscriptionMode: %s exchangeType: %s token: %s sequenceNumber: %d ltp: %.2f exchangeTime: %s exchangeToClientLatency: %s", SmartStreamSubsMode.findByVal(ltp.getSubscriptionMode()), ExchangeType.findByValue(ltp.getExchangeType()), ltp.getToken().toString(), ltp.getSequenceNumber(), (ltp.getLastTradedPrice() / 100.0), getExchangeTime(ltp.getExchangeFeedTimeEpochMillis()), Instant.now().toEpochMilli() - ltp.getExchangeFeedTimeEpochMillis());
        log.info(ltpData);
    }

    private ZonedDateTime getExchangeTime(long exchangeFeedTimeEpochMillis) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(exchangeFeedTimeEpochMillis), TZ_IST);
    }

    public void onQuoteArrival(Quote quote) {
        String quoteData = String.format("subscriptionMode: %s exchangeType: %s token: %s sequenceNumber: %d ltp: %.2f lastTradedQty: %d avgTradedPrice: %.2f volumeTradedToday: %d totalBuyQty: %.2f totalSellQty: %.2f open: %.2f high: %.2f low: %.2f close: %.2f exchangeTime: %s exchangeToClientLatency: %s", SmartStreamSubsMode.findByVal(quote.getSubscriptionMode()), ExchangeType.findByValue(quote.getExchangeType()), quote.getToken().toString(), quote.getSequenceNumber(), (quote.getLastTradedPrice() / 100.0), quote.getLastTradedQty(), (quote.getAvgTradedPrice() / 100.0), quote.getVolumeTradedToday(), quote.getTotalBuyQty(), quote.getTotalSellQty(), (quote.getOpenPrice() / 100.0), (quote.getHighPrice() / 100.0), (quote.getLowPrice() / 100.0), (quote.getClosePrice() / 100.0), getExchangeTime(quote.getExchangeFeedTimeEpochMillis()), Instant.now().toEpochMilli() - quote.getExchangeFeedTimeEpochMillis());
        log.info(quoteData);

    }

    public void onSnapQuoteArrival(SnapQuote snapQuote) {
        String snapQuoteData = String.format("subscriptionMode: %s exchangeType: %s token: %s sequenceNumber: %d ltp: %.2f lastTradedQty: %d avgTradedPrice: %.2f volumeTradedToday: %d totalBuyQty: %.2f totalSellQty: %.2f open: %.2f high: %.2f low: %.2f close: %.2f " + "lastTradedTimestamp: %s openInterest: %.2f openInterestChangePerc: %.2f bestFiveBuyData: %s bestFiveSellData: %s upperCircuit: %.2f lowerCircuit: %.2f yearlyHighPrice: %.2f yearlyLowPrice: %.2f exchangeTime: %s exchangeToClientLatency: %s", SmartStreamSubsMode.findByVal(snapQuote.getSubscriptionMode()), ExchangeType.findByValue(snapQuote.getExchangeType()), snapQuote.getToken().toString(), snapQuote.getSequenceNumber(), (snapQuote.getLastTradedPrice() / 100.0), snapQuote.getLastTradedQty(), (snapQuote.getAvgTradedPrice() / 100.0), snapQuote.getVolumeTradedToday(), snapQuote.getTotalBuyQty(), snapQuote.getTotalSellQty(), (snapQuote.getOpenPrice() / 100.0), (snapQuote.getHighPrice() / 100.0), (snapQuote.getLowPrice() / 100.0), (snapQuote.getClosePrice() / 100.0), getExchangeTime(snapQuote.getLastTradedTimestamp()), (snapQuote.getOpenInterest() / 100.0), (snapQuote.getOpenInterestChangePerc()), Arrays.toString(snapQuote.getBestFiveBuy()), Arrays.toString(snapQuote.getBestFiveSell()), (snapQuote.getUpperCircuit() / 100.0), (snapQuote.getLowerCircuit() / 100.0), (snapQuote.getYearlyHighPrice() / 100.0), (snapQuote.getYearlyLowPrice() / 100.0), getExchangeTime(snapQuote.getExchangeFeedTimeEpochMillis()), Instant.now().toEpochMilli() - snapQuote.getExchangeFeedTimeEpochMillis());
        log.info(snapQuoteData);
    }

    public void onConnected() {
        log.info("web socket connected");

    }

    public void onError(SmartStreamError error) {
        log.error("An error occurred while processing the SmartStream: {}", error.getException().getMessage());
        throw new SmartConnectException("An error occurred while processing the SmartStream");
    }

    public void onPong() {
        log.info("pong received");
    }


}
