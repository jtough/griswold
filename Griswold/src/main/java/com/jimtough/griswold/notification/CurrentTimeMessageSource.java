package com.jimtough.griswold.notification;

import org.joda.time.DateTime;

/**
 * Returns the current date/time as a TRIVIAL importance message
 * @author JTOUGH
 */
public class CurrentTimeMessageSource implements NotificationMessageSource {

	@Override
	public NotificationMessage getMessage() {
		final String messageText = 
				"The current time is: " + DateTime.now().toString();
		return new NotificationMessage(
				messageText, NotificationImportance.TRIVIAL);
	}

}
