package com.angelbroking.smartapi.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A wrapper for order.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

	@SerializedName("disclosedquantity")
	private String disclosedQuantity;

	private String duration;

	@JsonProperty("tradingsymbol")
	@SerializedName("tradingsymbol")
	private String tradingSymbol;

	private String variety;

	@SerializedName("ordertype")
	private String orderType;

	@SerializedName("triggerprice")
	private String triggerPrice;

	private String text;

	private String price;

	private String status;

	@SerializedName("producttype")
	private String productType;

	private String exchange;

	@JsonProperty("orderid")
	@SerializedName("orderid")
	private String orderId;

	private String symbol;

	@SerializedName("updatetime")
	private String updateTime;

	@SerializedName("exchtime")
	private String exchangeTimestamp;

	@SerializedName("exchorderupdatetime")
	private String exchangeUpdateTimestamp;

	@SerializedName("averageprice")
	private String averagePrice;

	@SerializedName("transactiontype")
	private String transactionType;

	private String quantity;

	@SerializedName("squareoff")
	private String squareOff;

	@SerializedName("stoploss")
	private String stopLoss;

	@SerializedName("trailingstoploss")
	private String trailingStopLoss;

	@SerializedName("symboltoken")
	private String symbolToken;

	@SerializedName("instrumenttype")
	private String instrumentType;

	@SerializedName("strikeprice")
	private String strikePrice;

	@SerializedName("optiontype")
	private String optionType;

	@SerializedName("expirydate")
	private String expiryDate;

	@SerializedName("lotsize")
	private String lotSize;

	@SerializedName("cancelsize")
	private String cancelSize;

	@SerializedName("filledshares")
	private String filledShares;

	@SerializedName("orderstatus")
	private String orderStatus;

	@SerializedName("unfilledshares")
	private String unfilledShares;

	@SerializedName("fillid")
	private String fillId;

	@SerializedName("filltime")
	private String fillTime;

}
