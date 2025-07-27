package com.angelbroking.smartapi.algos.strategies;

import com.angelbroking.smartapi.algos.TradingAction;
import com.angelbroking.smartapi.algos.Signal;
import com.angelbroking.smartapi.algos.Strategy;
import com.angelbroking.smartapi.algos.ActionType;
import com.angelbroking.smartapi.models.Candle;
import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RsiDeviationStrategy implements Strategy {
    private static final Logger log = LoggerFactory.getLogger(RsiDeviationStrategy.class);

    // Store parameters passed during initialization/run
    private JSONObject params;

    public String getName() {
        // Include parameters in name for clarity in logs/GUI
        return "RSI Deviation (" + params.optInt("rsiPeriod", 14) + ")";
    }

    @Override
    public List<TradingAction> generateSignals(List<Candle> historicalData, JSONObject params) {
        this.params = params; // Store params for getName() and TradingAction creation
        List<TradingAction> signals = new ArrayList<>(); // Changed from List<Signal> to List<TradingAction>
        if (historicalData == null || historicalData.isEmpty()) {
            log.warn("[{}] Historical data is empty or null. Cannot generate RSI signals.", getName());
            return signals;
        }

        // Enhanced parameters with better defaults
        String symbolToken = params.optString("symbolToken", "UNKNOWN");
        String tradingSymbol = params.optString("tradingSymbol", "UNKNOWN");
        String exchange = params.optString("exchange", "UNKNOWN");
        String productType = params.optString("productType", "INTRADAY"); // Get product type from params

        int rsiPeriod = params.optInt("rsiPeriod", 14);
        double oversoldThreshold = params.optDouble("oversoldThreshold", 25.0);
        double overboughtThreshold = params.optDouble("overboughtThreshold", 75.0);
        double rsiConfirmationBuffer = params.optDouble("rsiConfirmationBuffer", 2.0);

        // Enhanced price stability parameters
        double priceStabilityThresholdPercent = params.optDouble("priceStabilityThresholdPercent", 0.15); // Increased from 0.05%
        double minRsiPositiveDifference = params.optDouble("minRsiPositiveDifference", 2.0); // Increased threshold

        // Enhanced control parameters
        int minCandlesBetweenTrades = params.optInt("minCandlesBetweenTrades", 10); // Increased gap
        double minProfitTarget = params.optDouble("minProfitTarget", 0.2); // Percentage
        double stopLossPercent = params.optDouble("stopLossPercent", 0.4);

        // Market timing parameters
        boolean avoidMarketOpenClose = params.optBoolean("avoidMarketOpenClose", true);
        int marketOpenAvoidMinutes = params.optInt("marketOpenAvoidMinutes", 15); // Avoid first 15 min
        int marketCloseAvoidMinutes = params.optInt("marketCloseAvoidMinutes", 15); // Avoid last 15 min

        log.info("[{}] Enhanced parameters - Price Stability: {}%, Min RSI Diff: {}, Gap: {}, Avoid Open/Close: {}",
                getName(), priceStabilityThresholdPercent, minRsiPositiveDifference, minCandlesBetweenTrades, avoidMarketOpenClose);
        log.info("[{}] Backtest Params: Token={}, Exchange={}, Product={}, Interval={}",
                getName(), symbolToken, exchange, productType, params.optString("interval", "UNKNOWN"));

        double[] closePrices = historicalData.stream().mapToDouble(Candle::getClose).toArray();

        if (closePrices.length < rsiPeriod + 20) { // Increased minimum data requirement
            log.warn("[{}] Not enough data for RSI calculation. Data points: {}, Need at least: {}",
                    getName(), closePrices.length, rsiPeriod + 20);
            return signals;
        }

        Core taLib = new Core();
        double[] rsiValues = new double[closePrices.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();

        RetCode retCode = taLib.rsi(0, closePrices.length - 1, closePrices, rsiPeriod, begin, length, rsiValues);

        if (retCode != RetCode.Success || length.value == 0) {
            log.error("[{}] TA-Lib RSI calculation failed. RetCode: {}", getName(), retCode);
            return signals;
        }

        boolean inPosition = false;
        int lastTradeIndex = -minCandlesBetweenTrades;
        double entryPrice = 0.0;

        // Increased stabilization period
        int rsiSignalStabilizationCount = params.optInt("rsiSignalStabilizationCount", 15); // Increased from 5

        if (length.value < rsiSignalStabilizationCount + 10) {
            log.warn("[{}] Not enough RSI values after stabilization. Available: {}, Need: {}",
                    getName(), length.value, rsiSignalStabilizationCount + 10);
            return signals;
        }

        // Calculate volume-based filter if available
        double[] volumes = historicalData.stream().mapToDouble(candle -> candle.getVolume() == 0L ? 1.0 : candle.getVolume()).toArray();
        double avgVolume = calculateAverageVolume(volumes, Math.min(20, volumes.length));

        for (int i = rsiSignalStabilizationCount + 5; i < length.value - 5; i++) { // Avoid last 5 candles too
            int candleIndex = begin.value + i;
            if (candleIndex >= historicalData.size() - 5 || candleIndex - 5 < 0) continue; // Enhanced boundary check

            double currentRsi = rsiValues[i];
            double previousRsi = rsiValues[i - 1];
            double prevPrevRsi = rsiValues[i - 2];

            Candle currentCandle = historicalData.get(candleIndex);
            Candle previousCandle = historicalData.get(candleIndex - 1);

            double currentPrice = currentCandle.getClose();
            double previousPrice = previousCandle.getClose();

            // Enhanced market timing filter
            if (avoidMarketOpenClose && isNearMarketOpenOrClose(currentCandle, marketOpenAvoidMinutes, marketCloseAvoidMinutes)) {
                continue;
            }

            // Volume filter - avoid low volume periods
            double currentVolume = currentCandle.getVolume() == 0L ? 1.0 : currentCandle.getVolume();
            if (currentVolume < avgVolume * 0.5) { // Skip if volume is less than 50% of average
                continue;
            }

            // Anti-overtrading: ensure minimum gap between trades
            boolean canTrade = (candleIndex - lastTradeIndex) >= minCandlesBetweenTrades;

            // Enhanced RSI stability check - ensure RSI has been stable for multiple periods
            boolean rsiIsStable = isRsiStable(rsiValues, i, 3, 2.0); // Check last 3 periods for stability

            // ============ ENHANCED PRICE STABILITY + RSI DIVERGENCE LOGIC ============

            // Multi-period price stability check
            double priceDifferencePercent = Math.abs(currentPrice - previousPrice) / previousPrice * 100.0;
            boolean pricesAreSimilar = priceDifferencePercent <= priceStabilityThresholdPercent;

            // Enhanced price stability - check multiple periods
            boolean enhancedPriceStability = false;
            if (candleIndex >= 3) {
                double price3PeriodsAgo = historicalData.get(candleIndex - 3).getClose();
                double maxPriceInRange = Math.max(Math.max(currentPrice, previousPrice), price3PeriodsAgo);
                double minPriceInRange = Math.min(Math.min(currentPrice, previousPrice), price3PeriodsAgo);
                double priceRangePercent = (maxPriceInRange - minPriceInRange) / minPriceInRange * 100.0;
                enhancedPriceStability = priceRangePercent <= priceStabilityThresholdPercent * 2; // Allow slightly more range
            }

            // Check RSI positive movement with enhanced validation
            double rsiDifference = currentRsi - previousRsi;
            boolean rsiHasPositiveDifference = rsiDifference >= minRsiPositiveDifference;

            // Enhanced RSI trend confirmation - check longer trend
            boolean rsiTrendUp = isRsiTrendUp(rsiValues, i, 3); // Check 3-period trend
            boolean rsiTrendDown = isRsiTrendDown(rsiValues, i, 3);

            // ============ ENHANCED BUY SIGNAL CONDITIONS ============

            // 1. Strong Oversold Recovery (more conservative)
            boolean strongOversoldBuy = !inPosition && canTrade && rsiIsStable &&
                    previousRsi <= (oversoldThreshold - 2) && // More oversold
                    currentRsi >= (oversoldThreshold + rsiConfirmationBuffer) &&
                    rsiTrendUp;

            // 2. Enhanced Price Stability + RSI Divergence
            boolean priceStableRsiDivergenceBuy = !inPosition && canTrade && rsiIsStable &&
                    enhancedPriceStability &&
                    rsiHasPositiveDifference &&
                    currentRsi > 35 && currentRsi < 65 && // Tighter range
                    rsiTrendUp;

            // 3. Volume-confirmed momentum buy
            boolean volumeConfirmedBuy = !inPosition && canTrade &&
                    currentRsi > 40 && currentRsi < 60 &&
                    rsiTrendUp &&
                    currentVolume > avgVolume * 1.2 && // Above average volume
                    priceDifferencePercent <= 0.2;

            // ============ ENHANCED SELL SIGNAL CONDITIONS ============

            // 1. Strong Overbought Sell (more conservative)
            boolean strongOverboughtSell = inPosition &&
                    previousRsi >= (overboughtThreshold + 2) && // More overbought
                    currentRsi <= (overboughtThreshold - rsiConfirmationBuffer) &&
                    rsiTrendDown;

            // 2. Enhanced Price Stability + RSI Negative Divergence
            boolean priceStableRsiNegativeDivergence = inPosition &&
                    enhancedPriceStability &&
                    rsiDifference <= -minRsiPositiveDifference &&
                    currentRsi > 45; // Above midline

            // 3. Enhanced profit target and stop loss
            boolean profitTargetHit = inPosition && entryPrice > 0 &&
                    ((currentPrice - entryPrice) / entryPrice * 100.0) >= minProfitTarget;

            boolean stopLossHit = inPosition && entryPrice > 0 &&
                    ((entryPrice - currentPrice) / entryPrice * 100.0) >= stopLossPercent;

            // 4. Time-based exit (avoid holding overnight if applicable)
            boolean timeBasedExit = inPosition && (candleIndex - lastTradeIndex) > 50; // Max 50 candles holding

            // ============ EXECUTE TRADES ============

            if (strongOversoldBuy) {
                // Create TradingAction instead of Signal
                signals.add(new TradingAction(currentCandle.getTimestamp(), ActionType.BUY, symbolToken, tradingSymbol,
                        exchange, params.optInt("quantity", 1), currentPrice, 0, // Assuming Market order, no trigger price
                        "MARKET", productType, "NORMAL", "DAY")); // Use default order params
                inPosition = true;
                entryPrice = currentPrice;
                lastTradeIndex = candleIndex;
                log.info("[{}] STRONG OVERSOLD BUY at {}: RSI({:.1f}→{:.1f}) Price: {:.2f} Vol: {:.0f}",
                        getName(), currentCandle.getTimestamp(), previousRsi, currentRsi, currentPrice, currentVolume);

            } else if (priceStableRsiDivergenceBuy) {
                // Create TradingAction instead of Signal
                signals.add(new TradingAction(currentCandle.getTimestamp(), ActionType.BUY, symbolToken, tradingSymbol,
                        exchange, params.optInt("quantity", 1), currentPrice, 0,
                        "MARKET", productType, "NORMAL", "DAY"));
                inPosition = true;
                entryPrice = currentPrice;
                lastTradeIndex = candleIndex;
                log.info("[{}] ENHANCED DIVERGENCE BUY at {}: Price Stability: {:.3f}%, RSI Diff: +{:.1f} ({:.1f}→{:.1f}) Price: {:.2f}",
                        getName(), currentCandle.getTimestamp(), priceDifferencePercent, rsiDifference, previousRsi, currentRsi, currentPrice);

            } else if (volumeConfirmedBuy) {
                // Create TradingAction instead of Signal
                signals.add(new TradingAction(currentCandle.getTimestamp(), ActionType.BUY, symbolToken, tradingSymbol,
                        exchange, params.optInt("quantity", 1), currentPrice, 0,
                        "MARKET", productType, "NORMAL", "DAY"));
                inPosition = true;
                entryPrice = currentPrice;
                lastTradeIndex = candleIndex;
                log.info("[{}] VOLUME MOMENTUM BUY at {}: RSI({:.1f}→{:.1f}) Price: {:.2f} Vol: {:.0f} (Avg: {:.0f})",
                        getName(), currentCandle.getTimestamp(), previousRsi, currentRsi, currentPrice, currentVolume, avgVolume);

            } else if (strongOverboughtSell || priceStableRsiNegativeDivergence || profitTargetHit || stopLossHit || timeBasedExit) {
                String sellReason = strongOverboughtSell ? "OVERBOUGHT" :
                        priceStableRsiNegativeDivergence ? "DIVERGENCE_NEGATIVE" :
                                profitTargetHit ? "PROFIT_TARGET" :
                                        timeBasedExit ? "TIME_EXIT" : "STOP_LOSS";

                // Create TradingAction instead of Signal
                signals.add(new TradingAction(currentCandle.getTimestamp(), ActionType.SELL, symbolToken, tradingSymbol,
                        exchange, params.optInt("quantity", 1), currentPrice, 0, // Use currentPrice as exit price
                        "MARKET", productType, "NORMAL", "DAY"));

                double profitPercent = entryPrice > 0 ? ((currentPrice - entryPrice) / entryPrice * 100.0) : 0;
                log.info("[{}] {} SELL at {}: RSI({:.1f}→{:.1f}) Price: {:.2f} P&L: {:.2f}%",
                        getName(), sellReason, currentCandle.getTimestamp(), previousRsi, currentRsi, currentPrice, profitPercent);

                inPosition = false;
                entryPrice = 0.0;
                lastTradeIndex = candleIndex;
            }
        }

        log.info("[{}] Generated {} signals with enhanced anti-early/late trading logic.", getName(), signals.size());
        log.info("[{}] Generated {} trading actions.", getName(), signals.size());
        return signals;
    }

    // Helper methods
    private boolean isNearMarketOpenOrClose(Candle candle, int openAvoidMinutes, int closeAvoidMinutes) {
        try {
            Date timestampDate = candle.getTimestamp();
            if (timestampDate == null) {
                log.debug("Candle timestamp is null for candle: {}", candle);
                return false;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timeStr = sdf.format(timestampDate);

            // Extract time part (assuming format like "2025-03-17 09:15:00")
            if (timeStr.contains(" ")) {
                String timePart = timeStr.split(" ")[1];
                LocalTime candleTime = LocalTime.parse(timePart);

                LocalTime marketOpen = LocalTime.of(9, 15);
                LocalTime marketClose = LocalTime.of(15, 30);
                LocalTime avoidOpenUntil = marketOpen.plusMinutes(openAvoidMinutes);
                LocalTime avoidCloseFrom = marketClose.minusMinutes(closeAvoidMinutes);

                return candleTime.isBefore(avoidOpenUntil) || candleTime.isAfter(avoidCloseFrom);
            }
        } catch (Exception e) {
            log.debug("Could not parse time from candle timestamp: {}", candle.getTimestamp());
        }
        return false;
    }

    private boolean isRsiStable(double[] rsiValues, int currentIndex, int lookbackPeriods, double maxDeviation) {
        if (currentIndex < lookbackPeriods) return false;

        double avgRsi = 0;
        for (int i = currentIndex - lookbackPeriods + 1; i <= currentIndex; i++) {
            avgRsi += rsiValues[i];
        }
        avgRsi /= lookbackPeriods;

        for (int i = currentIndex - lookbackPeriods + 1; i <= currentIndex; i++) {
            if (Math.abs(rsiValues[i] - avgRsi) > maxDeviation) {
                return false;
            }
        }
        return true;
    }

    private boolean isRsiTrendUp(double[] rsiValues, int currentIndex, int periods) {
        if (currentIndex < periods) return false;
        for (int i = 1; i < periods; i++) {
            if (rsiValues[currentIndex - i + 1] <= rsiValues[currentIndex - i]) {
                return false;
            }
        }
        return true;
    }

    private boolean isRsiTrendDown(double[] rsiValues, int currentIndex, int periods) {
        if (currentIndex < periods) return false;
        for (int i = 1; i < periods; i++) {
            if (rsiValues[currentIndex - i + 1] >= rsiValues[currentIndex - i]) {
                return false;
            }
        }
        return true;
    }

    private double calculateAverageVolume(double[] volumes, int periods) {
        double sum = 0;
        int count = Math.min(periods, volumes.length);
        for (int i = volumes.length - count; i < volumes.length; i++) {
            sum += volumes[i];
        }
        return sum / count;
    }

    @Override
    public Map<String, List<Double>> getIndicatorData(List<Candle> historicalData, JSONObject params) {
        int rsiPeriod = params.optInt("rsiPeriod", 14);
        double[] closePrices = historicalData.stream().mapToDouble(Candle::getClose).toArray();

        if (closePrices.length < rsiPeriod) {
            return Map.of();
        }

        Core taLib = new Core();
        double[] rsiOut = new double[closePrices.length];
        MInteger outBegIdx = new MInteger();
        MInteger outNBElement = new MInteger();

        RetCode retCode = taLib.rsi(0, closePrices.length - 1, closePrices, rsiPeriod, outBegIdx, outNBElement, rsiOut);

        if (retCode == RetCode.Success && outNBElement.value > 0) {
            List<Double> rsiValuesList = new ArrayList<>();
            List<Double> rsiUpperBand = new ArrayList<>();
            List<Double> rsiLowerBand = new ArrayList<>();

            double overbought = params.optDouble("overboughtThreshold", 75.0);
            double oversold = params.optDouble("oversoldThreshold", 25.0);

            for (int i = 0; i < outBegIdx.value; i++) {
                rsiValuesList.add(Double.NaN);
                rsiUpperBand.add(Double.NaN);
                rsiLowerBand.add(Double.NaN);
            }
            for (int i = 0; i < outNBElement.value; i++) {
                rsiValuesList.add(rsiOut[i]);
                rsiUpperBand.add(overbought);
                rsiLowerBand.add(oversold);
            }

            Map<String, List<Double>> indicators = new HashMap<>();
            indicators.put("RSI(" + rsiPeriod + ")", rsiValuesList);
            indicators.put("RSI_Overbought", rsiUpperBand);
            indicators.put("RSI_Oversold", rsiLowerBand);
            return indicators;
        }
        return Map.of();
    }
}