package com.angelbroking.smartapi.smartstream.models;

import lombok.Data;

@Data
public class SmartApiBBSInfo {
	public static final int BYTES = (2 * Short.BYTES) + (2 * Long.BYTES);

	// siBbBuySellFlag = 1 buy
	// siBbBuySellFlag = 0 sell
	private short siBbBuySellFlag = -1;
	private long lQuantity = -1;
	private long lPrice = -1;
	private short siNumberOfOrders = -1;


}