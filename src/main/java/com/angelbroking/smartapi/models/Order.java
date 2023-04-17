package com.angelbroking.smartapi.models;

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

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Order [disclosedQuantity=").append(disclosedQuantity)
				.append(", duration=").append(duration)
				.append(", tradingSymbol=").append(tradingSymbol)
				.append(", variety=").append(variety)
				.append(", orderType=").append(orderType)
				.append(", triggerPrice=").append(triggerPrice)
				.append(", text=").append(text)
				.append(", price=").append(price)
				.append(", status=").append(status)
				.append(", productType=").append(productType)
				.append(", exchange=").append(exchange)
				.append(", orderId=").append(orderId)
				.append(", symbol=").append(symbol)
				.append(", updateTime=").append(updateTime)
				.append(", exchangeTimestamp=").append(exchangeTimestamp)
				.append(", exchangeUpdateTimestamp=").append(exchangeUpdateTimestamp)
				.append(", averagePrice=").append(averagePrice)
				.append(", transactionType=").append(transactionType)
				.append(", quantity=").append(quantity)
				.append(", squareOff=").append(squareOff)
				.append(", stopLoss=").append(stopLoss)
				.append(", trailingStopLoss=").append(trailingStopLoss)
				.append(", symbolToken=").append(symbolToken)
				.append(", instrumentType=").append(instrumentType)
				.append(", strikePrice=").append(strikePrice)
				.append(", optionType=").append(optionType)
				.append(", expiryDate=").append(expiryDate)
				.append(", lotSize=").append(lotSize)
				.append(", cancelSize=").append(cancelSize)
				.append(", filledShares=").append(filledShares)
				.append(", orderStatus=").append(orderStatus)
				.append(", unfilledShares=").append(unfilledShares)
				.append(", fillId=").append(fillId)
				.append(", fillTime=").append(fillTime)
				.append("]");
		return sb.toString();
	}


}
