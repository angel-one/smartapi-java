package com.angelbroking.smartapi.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class StockHistoryRequestDTO {
    @SerializedName("todate")

    private String toDate;
    private String exchange;
    private String interval;
    @SerializedName("symboltoken")
    private String symbolToken;
    @SerializedName("fromdate")
    private String fromDate;
}
