package com.angelbroking.smartapi.models;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

public class BacktestReport {
    private double initialCapital;
    private double finalCapital;
    private double netProfit;
    private double profitPercentage;
    private int totalTrades;
    private List<TradeLog> trades;
    private List<Date> equityCurveTimestamps;
    private List<Double> equityCurveValues;
    private List<Candle> historicalCandles;
    // Add more metrics later: winRate, maxDrawdown, sharpeRatio, etc.

    public BacktestReport() {
        this.trades = new ArrayList<>();
        this.equityCurveTimestamps = new ArrayList<>();
        this.equityCurveValues = new ArrayList<>();
    }

    public double getInitialCapital() {
        return initialCapital;
    }

    public void setInitialCapital(double initialCapital) {
        this.initialCapital = initialCapital;
    }

    public double getFinalCapital() {
        return finalCapital;
    }

    public void setFinalCapital(double finalCapital) {
        this.finalCapital = finalCapital;
    }

    public double getNetProfit() {
        return netProfit;
    }

    public void setNetProfit(double netProfit) {
        this.netProfit = netProfit;
    }

    public double getProfitPercentage() {
        return profitPercentage;
    }

    public void setProfitPercentage(double profitPercentage) {
        this.profitPercentage = profitPercentage;
    }

    public int getTotalTrades() {
        return totalTrades;
    }

    public void setTotalTrades(int totalTrades) {
        this.totalTrades = totalTrades;
    }

    public List<TradeLog> getTrades() {
        return trades;
    }

    public void setTrades(List<TradeLog> trades) {
        this.trades = trades;
        this.totalTrades = trades != null ? trades.size() : 0;
    }

    public List<Date> getEquityCurveTimestamps() {
        return equityCurveTimestamps;
    }

    public void setEquityCurveTimestamps(List<Date> equityCurveTimestamps) {
        this.equityCurveTimestamps = equityCurveTimestamps;
    }

    public List<Double> getEquityCurveValues() {
        return equityCurveValues;
    }

    public void setEquityCurveValues(List<Double> equityCurveValues) {
        this.equityCurveValues = equityCurveValues;
    }

    public List<Candle> getHistoricalCandles() {
        return historicalCandles;
    }

    public void setHistoricalCandles(List<Candle> historicalCandles) {
        this.historicalCandles = historicalCandles;
    }
}