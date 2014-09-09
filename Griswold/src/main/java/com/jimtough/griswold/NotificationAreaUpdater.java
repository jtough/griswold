package com.jimtough.griswold;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jimtough.griswold.notification.NotificationMessage;
import com.jimtough.griswold.notification.NotificationMessageSource;

public class NotificationAreaUpdater {

	private static final Logger logger =
			LoggerFactory.getLogger(NotificationAreaUpdater.class);

	private final ReadOnlyStringWrapper notificationAreaTextStringWrapper;
	private final List<NotificationMessageSource> messageSourceList =
			new ArrayList<NotificationMessageSource>();
	private final Random random = new Random();
	
	public NotificationAreaUpdater(
			ReadOnlyStringWrapper notificationAreaTextStringWrapper) {
		if (notificationAreaTextStringWrapper == null) {
			throw new IllegalArgumentException(
					"notificationAreaTextStringWrapper cannot be null");
		}
		this.notificationAreaTextStringWrapper = 
				notificationAreaTextStringWrapper;
	}
	
	public synchronized void addMessageSource(
			NotificationMessageSource messageSource) {
		if (messageSource == null) {
			throw new IllegalArgumentException("messageSource cannot be null");
		}
		messageSourceList.add(messageSource);
	}
	
	public synchronized void rotateText() {
		logger.info("rotateText() | INVOKED");
		if (messageSourceList.isEmpty()) {
			logger.warn("rotateText() | No message sources have been set");
			this.notificationAreaTextStringWrapper.set("");
		}
		int messageSourceIndex = random.nextInt(this.messageSourceList.size());
		NotificationMessageSource messageSource =
				this.messageSourceList.get(messageSourceIndex);
		NotificationMessage message = messageSource.getMessage();
		// TODO Also need to change the notification icon based on the message importance
		this.notificationAreaTextStringWrapper.set(message.getMessageText());
	}
	
	public ReadOnlyStringProperty notificationAreaTextProperty() {
		return notificationAreaTextStringWrapper.getReadOnlyProperty();
	}
	
}
