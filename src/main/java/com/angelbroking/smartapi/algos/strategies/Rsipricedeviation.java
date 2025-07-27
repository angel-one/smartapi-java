package com.angelbroking.smartapi.algos.strategies;

import com.angelbroking.smartapi.algos.TradingAction;
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
import java.util.HashMap;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Rsipricedeviation implements Strategy {
    private static final Logger log = LoggerFactory.getLogger(Rsipricedeviation.class);

    public String getName() {
        return "RSI Price Deviation Strategy";
    }

    @Override
    public List<TradingAction> generateSignals(List<Candle> historicalData, JSONObject params) {
        List<TradingAction> signals = new ArrayList<>();
        if (historicalData == null || historicalData.isEmpty()) {
            log.warn("[{}] Historical data is empty or null. Cannot generate RSI signals.", getName());
            return signals;
        }

        String interval = params.optString("interval", "UNKNOWN");
        log.info("[{}] Backtesting with interval: {}", getName(), interval);

        if (!historicalData.isEmpty()) {
            SimpleDateFormat sdfLog = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String fromDateStr = sdfLog.format(historicalData.get(0).getTimestamp());
            String toDateStr = sdfLog.format(historicalData.get(historicalData.size() - 1).getTimestamp());
            log.info("[{}] Processing data from: {} to: {}", getName(), fromDateStr, toDateStr);
        }
        
        // Parameters needed for TradingAction
        String symbolToken = params.optString("symbolToken", "UNKNOWN_TOKEN");
        String tradingSymbol = params.optString("tradingSymbol", "UNKNOWN_SYMBOL");
        String exchange = params.optString("exchange", "NSE"); // Default or get from params
        String productType = params.optString("productType", "INTRADAY"); // Default or get from params

        // Existing strategy parameters
        int rsiPeriod = params.optInt("rsiPeriod", 14);
        double oversoldThreshold = params.optDouble("oversoldThreshold", 25.0);
        double overboughtThreshold = params.optDouble("overboughtThreshold", 75.0);
        double rsiConfirmationBuffer = params.optDouble("rsiConfirmationBuffer", 2.0);
        
        // Price stability parameters for divergence detection
        double priceStabilityThresholdPercent = params.optDouble("priceStabilityThresholdPercent", 0.15);
        double minRsiPositiveDifference = params.optDouble("minRsiPositiveDifference", 2.0); // Ensure this is used
        
        // Control parameters
        int minCandlesBetweenTrades = params.optInt("minCandlesBetweenTrades", 10);
        double minProfitTarget = params.optDouble("minProfitTarget", 0.2);
        double stopLossPercent = params.optDouble("stopLossPercent", 0.4); // Ensure this is used
        int quantity = params.optInt("quantity", 1);
        
        // Market timing parameters
        boolean avoidMarketOpenClose = params.optBoolean("avoidMarketOpenClose", true);
        int marketOpenAvoidMinutes = params.optInt("marketOpenAvoidMinutes", 15);
        int marketCloseAvoidMinutes = params.optInt("marketCloseAvoidMinutes", 15);

        log.info("[{}] Parameters - RSI Period: {}, Price Stability: {}%, Min RSI Diff: {}, Gap: {}, Avoid Open/Close: {}",
                getName(), rsiPeriod, priceStabilityThresholdPercent, minRsiPositiveDifference, minCandlesBetweenTrades, avoidMarketOpenClose);

        double[] closePrices = historicalData.stream().mapToDouble(Candle::getClose).toArray();

        if (closePrices.length < rsiPeriod + 20) {
            log.warn("[{}] Not enough data for RSI calculation. Data points: {}, Need at least: {}",
                    getName(), closePrices.length, rsiPeriod + 20);
            return signals;
        }

        // Calculate RSI using TA-Lib
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
        
        int rsiSignalStabilizationCount = params.optInt("rsiSignalStabilizationCount", 15);

        if (length.value < rsiSignalStabilizationCount + 10) {
            log.warn("[{}] Not enough RSI values after stabilization. Available: {}, Need: {}",
                    getName(), length.value, rsiSignalStabilizationCount + 10);
            return signals;
        }

        // Calculate volume-based filter if available
        double[] volumes = historicalData.stream().mapToDouble(candle -> 
            candle.getVolume() > 0 ? candle.getVolume() : 1.0).toArray();
        double avgVolume = calculateAverageVolume(volumes, Math.min(20, volumes.length));

        // Main signal generation loop
        for (int i = rsiSignalStabilizationCount + 5; i < length.value - 5; i++) {
            int candleIndex = begin.value + i;
            if (candleIndex >= historicalData.size() - 5 || candleIndex - 5 < 0) continue;

            double currentRsi = rsiValues[i];
            double previousRsi = rsiValues[i - 1];
            double prevPrevRsi = rsiValues[i - 2];
            
            Candle currentCandle = historicalData.get(candleIndex);
            Candle previousCandle = historicalData.get(candleIndex - 1);
            
            double currentPrice = currentCandle.getClose();
            double previousPrice = previousCandle.getClose();

            // Market timing filter
            if (avoidMarketOpenClose && isNearMarketOpenOrClose(currentCandle, marketOpenAvoidMinutes, marketCloseAvoidMinutes)) {
                continue;
            }

            // Volume filter
            double currentVolume = currentCandle.getVolume() > 0 ? 
                currentCandle.getVolume() : 1.0;
            if (currentVolume < avgVolume * 0.5) {
                continue;
            }

            // Anti-overtrading check
            boolean canTrade = (candleIndex - lastTradeIndex) >= minCandlesBetweenTrades;

            // RSI stability check
            boolean rsiIsStable = isRsiStable(rsiValues, i, 3, 2.0);

            // Price stability calculations
            double priceDifferencePercent = Math.abs(currentPrice - previousPrice) / previousPrice * 100.0;
            boolean pricesAreSimilar = priceDifferencePercent <= priceStabilityThresholdPercent;
            
            // Enhanced price stability check
            boolean enhancedPriceStability = false;
            if (candleIndex >= 3) {
                double price3PeriodsAgo = historicalData.get(candleIndex - 3).getClose();
                double maxPriceInRange = Math.max(Math.max(currentPrice, previousPrice), price3PeriodsAgo);
                double minPriceInRange = Math.min(Math.min(currentPrice, previousPrice), price3PeriodsAgo);
                double priceRangePercent = (maxPriceInRange - minPriceInRange) / minPriceInRange * 100.0;
                enhancedPriceStability = priceRangePercent <= priceStabilityThresholdPercent * 2;
            }
            
            // RSI movement calculations
            double rsiDifference = currentRsi - previousRsi;
            boolean rsiHasPositiveDifference = rsiDifference >= minRsiPositiveDifference;
            
            // RSI trend confirmation
            boolean rsiTrendUp = isRsiTrendUp(rsiValues, i, 3);
            boolean rsiTrendDown = isRsiTrendDown(rsiValues, i, 3);

            // BUY SIGNAL CONDITIONS
            
            // 1. Strong Oversold Recovery
            boolean strongOversoldBuy = !inPosition && canTrade && rsiIsStable &&
                    previousRsi <= (oversoldThreshold - 2) &&
                    currentRsi >= (oversoldThreshold + rsiConfirmationBuffer) &&
                    rsiTrendUp;

            // 2. Price Stability + RSI Divergence (Main Strategy)
            boolean priceStableRsiDivergenceBuy = !inPosition && canTrade && rsiIsStable &&
                    enhancedPriceStability &&
                    rsiHasPositiveDifference &&
                    currentRsi > 35 && currentRsi < 65 &&
                    rsiTrendUp;

            // 3. Volume-confirmed momentum buy
            boolean volumeConfirmedBuy = !inPosition && canTrade &&
                    currentRsi > 40 && currentRsi < 60 &&
                    rsiTrendUp &&
                    currentVolume > avgVolume * 1.2 &&
                    priceDifferencePercent <= 0.2;

            // SELL SIGNAL CONDITIONS
            
            // 1. Strong Overbought Sell
            boolean strongOverboughtSell = inPosition &&
                    previousRsi >= (overboughtThreshold + 2) &&
                    currentRsi <= (overboughtThreshold - rsiConfirmationBuffer) &&
                    rsiTrendDown;

            // 2. Price Stability + RSI Negative Divergence
            boolean priceStableRsiNegativeDivergence = inPosition &&
                    enhancedPriceStability &&
                    rsiDifference <= -minRsiPositiveDifference &&
                    currentRsi > 45;

            // 3. Risk management exits
            boolean profitTargetHit = inPosition && entryPrice > 0 &&
                    ((currentPrice - entryPrice) / entryPrice * 100.0) >= minProfitTarget;
                    
            boolean stopLossHit = inPosition && entryPrice > 0 &&
                    ((entryPrice - currentPrice) / entryPrice * 100.0) >= stopLossPercent;

            boolean timeBasedExit = inPosition && (candleIndex - lastTradeIndex) > 50;

            // EXECUTE TRADES
            
            if (strongOversoldBuy) {
                signals.add(new TradingAction(currentCandle.getTimestamp(), ActionType.BUY, symbolToken, tradingSymbol,
                                              exchange, quantity, currentPrice, 0,
                                              "MARKET", productType, "NORMAL", "DAY"));
                inPosition = true;
                entryPrice = currentPrice;
                lastTradeIndex = candleIndex;
                log.info("[{}] OVERSOLD BUY at {}: RSI({}->{}) Price: {}",
                        getName(), currentCandle.getTimestamp(), 
                        String.format("%.1f", previousRsi), String.format("%.1f", currentRsi), 
                        String.format("%.2f", currentPrice));
                        
            } else if (priceStableRsiDivergenceBuy) {
                signals.add(new TradingAction(currentCandle.getTimestamp(), ActionType.BUY, symbolToken, tradingSymbol,
                                              exchange, quantity, currentPrice, 0,
                                              "MARKET", productType, "NORMAL", "DAY"));
                inPosition = true;
                entryPrice = currentPrice;
                lastTradeIndex = candleIndex;
                log.info("[{}] PRICE STABLE + RSI DIVERGENCE BUY at {}: Price Diff: {}%, RSI Diff: +{} ({}->{}) Price: {}",
                        getName(), currentCandle.getTimestamp(), 
                        String.format("%.3f", priceDifferencePercent), 
                        String.format("%.1f", rsiDifference), 
                        String.format("%.1f", previousRsi), 
                        String.format("%.1f", currentRsi), 
                        String.format("%.2f", currentPrice));
                        
            } else if (volumeConfirmedBuy) {
                signals.add(new TradingAction(currentCandle.getTimestamp(), ActionType.BUY, symbolToken, tradingSymbol,
                                              exchange, quantity, currentPrice, 0,
                                              "MARKET", productType, "NORMAL", "DAY"));
                inPosition = true;
                entryPrice = currentPrice;
                lastTradeIndex = candleIndex;
                log.info("[{}] VOLUME MOMENTUM BUY at {}: RSI({}->{}) Price: {} Vol: {}",
                        getName(), currentCandle.getTimestamp(), 
                        String.format("%.1f", previousRsi), String.format("%.1f", currentRsi), 
                        String.format("%.2f", currentPrice), 
                        String.format("%.0f", currentVolume));
                        
            } else if (strongOverboughtSell || priceStableRsiNegativeDivergence || profitTargetHit || stopLossHit || timeBasedExit) {
                String sellReason = strongOverboughtSell ? "OVERBOUGHT" : 
                                 priceStableRsiNegativeDivergence ? "DIVERGENCE_NEGATIVE" :
                                 profitTargetHit ? "PROFIT_TARGET" : 
                                 timeBasedExit ? "TIME_EXIT" : "STOP_LOSS";
                                 
                signals.add(new TradingAction(currentCandle.getTimestamp(), ActionType.SELL, symbolToken, tradingSymbol,
                                              exchange, quantity, currentPrice, 0,
                                              "MARKET", productType, "NORMAL", "DAY"));
                
                double profitPercent = entryPrice > 0 ? ((currentPrice - entryPrice) / entryPrice * 100.0) : 0;
                log.info("[{}] {} SELL at {}: RSI({}->{}) Price: {} P&L: {}%",
                        getName(), sellReason, currentCandle.getTimestamp(), 
                        String.format("%.1f", previousRsi), 
                        String.format("%.1f", currentRsi), 
                        String.format("%.2f", currentPrice), 
                        String.format("%.2f", profitPercent));
                        
                inPosition = false;
                entryPrice = 0.0;
                lastTradeIndex = candleIndex;
            }
        }

        log.info("[{}] Generated {} signals.", getName(), signals.size());
        return signals;
    }

    // Helper methods
    private boolean isNearMarketOpenOrClose(Candle candle, int openAvoidMinutes, int closeAvoidMinutes) {
        try {
            String timeStr = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(candle.getTimestamp());
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
            return new HashMap<>();
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
        return new HashMap<>();
    }
}

// Removed the Strategy interface as it is now in its own file
