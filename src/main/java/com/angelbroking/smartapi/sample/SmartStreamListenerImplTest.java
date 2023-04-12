package com.angelbroking.smartapi.sample;

import com.angelbroking.smartapi.smartstream.models.LTP;
import com.angelbroking.smartapi.smartstream.models.Quote;
import com.angelbroking.smartapi.smartstream.models.SmartStreamError;
import com.angelbroking.smartapi.smartstream.models.SnapQuote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class SmartStreamListenerImplTest {
//		implements SmartStreamListener {

	private static final Logger logger = LoggerFactory.getLogger(SmartStreamListenerImplTest.class);


	public static final ZoneId TZ_UTC = ZoneId.of("UTC");
	public static final ZoneId TZ_IST = ZoneId.of("Asia/Kolkata");


	public void onLTPArrival(LTP ltp) {
		ZonedDateTime exchangeTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(ltp.getExchangeFeedTimeEpochMillis()), TZ_IST);
		String ltpData = String.format("token: %s"
				+ " sequenceNumber: %d"
				+ " ltp: %.2f"
				+ " exchangeTime: %s"
				+ " exchangeToClientLatency: %s",
				ltp.getToken().toString(),
				ltp.getSequenceNumber(),
				(ltp.getLastTradedPrice() / 100.0),
				exchangeTime,
				Instant.now().toEpochMilli() - ltp.getExchangeFeedTimeEpochMillis());
		logger.info(ltpData);
	}

	public void onQuoteArrival(Quote quote) {
		ZonedDateTime exchangeTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(quote.getExchangeFeedTimeEpochMillis()), TZ_IST);
		String quoteData = String.format("token: %s"
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
		logger.info(quoteData);
	}

	public void onSnapQuoteArrival(SnapQuote snapQuote) {
		ZonedDateTime exchangeTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(snapQuote.getExchangeFeedTimeEpochMillis()), TZ_IST);
		String snapQuoteData = String.format("token: %s"
				+ " sequenceNumber: %d"
				+ " ltp: %.2f"
				+ " open: %.2f"
				+ " high: %.2f"
				+ " low: %.2f"
				+ " close: %.2f"
				+ " exchangeTime: %s"
				+ " exchangeToClientLatency: %s",
				snapQuote.getToken().toString(),
				snapQuote.getSequenceNumber(),
				(snapQuote.getLastTradedPrice() / 100.0),
				(snapQuote.getOpenPrice() / 100.0),
				(snapQuote.getHighPrice() / 100.0),
				(snapQuote.getLowPrice() / 100.0),
				(snapQuote.getClosePrice() / 100.0),
				exchangeTime,
				Instant.now().toEpochMilli() - snapQuote.getExchangeFeedTimeEpochMillis());
		logger.info(snapQuoteData);
	}

	public void onConnected() {
		logger.info("web socket connected");

	}

	public void onError(SmartStreamError error) {
		logger.error("An error occurred while processing the SmartStream: " + error.getException().getMessage());
		error.getException().printStackTrace();
	}

	public void onPong() {
		logger.info("pong received");
	}
}
