package com.angelbroking.smartapi.smartticker;

import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SmartWSOnErrorImpl   implements SmartWSOnError{

    @Override
    public void onError(Exception exception) {

        log.error("Error: {}", exception.getMessage());


    }

    @Override
    public void onError(SmartAPIException smartAPIException) {
        log.error("Error: {}", smartAPIException.getMessage());


    }
}
