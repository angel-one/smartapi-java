package com.angelbroking.smartapi.smartticker.ticker;

import com.angelbroking.smartapi.http.exceptions.SmartAPIException;

public interface OnError {

    void onError(Exception exception);

    void onError(SmartAPIException smartAPIException);

    void onError(String error);
}
