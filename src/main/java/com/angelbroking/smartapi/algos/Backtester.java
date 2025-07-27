package com.angelbroking.smartapi.algos;

import com.angelbroking.smartapi.models.BacktestReport;
import com.angelbroking.smartapi.models.TradeLog;
import com.angelbroking.smartapi.models.Candle;
import com.angelbroking.smartapi.algos.AngelOneChargeCalculator; // Added import
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Backtester {
    private Strategy strategy;
    private List<Candle> historicalData;
    private double initialCapital;
    private int sharesPerTrade;
    private AngelOneChargeCalculator chargeCalculator;

    public Backtester(Strategy strategy, List<Candle> historicalData, JSONObject params) {
        this.strategy = strategy;
        this.historicalData = historicalData;
        this.initialCapital = params.optDouble("initialCapital", 100000.0);
        this.sharesPerTrade = params.optInt("sharesPerTrade", 10);
        this.chargeCalculator = new AngelOneChargeCalculator(); // Initialize charge calculator
    }

    public BacktestReport run(JSONObject strategyParams) {
        BacktestReport report = new BacktestReport();
        report.setInitialCapital(initialCapital);

        List<TradeLog> trades = new ArrayList<>();
        double currentCapital = initialCapital;
        int sharesHeld = 0;
        List<Date> equityTimestamps = new ArrayList<>();
        List<Double> equityValues = new ArrayList<>();
        double entryPrice = 0.0;

        // Generate all signals from the strategy based on the full historical data
        // Expecting TradingAction objects from strategies now
        List<TradingAction> allGeneratedSignals = strategy.generateSignals(historicalData, strategyParams);
        int signalIndex = 0; // To iterate through the allGeneratedSignals list

        for (Candle candle : historicalData) { // Iterate through each candle in the historical data
            TradingAction actionForThisCandle = null;
            // Check if the next signal from the strategy matches the current candle's timestamp
            if (signalIndex < allGeneratedSignals.size()) {
                TradingAction nextPotentialSignal = allGeneratedSignals.get(signalIndex);
                // Ensure nextPotentialSignal and its timestamp are not null before comparing
                if (nextPotentialSignal != null && nextPotentialSignal.getTimestamp() != null &&
                    nextPotentialSignal.getTimestamp().equals(candle.getTimestamp())) {
                    actionForThisCandle = nextPotentialSignal; // Assign the found signal
                    signalIndex++; // Consume this signal
                }
            }

            // Process BUY signal
            if (actionForThisCandle != null && actionForThisCandle.getActionType() == ActionType.BUY && sharesHeld == 0) {
                double tradePrice = actionForThisCandle.getPrice(); // Use signal price, not necessarily candle close
                int tradeQuantity = actionForThisCandle.getQuantity(); // Use signal quantity
                String productType = actionForThisCandle.getProductType();
                String exchange = actionForThisCandle.getExchange();

                double estimatedCharges = chargeCalculator.calculateTotalCharges(tradePrice, tradeQuantity, "BUY", productType, exchange);

                if (currentCapital >= (tradeQuantity * tradePrice + estimatedCharges)) {
                    sharesHeld = sharesPerTrade;
                    entryPrice = tradePrice; // Use trade price for entry
                    currentCapital -= (sharesHeld * entryPrice);
                    currentCapital -= estimatedCharges;
                    trades.add(new TradeLog(actionForThisCandle.getActionType().toString(), candle.getTimestamp(), sharesHeld, entryPrice, estimatedCharges));
                }
            }
            // Process SELL signal
            else if (actionForThisCandle != null && actionForThisCandle.getActionType() == ActionType.SELL && sharesHeld > 0) {
                double tradePrice = actionForThisCandle.getPrice(); // Use signal price
                String productType = actionForThisCandle.getProductType();
                String exchange = actionForThisCandle.getExchange();
                double estimatedCharges = chargeCalculator.calculateTotalCharges(tradePrice, sharesHeld, "SELL", productType, exchange); // Use sharesHeld for sell quantity
                currentCapital += (sharesHeld * tradePrice); // Use trade price for exit
                currentCapital -= estimatedCharges;
                trades.add(new TradeLog(actionForThisCandle.getActionType().toString(), candle.getTimestamp(), sharesHeld, tradePrice, estimatedCharges));
                sharesHeld = 0;
                entryPrice = 0.0;
            }

            // Record equity at the end of each candle
            double currentPortfolioValue = currentCapital + (sharesHeld * candle.getClose());
            equityTimestamps.add(candle.getTimestamp());
            equityValues.add(currentPortfolioValue);
        }

        // If position is still open at the end, square it off
        if (sharesHeld > 0 && !historicalData.isEmpty()) {
            Candle lastCandle = historicalData.get(historicalData.size() - 1);
            currentCapital += (sharesHeld * lastCandle.getClose());
            // Calculate and subtract square-off charges
            double squareOffCharges = chargeCalculator.calculateTotalCharges(lastCandle.getClose(), sharesHeld, "SELL", "SQUAREOFF", "UNKNOWN"); // Use a placeholder product/exchange
            currentCapital -= squareOffCharges;
            trades.add(new TradeLog("SQUAREOFF_END", lastCandle.getTimestamp(), sharesHeld, lastCandle.getClose(), squareOffCharges));
            // Update the last equity point to reflect the square-off
            if (!equityValues.isEmpty()) {
                equityValues.set(equityValues.size() - 1, currentCapital);
            }
        }

        report.setTrades(trades);
        report.setFinalCapital(currentCapital);
        report.setNetProfit(currentCapital - initialCapital);
        if (initialCapital != 0) {
            report.setProfitPercentage(((currentCapital - initialCapital) / initialCapital) * 100);
        } else {
            report.setProfitPercentage(0);
        }
        report.setEquityCurveTimestamps(equityTimestamps);
        report.setEquityCurveValues(equityValues);
        report.setHistoricalCandles(new ArrayList<>(historicalData)); // Store a copy

        return report;
    }
}