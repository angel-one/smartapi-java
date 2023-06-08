package com.angelbroking.smartapi.smartstream.models;



import lombok.extern.slf4j.Slf4j;

import java.util.NoSuchElementException;

@Slf4j
public enum SmartStreamAction {

	SUBSCRIBE(1), UNSUBSCRIBE(0);
	
	private int val;


	SmartStreamAction(int val) {
		this.val = val;
	}
	
	public static SmartStreamAction findByVal(int val) {
		for(SmartStreamAction entry : SmartStreamAction.values()) {
			if(entry.getVal() == val) {
				return entry;
			}
		}
		String errorMessage = "No SmartStreamAction found with value: ${val}";
		log.error(errorMessage);
		throw new NoSuchElementException(errorMessage);
	}
	
	public int getVal() {
		return this.val;
	}
}