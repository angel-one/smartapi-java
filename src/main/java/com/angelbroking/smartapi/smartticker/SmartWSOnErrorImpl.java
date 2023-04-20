package com.angelbroking.smartapi.smartticker;

import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmartWSOnErrorImpl   implements SmartWSOnError{
    private static final Logger logger = LoggerFactory.getLogger(SmartWSOnErrorImpl.class);

    @Override
    public void onError(Exception exception) {

        logger.error("Error: %s", exception.getMessage());

    }

    @Override
    public void onError(SmartAPIException smartAPIException) {
        logger.error("Error: %s", smartAPIException.getMessage());

    }
}
