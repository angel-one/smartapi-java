package com.angelbroking.smartapi.models;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GttParams {

    private Integer id;

    @SerializedName("tradingsymbol")
    private String tradingSymbol;

    private String exchange;

    @SerializedName("transactiontype")
    private String transactionType;

    @SerializedName("producttype")
    private String productType;

    private Double price;

    private Integer qty;

    @SerializedName("triggerprice")
    private Double triggerPrice;

    @SerializedName("disclosedqty")
    private Integer disclosedQty;

    @SerializedName("timeperiod")
    private Integer timePeriod;

    @SerializedName("symboltoken")
    private String symbolToken;

}
