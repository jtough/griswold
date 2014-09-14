package com.jimtough.griswold.beans;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Period;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;

public class AppBetaStatus extends GenericAppStatus {

	private final static String UPTIME_FORMAT_STRING =
			"%dy, %02dm, %dw, %02dd, %02d:%02d:%02d.%03d";
	
	private Duration uptime;
	private final ReadOnlyStringWrapper uptimeString;

	/**
	 * Constructor
	 * @param hostname Non-null, non-empty string
	 */
	public AppBetaStatus(final String hostname) {
		super(hostname);
		this.uptime = new Duration(0);
		this.uptimeString = new ReadOnlyStringWrapper(this.uptime.toString());
	}

	// uptime accessors
	
	String asString(Duration d) {
		Period p = new Period(DateTime.now().minus(d), d);
		return String.format(UPTIME_FORMAT_STRING,
				p.getYears(),
				p.getMonths(),
				p.getWeeks(),
				p.getDays(),
				p.getHours(),
				p.getMinutes(),
				p.getSeconds(),
				p.getMillis());
	}
	
	public synchronized void setUptime(final Duration value) {
		if (value == null) {
			throw new IllegalArgumentException("value cannot be null");
		}
		this.uptime = value;
		if (Platform.isFxApplicationThread()) {
			this.uptimeString.set(asString(value));
		} else {
			Platform.runLater(() -> {
				this.uptimeString.set(asString(value));
			});
		}
	}

	public synchronized Duration getUptime() {
		return uptime;
	}

	public ReadOnlyStringProperty uptimeStringProperty() {
		return uptimeString.getReadOnlyProperty();
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
