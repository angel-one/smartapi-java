package com.angelbroking.smartapi.dto;

import lombok.Data;

@Data
public class StockHistoryRequestDTO {
    private String toDate;
    private String exchange;
    private String interval;
    private String symbolToken;
    private String fromDate;
}
