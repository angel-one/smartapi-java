package com.angelbroking.smartapi.models;

import com.fasterxml.jackson.annotation.JsonProperty;

// If you were using Lombok, you might have:
// import lombok.Data;
// @Data
public class SearchScripResponseDTO {

    @JsonProperty("exchange")
    private String exchange;

    @JsonProperty("tradingsymbol")
    private String tradingSymbol;

    @JsonProperty("symboltoken")
    private String symbolToken;

    // Default constructor (needed for Jackson deserialization)
    public SearchScripResponseDTO() {
    }

    // Getters
    public String getExchange() {
        return exchange;
    }

    public String getTradingSymbol() {
        return tradingSymbol;
    }

    public String getSymbolToken() {
        return symbolToken;
    }

    // Setters (optional, but good practice if you need to create these objects manually)
    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public void setTradingSymbol(String tradingSymbol) {
        this.tradingSymbol = tradingSymbol;
    }

    public void setSymbolToken(String symbolToken) {
        this.symbolToken = symbolToken;
    }
}