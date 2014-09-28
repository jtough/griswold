package com.jimtough.griswold.notification;

/**
 * Source for {@code NotificationMessage} instances
 * @author JTOUGH
 */
public interface NotificationMessageSource {

	/**
	 * Peek at the currently offered {@code NotificationMessage} from this
	 * source, if any are available. If the caller wants this message, it 
	 * must invoke the {@code take()} method to indicate that the message
	 * has been taken and consumed.
	 * @return {@code NotificationMessage}, or null if none are available
	 */
	NotificationMessage peek();

	/**
	 * Take the currently offered {@code NotificationMessage} from this
	 * source, if any are available. Invoking this method signals that
	 * the message has been taken and consumed by the caller.
	 * The implementing class may or may not need to take some action
	 * when {@code take()} is invoked, such as removing the message from
	 * and internal queue. The implementing class may make this a no-op
	 * method if there is no action required when a message is taken.
	 */
	NotificationMessage take();
	
}
