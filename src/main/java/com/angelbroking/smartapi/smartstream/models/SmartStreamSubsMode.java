package com.angelbroking.smartapi.smartstream.models;

public enum SmartStreamSubsMode {
	LTP(1), QUOTE(2), SNAP_QUOTE(3), DEPTH_20(4);
	
	private static final int SIZE = SmartStreamSubsMode.values().length;
	
	private int val;
	
	private SmartStreamSubsMode(int val) {
		this.val = val;
	}
	
	public static SmartStreamSubsMode findByVal(int val) {
		for(SmartStreamSubsMode entry : SmartStreamSubsMode.values()) {
			if(entry.getVal() == val) {
				return entry;
			}
		}
		return null;
	}
	
	public static int size() {
		return SIZE;
	}
	
	public int getVal() {
		return this.val;
	}
	
}