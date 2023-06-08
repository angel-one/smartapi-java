package com.angelbroking.smartapi.smartticker;

import org.json.JSONArray;

public interface SmartWSOnTicks {
	void onTicks(JSONArray ticks);
}
