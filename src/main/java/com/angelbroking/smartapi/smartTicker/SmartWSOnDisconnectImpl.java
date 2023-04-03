package com.angelbroking.smartapi.smartTicker;

import com.angelbroking.smartapi.SmartConnect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmartWSOnDisconnectImpl  implements SmartWSOnDisconnect {
    private static final Logger logger = LoggerFactory.getLogger(SmartWSOnDisconnectImpl.class);
    @Override
    public void onDisconnected() {
        logger.info("Disconnected from server.");
    }
}
