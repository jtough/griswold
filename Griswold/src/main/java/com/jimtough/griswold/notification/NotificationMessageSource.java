package com.jimtough.griswold.notification;

/**
 * Source for {@code NotificationMessage} instances
 * @author JTOUGH
 */
public interface NotificationMessageSource {

	/**
	 * Offer a {@code NotificationMessage}, if any are available, to the
	 * caller. If the caller wants this message, it will invoke the
	 * {@code takeMessage()} method to indicate that the message has been
	 * accepted and consumed.
	 * @return {@code NotificationMessage}, or null if none are available
	 */
	NotificationMessage offerMessage();

	/**
	 * Informs the message source that a 'peeked' message will be taken
	 * (typically to be displayed). This gives the source a chance to
	 * remove that message, if it has any sort of queuing mechanism.
	 * The message source can make this a no-op method if there is no
	 * action to be taken when a message is taken.
	 */
	void takeMessage(NotificationMessage notificationMessage);
	
}
