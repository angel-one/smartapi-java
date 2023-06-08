package com.angelbroking.smartapi.smartticker;

import com.angelbroking.smartapi.http.exceptions.SmartAPIException;

public interface SmartWSOnError {

	 void onError(Exception exception);

	 void onError(SmartAPIException smartAPIException);

}
 