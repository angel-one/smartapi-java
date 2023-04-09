package com.angelbroking.smartapi.models;

import java.util.Date;

import com.google.gson.annotations.SerializedName;

public class Trade {

	@SerializedName("order_id")
	public String orderId;

	@SerializedName("tradingsymbol")
	public String tradingSymbol;

	public String exchange;

	public String product;

	public String quantity;

	@SerializedName("transaction_type")
	public String transactionType;

}
