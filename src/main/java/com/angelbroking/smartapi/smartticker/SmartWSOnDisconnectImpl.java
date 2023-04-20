package com.angelbroking.smartapi.smartticker;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SmartWSOnDisconnectImpl  implements SmartWSOnDisconnect {

    @Override
    public void onDisconnected() {
        log.info("Disconnected from server.");
    }
}
