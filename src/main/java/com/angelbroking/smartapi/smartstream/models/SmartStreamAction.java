package com.angelbroking.smartapi.smartstream.models;

public enum SmartStreamAction {
	SUBS(1), UNSUBS(0);
	
	private int val;
	
	private SmartStreamAction(int val) {
		this.val = val;
	}
	
	public static SmartStreamAction findByVal(int val) {
		for(SmartStreamAction entry : SmartStreamAction.values()) {
			if(entry.getVal() == val) {
				return entry;
			}
		}
		return null;
	}
	
	public int getVal() {
		return this.val;
	}
}