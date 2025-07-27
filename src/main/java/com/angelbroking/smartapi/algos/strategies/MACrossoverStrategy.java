package com.angelbroking.smartapi.algos.strategies;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.Arrays;

import org.json.JSONObject;

import com.angelbroking.smartapi.SmartConnect;
import com.angelbroking.smartapi.algos.ActionType;
import com.angelbroking.smartapi.algos.Strategy; // For order constants
import com.angelbroking.smartapi.algos.TradingAction;
import com.angelbroking.smartapi.models.Candle;
import com.angelbroking.smartapi.models.Tick;
import com.angelbroking.smartapi.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MACrossoverStrategy implements Strategy {
    private SmartConnect smartConnect;
    private String symbolToken;
    private String tradingSymbol;
    private String exchange;
    private int shortPeriod;
    private int longPeriod;
    private int quantity;
    private String productType; // Added product type field

    private static final Logger log = LoggerFactory.getLogger(MACrossoverStrategy.class);

    private List<Double> closePrices = new ArrayList<>();
    private boolean positionOpen = false; // True if we have an open long position

    public String getName() {
        // Include parameters in name for clarity in logs/GUI
        return "MA Crossover (" + shortPeriod + "/" + longPeriod + ") [" + productType + "]";
    }

    public void init(SmartConnect smartConnect, JSONObject params) {
        this.smartConnect = smartConnect; // Store for potential live trading (can be null for backtesting)
        this.symbolToken = params.getString("symbolToken");
        this.tradingSymbol = params.optString("tradingSymbol", this.symbolToken); // Fallback to token if no symbol
        this.exchange = params.getString("exchange");
        this.shortPeriod = params.getInt("shortPeriod");
        this.longPeriod = params.getInt("longPeriod");
        this.quantity = params.optInt("quantity", 1); // Default quantity to 1 if not specified

        this.productType = params.optString("productType", "INTRADAY"); // Get product type from params

        // Reset state for new initialization (e.g., during backtesting a new period)
        log.info("[{}] Initializing strategy with params: {}", getName(), params.toString());
        this.closePrices = new ArrayList<>(); // Re-initialize list
        this.positionOpen = false;
    }

    public void onCandle(Candle candle) {
        if (candle != null) {
            closePrices.add(candle.getClose());
            // log.trace("[{}] Added close price: {} from candle: {}", getName(), candle.getClose(), candle.getTimestamp());
            // Ensure we don't keep an infinitely growing list if not needed for very long MAs
            // For this strategy, we need at least `longPeriod` prices.
            // A more robust implementation might use a circular buffer or fixed-size list.
            if (closePrices.size() > longPeriod + 50) { // Keep some buffer
                closePrices = closePrices.stream().skip(closePrices.size() - (longPeriod + 50)).collect(Collectors.toList());
            }
        }
    }

    public void onTick(Tick tick) {
        // This strategy is candle-based, so onTick might not be used directly for signals.
        // However, you could use it to update LTP for trailing stops or real-time P&L.
        // For now, we'll leave it empty.
    }

    public TradingAction getActionSignal(Candle currentCandle) {
        // `currentCandle` is the latest, potentially incomplete candle in live mode,
        // or the current candle being processed in backtesting.
        // The `onCandle` method should have already added its close price to `closePrices`
        // if it's a completed candle. For `getActionSignal`, we operate on the historical `closePrices`.

        if (closePrices.size() < longPeriod) {
            log.debug("[{}] Not enough data for MA calculation. Close prices count: {}, Required: {}", getName(), closePrices.size(), longPeriod);
            // Pass currentCandle's timestamp, or null if not strictly needed for HOLD signal processing later
            return new TradingAction(currentCandle != null ? currentCandle.getTimestamp() : null, ActionType.HOLD, symbolToken);
        }

        double shortMA = calculateMA(shortPeriod);
        double longMA = calculateMA(longPeriod);
        log.debug("[{}] Calculated MAs - ShortMA({}): {}, LongMA({}): {}", getName(), shortPeriod, shortMA, longPeriod, longMA);

        // Need previous MAs to detect crossover
        if (closePrices.size() < longPeriod + 1) {
            log.debug("[{}] Not enough data for previous MA calculation. Close prices count: {}, Required: {}", getName(), closePrices.size(), longPeriod + 1);
            return new TradingAction(currentCandle != null ? currentCandle.getTimestamp() : null, ActionType.HOLD, symbolToken); // Not enough data for previous MAs
        }
        double prevShortMA = calculatePreviousMA(shortPeriod, 1);
        double prevLongMA = calculatePreviousMA(longPeriod, 1);
        log.debug("[{}] Calculated Previous MAs - PrevShortMA({}): {}, PrevLongMA({}): {}", getName(), shortPeriod, prevShortMA, longPeriod, prevLongMA);

        // Buy signal: short MA crosses above long MA
        if (prevShortMA <= prevLongMA && shortMA > longMA && !positionOpen) {
            positionOpen = true;
            log.info("[{}] BUY signal triggered at price of current candle {}: ShortMA ({}) crossed above LongMA ({}). PrevShortMA: {:.2f}, PrevLongMA: {:.2f}, CurrentShortMA: {:.2f}, CurrentLongMA: {:.2f}",
                    getName(), currentCandle != null ? currentCandle.getClose() : "N/A", shortPeriod, longPeriod, prevShortMA, prevLongMA, shortMA, longMA);
            // Use currentCandle.getClose() as the price for the signal of a MARKET order
            // Use the productType stored in the strategy instance
            return new TradingAction(currentCandle.getTimestamp(), ActionType.BUY, symbolToken, tradingSymbol, exchange, quantity, currentCandle.getClose(), 0, Constants.ORDER_TYPE_MARKET, productType,
                                     Constants.VARIETY_NORMAL, Constants.DURATION_DAY);
        }
        // Sell signal (to close long position): short MA crosses below long MA
        else if (prevShortMA >= prevLongMA && shortMA < longMA && positionOpen) {
            positionOpen = false;
            log.info("[{}] SELL signal triggered at price of current candle {}: ShortMA ({}) crossed below LongMA ({}). PrevShortMA: {:.2f}, PrevLongMA: {:.2f}, CurrentShortMA: {:.2f}, CurrentLongMA: {:.2f}",
                    getName(), currentCandle != null ? currentCandle.getClose() : "N/A", shortPeriod, longPeriod, prevShortMA, prevLongMA, shortMA, longMA);
            // Use currentCandle.getClose() as the price for the signal of a MARKET order
            // Use the productType stored in the strategy instance
            return new TradingAction(currentCandle.getTimestamp(), ActionType.SELL, symbolToken, tradingSymbol, exchange, quantity, currentCandle.getClose(), 0, Constants.ORDER_TYPE_MARKET, productType,
                                     Constants.VARIETY_NORMAL, Constants.DURATION_DAY);
        }

        return new TradingAction(currentCandle != null ? currentCandle.getTimestamp() : null, ActionType.HOLD, symbolToken);
    }

    private double calculateMA(int period) {
        if (closePrices.size() < period) return 0.0;
        return closePrices.stream().skip(closePrices.size() - period).mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    private double calculatePreviousMA(int period, int lookback) {
        if (closePrices.size() < period + lookback) return 0.0;
        return closePrices.stream().skip(closePrices.size() - period - lookback).limit(period).mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    @Override
    public List<TradingAction> generateSignals(List<Candle> historicalData, JSONObject params) {
        // Initialize the strategy state for this specific run
        // Assumes this.smartConnect is either already set or can be null if not used by init for backtesting logic
        this.init(null, params); // Pass null for smartConnect during backtesting

        List<TradingAction> signals = new ArrayList<>(); // Changed from List<Signal> to List<TradingAction>
        for (Candle candle : historicalData) {
            onCandle(candle); // Update internal state (closePrices) with the current candle
            log.trace("[{}] Processing candle for generateSignals: {}", getName(), candle.getTimestamp());
            TradingAction action = getActionSignal(candle); // Get signal based on the updated state
            
            // Add action to signals list if it's not a HOLD
            // Also, associate the candle's timestamp with the action if needed (not done here)
            if (action.getActionType() != ActionType.HOLD) {
                signals.add((TradingAction) action); // Cast to TradingAction
                log.debug("[{}] Added signal: {} for candle at {}", getName(), action.getActionType(), candle.getTimestamp());
            }
        }
        return signals;
    }

    @Override
    public Map<String, List<Double>> getIndicatorData(List<Candle> historicalData, JSONObject params) {
        // This method calculates indicator values over a given historical dataset.
        // It should not modify the primary state of the strategy instance (this.closePrices, this.positionOpen).
        // It uses parameters passed to it, or falls back to strategy's initialized parameters.

        List<Double> localClosePrices = historicalData.stream()
                                            .map(Candle::getClose)
                                            .collect(Collectors.toList());

        List<Double> shortMAList = new ArrayList<>();
        List<Double> longMAList = new ArrayList<>();

        // Use periods from params if provided, otherwise use the strategy's configured periods
        int currentShortPeriod = params.has("shortPeriod") ? params.getInt("shortPeriod") : this.shortPeriod;
        int currentLongPeriod = params.has("longPeriod") ? params.getInt("longPeriod") : this.longPeriod;
        
        // Ensure periods are valid if they were not set by init (e.g. this.shortPeriod is 0)
        if (currentShortPeriod <= 0 || currentLongPeriod <= 0) {
            // Or throw an IllegalArgumentException
            // For now, returning empty data or NaNs might be acceptable depending on requirements
            Map<String, List<Double>> errorIndicators = new HashMap<>();
            // Optionally add an error message or return empty map
            // errorIndicators.put("error", Arrays.asList(-1.0)); // Example error indication
            log.warn("[{}] Invalid periods for MA calculation in getIndicatorData. Short: {}, Long: {}", getName(), currentShortPeriod, currentLongPeriod);
            return errorIndicators; // Return empty map
        }


        for (int i = 0; i < localClosePrices.size(); i++) {
            // Calculate Short MA
            if (i >= currentShortPeriod - 1) {
                double ma = localClosePrices.subList(i - currentShortPeriod + 1, i + 1)
                                    .stream().mapToDouble(Double::doubleValue).average().orElse(Double.NaN);
                shortMAList.add(ma);
            } else {
                shortMAList.add(Double.NaN); // Not enough data for MA
            }

            // Calculate Long MA
            if (i >= currentLongPeriod - 1) {
                double ma = localClosePrices.subList(i - currentLongPeriod + 1, i + 1)
                                    .stream().mapToDouble(Double::doubleValue).average().orElse(Double.NaN);
                longMAList.add(ma);
            } else {
                longMAList.add(Double.NaN); // Not enough data for MA
            }
        }

        Map<String, List<Double>> indicators = new HashMap<>();
        indicators.put("SMA(" + currentShortPeriod + ")", shortMAList);
        indicators.put("SMA(" + currentLongPeriod + ")", longMAList);
        // Optionally, include timestamps or other candle data for alignment if plotting
        // List<String> timestamps = historicalData.stream().map(Candle::getTimestampString).collect(Collectors.toList());
        // indicators.put("timestamps", historicalData.stream().map(c -> (double) c.getTimestamp().getTime()).collect(Collectors.toList()));
        return indicators;
    }
}