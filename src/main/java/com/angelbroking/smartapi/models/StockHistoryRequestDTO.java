package com.angelbroking.smartapi.models;

import lombok.Data;

@Data
public class StockHistoryRequestDTO {
    private String toDate;
    private String exchange;
    private String interval;
    private String symbolToken;
    private String fromDate;
}
