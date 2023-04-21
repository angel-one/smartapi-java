package com.angelbroking.smartapi.models;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Gtt {

	@SerializedName("id")
	private Integer id;

	@SerializedName("tradingsymbol")
	private String tradingSymbol;

	@SerializedName("symboltoken")
	private String symbolToken;

	@SerializedName("exchange")
	private String exchange;

	@SerializedName("transactiontype")
	private String transactionType;

	@SerializedName("producttype")
	private String productType;

	@SerializedName("price")
	private Integer price;

	@SerializedName("quantity")
	private Integer quantity;

	@SerializedName("triggerprice")
	private Integer triggerPrice;

	@SerializedName("disclosedqty")
	private Integer disclosedQty;

	@SerializedName("timeperiod")
	private Integer timePeriod;

}
