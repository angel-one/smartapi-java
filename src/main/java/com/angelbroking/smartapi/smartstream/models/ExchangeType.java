package com.angelbroking.smartapi.smartstream.models;

public enum ExchangeType {
	NSE_CM(1), NSE_FO(2), BSE_CM(3), BSE_FO(4), MCX_FO(5), NCX_FO(7), CDE_FO(13);

	private int val;

	private ExchangeType(int val) {
		this.val = val;
	}

	public int getVal() {
		return this.val;
	}
	
	public static ExchangeType findByValue(int val) {
		for(ExchangeType entry : ExchangeType.values()) {
			if(entry.getVal() == val) {
				return entry;
			}
		}
		return null;
	}
}
