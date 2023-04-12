package com.angelbroking.smartapi.sample.ticker;

import org.json.JSONArray;

public interface OnTicks {
	void onTicks(JSONArray ticks);
}
