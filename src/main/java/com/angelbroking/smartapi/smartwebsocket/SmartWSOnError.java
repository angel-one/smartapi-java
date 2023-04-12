package com.angelbroking.smartapi.smartwebsocket;

import com.angelbroking.smartapi.http.exceptions.SmartAPIException;

public interface SmartWSOnError {

	public void onError(Exception exception);

	public void onError(SmartAPIException smartAPIException);

}
 