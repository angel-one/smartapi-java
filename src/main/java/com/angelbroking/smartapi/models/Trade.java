package com.angelbroking.smartapi.models;

import java.util.Date;

import com.google.gson.annotations.SerializedName;

public class Trade {

	@SerializedName("order_id")
	public String orderId;
	@SerializedName("tradingsymbol")
	public String tradingSymbol;
	@SerializedName("exchange")
	public String exchange;
	@SerializedName("product")
	public String product;
	@SerializedName("quantity")
	public String quantity;
	@SerializedName("transaction_type")
	public String transactionType;

}
