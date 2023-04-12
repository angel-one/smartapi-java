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
	public String disclosedQuantity;

	public String duration;

	@SerializedName("tradingsymbol")
	public String tradingSymbol;

	public String variety;

	@SerializedName("ordertype")
	public String orderType;

	@SerializedName("triggerprice")
	public String triggerPrice;

	public String text;

	public String price;

	public String status;

	@SerializedName("producttype")
	public String productType;

	public String exchange;

	@SerializedName("orderid")
	public String orderId;

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
