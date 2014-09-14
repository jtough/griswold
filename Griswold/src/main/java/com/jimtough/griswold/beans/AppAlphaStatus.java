package com.jimtough.griswold.beans;

public class AppAlphaStatus extends GenericAppStatus {

	/**
	 * Constructor
	 * @param hostname Non-null, non-empty string
	 */
	public AppAlphaStatus(String hostname) {
		super(hostname);
	}

	//--------------------------------------

	public int hashCode() {
		return this.getHostname().hashCode();
	}

	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if (other == null) {
			return false;
		}
		if (this.getClass() != other.getClass()) {
			return false;
		}
		// ASSUMPTION: hostname is unique among all instances of this class
		AppAlphaStatus that = (AppAlphaStatus) other;
		return this.getHostname().equals(that.getHostname());
	}
	
}
