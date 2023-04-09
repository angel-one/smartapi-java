package com.angelbroking.smartapi.ticker;

import lombok.Data;

@Data
public class SmartApiTickerParams {

    private final String clientId;
    private final String feedToken;
    private final String script;
    private final String task;

    public SmartApiTickerParams(String clientId, String feedToken, String script, String task) {
        this.clientId = clientId;
        this.feedToken = feedToken;
        this.script = script;
        this.task = task;
    }
}

