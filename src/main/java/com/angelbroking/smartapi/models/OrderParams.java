package com.angelbroking.smartapi.models;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** A wrapper for order params to be sent while placing an order. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderParams {

	@SerializedName("orderid")
	public String orderId;

	/**
	 * Exchange in which instrument is listed (NSE, BSE, NFO, BFO, CDS, MCX).
	 */
	@SerializedName("exchange")
	public String exchange;

	/**
	 * Symbol Token is unique identifier.
	 */
	@SerializedName("symbolToken")
	public String symbolToken;

	/**
	 * Transaction type (BUY or SELL).
	 */
	@SerializedName("transactiontype")
	public String transactionType;

	/**
	 * Quantity to transact
	 */
	@SerializedName("quantity")
	public Integer quantity;

	/**
	 * The min or max price to execute the order at (for LIMIT orders)
	 */
	@SerializedName("price")
	public Double price;

	/**
	 * producttype code (NRML, MIS, CNC).
	 */
	@SerializedName("producttype")
	public String productType;

	/**
	 * Order type (LIMIT, SL, SL-M, MARKET).
	 */
	@SerializedName("ordertype")
	public String orderType;

	/**
	 * Order duration (DAY, IOC).
	 */
	@SerializedName("duration")
	public String duration;

	/**
	 * variety
	 */
	@SerializedName("variety")
	public String variety;

	/**
	 * Trading Symbol of the instrument
	 */
	@SerializedName("tradingsymbol")
	public String tradingSymbol;

	/**
	 * The price at which an order should be triggered (SL, SL-M)
	 */
	@SerializedName("triggerprice")
	public String triggerPrice;

	/**
	 * Only For ROBO (Bracket Order)
	 */
	@SerializedName("squareoff")
	public String squareOff;

	/**
	 * Only For ROBO (Bracket Order)
	 */
	@SerializedName("stoploss")
	public String stopLoss;


}