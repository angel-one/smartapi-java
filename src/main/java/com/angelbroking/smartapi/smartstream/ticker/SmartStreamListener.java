package com.angelbroking.smartapi.smartstream.ticker;

import com.angelbroking.smartapi.smartstream.models.LTP;
import com.angelbroking.smartapi.smartstream.models.Quote;
import com.angelbroking.smartapi.smartstream.models.SmartStreamError;
import com.angelbroking.smartapi.smartstream.models.SnapQuote;

public interface SmartStreamListener {
	void onLTPArrival(LTP ltp);
	void onQuoteArrival(Quote quote);
	void onSnapQuoteArrival(SnapQuote snapQuote);
	
	void onConnected();
	void onError(SmartStreamError error);
	void onPong();
}