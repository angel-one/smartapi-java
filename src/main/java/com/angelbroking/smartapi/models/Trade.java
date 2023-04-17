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
	private String orderId;

	@SerializedName("tradingsymbol")
	private String tradingSymbol;

	private String exchange;

	private String product;

	private String quantity;

	@SerializedName("transaction_type")
	private String transactionType;

}
