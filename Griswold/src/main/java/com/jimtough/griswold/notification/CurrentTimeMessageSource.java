package com.jimtough.griswold.notification;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Returns the current date/time as a TRIVIAL importance message
 * @author JTOUGH
 */
public class CurrentTimeMessageSource implements NotificationMessageSource {

	private static final DateTimeFormatter CURRENT_TIME_FORMATTER = 
			DateTimeFormat.forPattern("HH:mm:ss z");

	@Override
	public NotificationMessage offerMessage() {
		final String messageText = 
				"The current time is: " + 
				CURRENT_TIME_FORMATTER.print(DateTime.now());
		return new NotificationMessage(
				messageText, 
				NotificationImportance.TRIVIAL,
				NotificationCategory.INFO_NEUTRAL,
				NotificationIcon.CLOCK);
	}

	@Override
	public void takeMessage(NotificationMessage notificationMessage) {
		// nothing to do here
	}

}
