package com.angelbroking.smartapi.models;

import com.google.gson.annotations.SerializedName;

/**
 * A wrapper for order.
 */
public class Order {

	@SerializedName("disclosedquantity")
	public String disclosedQuantity;

	@SerializedName("duration")
	public String duration;

	@SerializedName("tradingsymbol")
	public String tradingSymbol;

	@SerializedName("variety")
	public String variety;

	@SerializedName("ordertype")
	public String orderType;

	@SerializedName("triggerprice")
	public String triggerPrice;

	@SerializedName("text")
	public String text;

	@SerializedName("price")
	public String price;

	@SerializedName("status")
	public String status;

	@SerializedName("producttype")
	public String productType;

	@SerializedName("exchange")
	public String exchange;

	@SerializedName("orderid")
	public String orderId;

	@SerializedName("symbol")
	public String symbol;

	@SerializedName("updatetime")
	public String updateTime;

	@SerializedName("exchtime")
	public String exchangeTimestamp;

	@SerializedName("exchorderupdatetime")
	public String exchangeUpdateTimestamp;

	@SerializedName("averageprice")
	public String averagePrice;

	@SerializedName("transactiontype")
	public String transactionType;

	@SerializedName("quantity")
	public String quantity;

	@SerializedName("squareoff")
	public String squareOff;

	@SerializedName("stoploss")
	public String stopLoss;

	@SerializedName("trailingstoploss")
	public String trailingStopLoss;

	@SerializedName("symboltoken")
	public String symbolToken;

	@SerializedName("instrumenttype")
	public String instrumentType;

	@SerializedName("strikeprice")
	public String strikePrice;

	@SerializedName("optiontype")
	public String optionType;

	@SerializedName("expirydate")
	public String expiryDate;

	@SerializedName("lotsize")
	public String lotSize;

	@SerializedName("cancelsize")
	public String cancelSize;

	@SerializedName("filledshares")
	public String filledShares;

	@SerializedName("orderstatus")
	public String orderStatus;

	@SerializedName("unfilledshares")
	public String unfilledShares;

	@SerializedName("fillid")
	public String fillId;

	@SerializedName("filltime")
	public String fillTime;

	@SerializedName("uniqueorderid")
	public String uniqueOrderId;

	@SerializedName("errorMessage")
	public String apiErrorMessage;

	@Override
	public String toString() {
		return "Order [disclosedQuantity=" + disclosedQuantity + ", duration=" + duration + ", tradingSymbol="
				+ tradingSymbol + ", variety=" + variety + ", orderType=" + orderType + ", triggerPrice=" + triggerPrice
				+ ", text=" + text + ", price=" + price + ", status=" + status + ", productType=" + productType
				+ ", exchange=" + exchange + ", orderId=" + orderId + ", symbol=" + symbol + ", updateTime="
				+ updateTime + ", exchangeTimestamp=" + exchangeTimestamp + ", exchangeUpdateTimestamp="
				+ exchangeUpdateTimestamp + ", averagePrice=" + averagePrice + ", transactionType=" + transactionType
				+ ", quantity=" + quantity + ", squareOff=" + squareOff + ", stopLoss=" + stopLoss
				+ ", trailingStopLoss=" + trailingStopLoss + ", symbolToken=" + symbolToken + ", instrumentType="
				+ instrumentType + ", strikePrice=" + strikePrice + ", optionType=" + optionType + ", expiryDate="
				+ expiryDate + ", lotSize=" + lotSize + ", cancelSize=" + cancelSize + ", filledShares=" + filledShares
				+ ", orderStatus=" + orderStatus + ", unfilledShares=" + unfilledShares + ", fillId=" + fillId
				+ ", fillTime=" + fillTime + ", uniqueorderid=" + uniqueOrderId + ", errorMessage=" + apiErrorMessage + "]";
	}

}
