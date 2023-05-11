package com.angelbroking.smartapi.dto;

import lombok.Data;

import com.google.gson.annotations.SerializedName;

@Data
public class TradeRequestDTO {
    private String exchange;

    @SerializedName("symboltoken")
    private String symbolToken;

    @SerializedName("producttype")
    private String oldProductType;

    @SerializedName("newproducttype")
    private String newProductType;

    @SerializedName("tradingsymbol")
    private String tradingSymbol;

    @SerializedName("symbolname")
    private String symbolName;

    @SerializedName("instrumenttype")
    private String instrumentType;

    @SerializedName("priceden")
    private String priceDen;

    @SerializedName("pricenum")
    private String priceNum;

    @SerializedName("genden")
    private String genDen;

    @SerializedName("gennum")
    private String genNum;

    private String precision;

    private String multiplier;

    @SerializedName("boardlotsize")
    private String boardLotSize;

    @SerializedName("buyqty")
    private String buyQty;

    @SerializedName("sellqty")
    private String sellQty;

    @SerializedName("buyamount")
    private String buyAmount;

    @SerializedName("sellamount")
    private String sellAmount;

    @SerializedName("transactiontype")
    private String transactionType;

    private int quantity;

    private String type;
}

