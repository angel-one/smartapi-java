package com.angelbroking.smartapi.models;

import com.google.gson.annotations.SerializedName;

public class Gtt {
	@SerializedName("id")
	public Integer id;

	@SerializedName("tradingsymbol")
	public String tradingSymbol;

	@SerializedName("symboltoken")
	public String symbolToken;

	@SerializedName("exchange")
	public String exchange;

	@SerializedName("transactiontype")
	public String transactionType;

	@SerializedName("producttype")
	public String productType;

	@SerializedName("price")
	public Integer price;

	@SerializedName("quantity")
	public Integer quantity;

	@SerializedName("triggerprice")
	public Integer triggerPrice;

	@SerializedName("disclosedqty")
	public Integer disclosedQty;

	@SerializedName("timeperiod")
	public Integer timePeriod;

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Gtt [id=").append(id)
				.append(", tradingSymbol=").append(tradingSymbol)
				.append(", symbolToken=").append(symbolToken)
				.append(", exchange=").append(exchange)
				.append(", transactionType=").append(transactionType)
				.append(", productType=").append(productType)
				.append(", price=").append(price)
				.append(", quantity=").append(quantity)
				.append(", triggerPrice=").append(triggerPrice)
				.append(", disclosedQty=").append(disclosedQty)
				.append(", timePeriod=").append(timePeriod)
				.append("]");
		return sb.toString();
	}

}
