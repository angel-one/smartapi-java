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
	private String orderId;

	/**
	 * Exchange in which instrument is listed (NSE, BSE, NFO, BFO, CDS, MCX).
	 */
	@SerializedName("exchange")
	private String exchange;

	/**
	 * Symbol Token is unique identifier.
	 */
	@SerializedName("symbolToken")
	private String symbolToken;

	/**
	 * Transaction type (BUY or SELL).
	 */
	@SerializedName("transactiontype")
	private String transactionType;

	/**
	 * Quantity to transact
	 */
	@SerializedName("quantity")
	private Integer quantity;

	/**
	 * The min or max price to execute the order at (for LIMIT orders)
	 */
	@SerializedName("price")
	private Double price;

	/**
	 * producttype code (NRML, MIS, CNC).
	 */
	@SerializedName("producttype")
	private String productType;

	/**
	 * Order type (LIMIT, SL, SL-M, MARKET).
	 */
	@SerializedName("ordertype")
	private String orderType;

	/**
	 * Order duration (DAY, IOC).
	 */
	@SerializedName("duration")
	private String duration;

	/**
	 * variety
	 */
	@SerializedName("variety")
	private String variety;

	/**
	 * Trading Symbol of the instrument
	 */
	@SerializedName("tradingsymbol")
	private String tradingSymbol;

	/**
	 * The price at which an order should be triggered (SL, SL-M)
	 */
	@SerializedName("triggerprice")
	private String triggerPrice;

	/**
	 * Only For ROBO (Bracket Order)
	 */
	@SerializedName("squareoff")
	private String squareOff;

	/**
	 * Only For ROBO (Bracket Order)
	 */
	@SerializedName("stoploss")
	private String stopLoss;


}