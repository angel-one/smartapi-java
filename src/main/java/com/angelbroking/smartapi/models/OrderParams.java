package com.angelbroking.smartapi.models;

/** A wrapper for order params to be sent while placing an order. */
public class OrderParams {

	public String orderid;
	/**
	 * Exchange in which instrument is listed (NSE, BSE, NFO, BFO, CDS, MCX).
	 */

	public String exchange;

	/**
	 * Transaction type (BUY or SELL).
	 */

	public String transactiontype;

	/**
	 * Order quantity
	 */

	public Integer quantity;

	/**
	 * Order Price
	 */

	public Double price;

	/**
	 * producttype code (NRML, MIS, CNC).
	 */

	public String producttype;

	/**
	 * Order type (LIMIT, SL, SL-M, MARKET).
	 */

	public String ordertype;

	/**
	 * Order duration (DAY, IOC).
	 */

	public String duration;

	/**
	 * variety
	 */

	public String variety;

	/**
	 * Order duration (DAY, IOC).
	 */

	public String tradingsymbol;
	
	public String triggerprice;
	
	public String squareoff;
	
	public String stoploss;
	
	public String symboltoken;
	
	

}