package com.angelbroking.smartapi.smartticker.ticker;

import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OnErrorImp implements OnError {

    @Override
    public void onError(Exception exception) {

        log.error(exception.getMessage());
    }

    @Override
    public void onError(SmartAPIException smartAPIException) {
        log.error(smartAPIException.getMessage());
    }

    @Override
    public void onError(String error) {

        log.error(error);
    }
}
