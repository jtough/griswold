package com.jimtough.griswold.beans;

import java.util.Date;

import javafx.beans.property.LongProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * JavaFX container for the status of an application instance running
 * on a server. This object is meant to be used as a bean by the JavaFX
 * user interface.
 * 
 * @author JTOUGH
 */
public abstract class GenericAppStatus {
	
	private StringProperty hostname;
	private LongProperty lastUpdated;
	
	/**
	 * Constructor
	 * @param hostname Non-null, non-empty string
	 */
	public GenericAppStatus(final String hostname) {
		if (hostname == null || hostname.isEmpty()) {
			throw new IllegalArgumentException(
					"hostname cannot be null or empty string");
		}
		setHostname(hostname);
	}

	// hostname accessors
	
	private final void setHostname(String value) {
		hostnameProperty().set(value);
	}

	public final String getHostname() {
		return hostnameProperty().get();
	}

	public StringProperty hostnameProperty() {
		if (hostname == null) {
			hostname = new SimpleStringProperty();
		}
		return hostname;
	}

	// statusString accessors

	public final String getStatusString() {
		return statusStringProperty().get();
	}

	/**
	 * Get the string that represents the status of the application instance,
	 * wrapped as a read-only property
	 * @return Non-null {@code ReadOnlyStringProperty}
	 */
	public abstract ReadOnlyStringProperty statusStringProperty();

	// lastUpdated accessors
	
	public final void setLastUpdated(Date value) {
		if (value == null) {
			throw new IllegalArgumentException("value cannot be null");
		}
		lastUpdatedProperty().set(value.getTime());
	}

	public final long getLastUpdated() {
		return lastUpdatedProperty().get();
	}

	public LongProperty lastUpdatedProperty() {
		if (lastUpdated == null) {
			lastUpdated = new SimpleLongProperty();
		}
		return lastUpdated;
	}
	
	
	

	@Override
	public String toString() {
		return hostname.getValue();
	}

}
