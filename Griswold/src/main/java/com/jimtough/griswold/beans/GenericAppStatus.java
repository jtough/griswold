package com.jimtough.griswold.beans;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
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

	private static final DateTimeFormatter LAST_UPDATED_DATE_TIME_FORMATTER = 
			DateTimeFormat.forPattern("MM/dd HH:mm:ss");
	
	private StringProperty hostname;
	private final ReadOnlyStringWrapper lastUpdatedString;
	private final ReadOnlyStringWrapper statusString;
	private final BooleanProperty suppressAlerts;
	
	
	private GenericStatusCode genericStatusCode;
	private DateTime lastUpdatedDateTime;
	
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
		this.lastUpdatedString = new ReadOnlyStringWrapper("");
		this.statusString = new ReadOnlyStringWrapper("");
		this.suppressAlerts = new SimpleBooleanProperty(false);
		setStatusCode(GenericStatusCode.UNKNOWN);
	}

	//---------------------------------------------------
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
	
	//---------------------------------------------------
	// statusString accessors
	public synchronized void setStatusCode(GenericStatusCode genericStatusCode) {
		this.genericStatusCode = genericStatusCode;
		setStatusString(this.genericStatusCode.displayString);
	}

	public synchronized GenericStatusCode getStatusCode() {
		return this.genericStatusCode;
	}
	
	private final void setStatusString(String value) {
		if (Platform.isFxApplicationThread()) {
			this.statusString.set(value);
		} else {
			Platform.runLater(() -> {
				this.statusString.set(value);
			});
		}
	}
	
	/**
	 * Get the string that represents the status of the application instance,
	 * wrapped as a read-only property
	 * @return Non-null {@code ReadOnlyStringProperty}
	 */
	public ReadOnlyStringProperty statusStringProperty() {
		return statusString.getReadOnlyProperty();
	}
	
	//---------------------------------------------------
	// lastUpdated accessors
	String asLastUpdatedString(DateTime dateTime) {
		return LAST_UPDATED_DATE_TIME_FORMATTER.print(dateTime);
	}
	
	public synchronized void setLastUpdatedDateTime(
			DateTime lastUpdatedDateTime) {
		if (lastUpdatedDateTime == null) {
			throw new IllegalArgumentException(
					"lastUpdatedDateTime cannot be null");
		}
		this.lastUpdatedDateTime = lastUpdatedDateTime;
		setLastUpdatedString(asLastUpdatedString(this.lastUpdatedDateTime));
	}

	public synchronized DateTime getLastUpdatedDateTime() {
		return this.lastUpdatedDateTime;
	}
	
	private final void setLastUpdatedString(String value) {
		if (Platform.isFxApplicationThread()) {
			this.lastUpdatedString.set(value);
		} else {
			Platform.runLater(() -> {
				this.lastUpdatedString.set(value);
			});
		}
	}
	
	public ReadOnlyStringProperty lastUpdatedStringProperty() {
		return lastUpdatedString.getReadOnlyProperty();
	}
	
	//---------------------------------------------------
	// suppressAlerts accessors
	public synchronized void setSuppressAlerts(final boolean value) {
		if (Platform.isFxApplicationThread()) {
			this.suppressAlerts.set(value);
		} else {
			Platform.runLater(() -> {
				this.suppressAlerts.set(value);
			});
		}
		
		
	}

	public synchronized boolean getSuppressAlerts() {
		return this.suppressAlerts.get();
	}
	
	public BooleanProperty suppressAlertsProperty() {
		return suppressAlerts;
	}
	
	//---------------------------------------------------

	@Override
	public String toString() {
		return hostname.getValue();
	}

}
