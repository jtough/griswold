package com.jimtough.griswold.notification;

/**
 * Source for {@code NotificationMessage} instances
 * @author JTOUGH
 */
public interface NotificationMessageSource {

	/**
	 * Get a {@code NotificationMessage}, if any are available
	 * @return {@code NotificationMessage}, or null if none are available
	 */
	NotificationMessage getMessage();
	
}
