package com.angelbroking.smartapi.smartwebsocket;

import org.json.JSONArray;

public interface SmartWSOnTicks {
	void onTicks(JSONArray ticks);
}
