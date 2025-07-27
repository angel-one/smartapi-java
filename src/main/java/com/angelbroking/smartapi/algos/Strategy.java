package com.angelbroking.smartapi.algos;

import com.angelbroking.smartapi.models.Candle;
import org.json.JSONObject;

import com.angelbroking.smartapi.algos.TradingAction;

import java.util.List;
import java.util.Map;

public interface Strategy {
    /**
     * Generates trading signals based on historical data and parameters.
     * Should return a list of TradingAction objects.
     */
    List<TradingAction> generateSignals(List<Candle> historicalData, JSONObject params);
    Map<String, List<Double>> getIndicatorData(List<Candle> historicalData, JSONObject params);
}