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
