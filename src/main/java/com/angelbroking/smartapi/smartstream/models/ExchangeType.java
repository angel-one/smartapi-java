package com.angelbroking.smartapi.smartstream.models;

import java.util.Arrays;
import java.util.NoSuchElementException;

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
		return Arrays.stream(ExchangeType.values())
				.filter(entry -> entry.getVal() == val)
				.findFirst()
				.orElseThrow(() -> new NoSuchElementException(String.format("No ExchangeType found with value: %s", val)));
	}


}
