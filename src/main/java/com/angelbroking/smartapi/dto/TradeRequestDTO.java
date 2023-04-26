package com.angelbroking.smartapi.dto;

import lombok.Data;

@Data
public class TradeRequestDTO {
    private String exchange;
    private String oldProductType;
    private String newProductType;
    private String tradingSymbol;
    private String transactionType;
    private int quantity;
    private String type;
}
