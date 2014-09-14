package com.jimtough.griswold.beans;

/**
 * Enumeration of status codes; mak
 * 
 * @author JTOUGH
 */
public enum GenericStatusCode {

	UNKNOWN("Unknown"),
	NORMAL("Normal"),
	WARNING("Warning"),
	ERROR("Error"),
	OFFLINE("Offline");
	
	public final String displayString;
	
	private GenericStatusCode(final String displayString) {
		this.displayString = displayString;
	}
	
}
