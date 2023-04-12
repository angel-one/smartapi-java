package com.angelbroking.smartapi.sample.ticker;

import com.angelbroking.smartapi.http.exceptions.SmartAPIException;

public interface OnError {

	public void onError(Exception exception);

	public void onError(SmartAPIException smartAPIException);

	void onError(String error);
}
