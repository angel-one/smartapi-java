package com.angelbroking.smartapi.smartticker.ticker;

import org.json.JSONArray;

public interface OnTicks {
	void onTicks(JSONArray ticks);
}
