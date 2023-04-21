package com.angelbroking.smartapi.smartstream.models;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class LTPParams {

    @SerializedName("exchange")
    private String exchange;

    @SerializedName("symboltoken")
    private String symbolToken;

    @SerializedName("tradingsymbol")
    private String tradingSymbol;
}
