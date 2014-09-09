package com.jimtough.griswold;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotificationAreaUpdater {

	private static final Logger logger =
			LoggerFactory.getLogger(NotificationAreaUpdater.class);

	private final ReadOnlyStringWrapper notificationAreaTextStringWrapper;
	
	public NotificationAreaUpdater(
			ReadOnlyStringWrapper notificationAreaTextStringWrapper) {
		if (notificationAreaTextStringWrapper == null) {
			throw new IllegalArgumentException(
					"notificationAreaTextStringWrapper cannot be null");
		}
		this.notificationAreaTextStringWrapper = 
				notificationAreaTextStringWrapper;
	}
	
	public synchronized void rotateText() {
		logger.info("rotateText() | INVOKED");
		this.notificationAreaTextStringWrapper.set(
				"The current time is: " + DateTime.now());
	}
	
	public ReadOnlyStringProperty notificationAreaTextProperty() {
		return notificationAreaTextStringWrapper.getReadOnlyProperty();
	}
	
}
