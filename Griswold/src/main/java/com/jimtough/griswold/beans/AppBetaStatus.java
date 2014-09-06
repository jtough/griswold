package com.jimtough.griswold.beans;

import org.joda.time.Duration;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;

public class AppBetaStatus extends GenericAppStatus {

	private final DurationProperty uptime;
	private final ReadOnlyStringWrapper statusString;

	/**
	 * Constructor
	 * @param hostname Non-null, non-empty string
	 */
	public AppBetaStatus(final String hostname) {
		super(hostname);
		this.uptime = new DurationProperty(this, null);
		this.statusString = new ReadOnlyStringWrapper("");
	}

	// uptime accessors
	
	public final void setUptime(Duration value) {
		uptimeProperty().set(value);
	}

	public final Duration getUptime() {
		return uptimeProperty().get();
	}

	public DurationProperty uptimeProperty() {
		return uptime;
	}

	// statusString accessors
	
	public final void setStatusString(String value) {
		statusStringProperty();		// ensure property is initialized
		statusString.set(value);
	}
	
	@Override
	public ReadOnlyStringProperty statusStringProperty() {
		return statusString.getReadOnlyProperty();
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
		AppBetaStatus that = (AppBetaStatus) other;
		return this.getHostname().equals(that.getHostname());
	}
	
}
