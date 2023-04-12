package com.angelbroking.smartapi.smartstream.models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.NoSuchElementException;


public enum SmartStreamAction {

	SUBS(1), UNSUBS(0);
	
	private int val;

	private static final Logger logger = LoggerFactory.getLogger(SmartStreamAction.class);

	private SmartStreamAction(int val) {
		this.val = val;
	}
	
	public static SmartStreamAction findByVal(int val) {
		for(SmartStreamAction entry : SmartStreamAction.values()) {
			if(entry.getVal() == val) {
				return entry;
			}
		}
		String errorMessage = "No SmartStreamAction found with value: " + val;
		logger.error(errorMessage);
		throw new NoSuchElementException(errorMessage);
	}
	
	public int getVal() {
		return this.val;
	}
}