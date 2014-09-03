package com.jimtough.griswold.beans;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;

public class AppAlphaStatus extends GenericAppStatus {

	private ReadOnlyStringWrapper statusString;

	/**
	 * Constructor
	 * @param hostname Non-null, non-empty string
	 */
	public AppAlphaStatus(String hostname) {
		super(hostname);
	}

	public final void setStatusString(String value) {
		statusStringProperty();		// ensure property is initialized
		statusString.set(value);
	}
	
	@Override
	public ReadOnlyStringProperty statusStringProperty() {
		if (statusString == null) {
			statusString = new ReadOnlyStringWrapper();
		}
		return statusString.getReadOnlyProperty();
	}

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
