package com.angelbroking.smartapi.models;

import java.util.Date;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
