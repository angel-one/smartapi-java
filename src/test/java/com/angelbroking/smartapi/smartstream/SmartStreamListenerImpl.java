package com.angelbroking.smartapi.smartstream;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;

import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import com.angelbroking.smartapi.smartstream.models.*;
import com.angelbroking.smartapi.smartstream.ticker.SmartStreamListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SmartStreamListenerImpl implements SmartStreamListener {
	private static final Logger log = LoggerFactory.getLogger(SmartStreamListenerImpl.class);
	public static final ZoneId TZ_UTC = ZoneId.of("UTC");
	public static final ZoneId TZ_IST = ZoneId.of("Asia/Kolkata");

	@Override
	public void onLTPArrival(LTP ltp) {
		ZonedDateTime exchangeTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(ltp.getExchangeFeedTimeEpochMillis()), TZ_IST);
		String ltpData = String.format(
				"subscriptionMode: %s exchangeType: %s token: %s sequenceNumber: %d ltp: %.2f exchangeTime: %s exchangeToClientLatency: %s",
				SmartStreamSubsMode.findByVal(ltp.getSubscriptionMode()),
				ltp.getExchangeType(), ltp.getToken().toString(), ltp.getSequenceNumber(),
				(ltp.getLastTradedPrice() / 100.0), exchangeTime,
				Instant.now().toEpochMilli() - ltp.getExchangeFeedTimeEpochMillis());
		log.info(ltpData);
	}

	@Override
	public void onQuoteArrival(Quote quote) {
		ZonedDateTime exchangeTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(quote.getExchangeFeedTimeEpochMillis()), TZ_IST);
		String data = String.format("token: %s"
				+ " sequenceNumber: %d"
				+ " ltp: %.2f"
				+ " open: %.2f"
				+ " high: %.2f"
				+ " low: %.2f"
				+ " close: %.2f"
				+ " exchangeTime: %s"
				+ " exchangeToClientLatency: %s",
				quote.getToken().toString(),
				quote.getSequenceNumber(),
				(quote.getLastTradedPrice() / 100.0),
				(quote.getOpenPrice() / 100.0),
				(quote.getHighPrice() / 100.0),
				(quote.getLowPrice() / 100.0),
				(quote.getClosePrice() / 100.0),
				exchangeTime,
				Instant.now().toEpochMilli() - quote.getExchangeFeedTimeEpochMillis());
		log.info(data);
	}

	@Override
	public void onSnapQuoteArrival(SnapQuote snapQuote) {
		String snapQuoteData = String.format(
				"subscriptionMode: %s exchangeType: %s token: %s sequenceNumber: %d ltp: %.2f lastTradedQty: %d avgTradedPrice: %.2f volumeTradedToday: %d totalBuyQty: %.2f totalSellQty: %.2f open: %.2f high: %.2f low: %.2f close: %.2f "
						+ "lastTradedTimestamp: %s openInterest: %.2f openInterestChangePerc: %.2f bestFiveBuyData: %s bestFiveSellData: %s upperCircuit: %.2f lowerCircuit: %.2f yearlyHighPrice: %.2f yearlyLowPrice: %.2f exchangeTime: %s exchangeToClientLatency: %s",
				SmartStreamSubsMode.findByVal(snapQuote.getSubscriptionMode()),
				snapQuote.getExchangeType(), snapQuote.getToken().toString(),
				snapQuote.getSequenceNumber(), (snapQuote.getLastTradedPrice() / 100.0), snapQuote.getLastTradedQty(),
				(snapQuote.getAvgTradedPrice() / 100.0), snapQuote.getVolumeTradedToday(), snapQuote.getTotalBuyQty(),
				snapQuote.getTotalSellQty(), (snapQuote.getOpenPrice() / 100.0), (snapQuote.getHighPrice() / 100.0),
				(snapQuote.getLowPrice() / 100.0), (snapQuote.getClosePrice() / 100.0),
				snapQuote.getLastTradedTimestamp(), (snapQuote.getOpenInterest() / 100.0),
				(snapQuote.getOpenInterestChangePerc()), Arrays.toString(snapQuote.getBestFiveBuy()),
				Arrays.toString(snapQuote.getBestFiveSell()), (snapQuote.getUpperCircuit() / 100.0),
				(snapQuote.getLowerCircuit() / 100.0), (snapQuote.getYearlyHighPrice() / 100.0),
				(snapQuote.getYearlyLowPrice() / 100.0), getExchangeTime(snapQuote.getExchangeFeedTimeEpochMillis()),
				Instant.now().toEpochMilli() - snapQuote.getExchangeFeedTimeEpochMillis());
		log.info(snapQuoteData);
	}

	@Override
	public void onDepthArrival(Depth depth) {
		String depthData = String.format(
				"subscriptionMode: %s exchangeType: %s token: %s exchangeTimeStamp: %s packetReceivedTime: %s bestTwentyBuyData: %s bestTwentySellData: %s",
				SmartStreamSubsMode.findByVal(depth.getSubscriptionMode()),
				depth.getExchangeType(), depth.getToken().toString(),
				depth.getExchangeTimeStamp(),
				depth.getPacketReceivedTime(),
				Arrays.toString(depth.getBestTwentyBuyData()),
				Arrays.toString(depth.getBestTwentySellData()));
		log.info(depthData);
	}

	private ZonedDateTime getExchangeTime(long exchangeFeedTimeEpochMillis) {
		return ZonedDateTime.ofInstant(Instant.ofEpochMilli(exchangeFeedTimeEpochMillis), TZ_IST);
	}
	
	@Override
	public void onConnected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onError(SmartStreamError error) {
		error.getException().printStackTrace();
	}

	@Override
	public void onPong() {
		log.info("pong received");
	}

	/* Custom on error method for SmartStream */
	@Override
	public SmartStreamError onErrorCustom() {
		// User thrown error return
		log.info("on custom error");
		SmartStreamError smartStreamError = new SmartStreamError();
		smartStreamError.setException(new SmartAPIException("custom error received"));
		return smartStreamError;
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}

}
