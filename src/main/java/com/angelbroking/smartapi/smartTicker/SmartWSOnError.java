package com.angelbroking.smartapi.smartTicker;

import com.angelbroking.smartapi.http.exceptions.SmartAPIException;

public interface SmartWSOnError {

	void onError(Exception exception);

	void onError(SmartAPIException smartAPIException);

	void onError(String error);
}
 