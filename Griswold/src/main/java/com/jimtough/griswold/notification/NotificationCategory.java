package com.jimtough.griswold.notification;

/**
 * Category associated with a notification message, used to style the message
 * when it is displayed
 * @author JTOUGH
 */
public enum NotificationCategory {
	/**
	 * Used for information that is neither good nor bad
	 */
	INFO_NEUTRAL,
	/**
	 * Used for good news (a warning/error has been resolved)
	 */
	INFO_POSITIVE,
	WARNING,
	ERROR
}
