package com.angelbroking.smartapi.models;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GttParams {

    @SerializedName("id")
    public Integer id;

    @SerializedName("tradingsymbol")
    public String tradingSymbol;

    @SerializedName("exchange")
    public String exchange;

    @SerializedName("transactiontype")
    public String transactionType;

    @SerializedName("producttype")
    public String productType;

    @SerializedName("price")
    public Double price;

    @SerializedName("qty")
    public Integer qty;

    @SerializedName("triggerprice")
    public Double triggerPrice;

    @SerializedName("disclosedqty")
    public Integer disclosedQty;

    @SerializedName("timeperiod")
    public Integer timePeriod;

    @SerializedName("symboltoken")
    public String symbolToken;

}
