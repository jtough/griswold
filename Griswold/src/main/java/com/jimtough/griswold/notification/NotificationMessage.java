package com.jimtough.griswold.notification;

/**
 * Immutable container for a notification message and its associated properties
 * @author JTOUGH
 */
public class NotificationMessage {

	private final String messageText;
	private final NotificationImportance importance;

	/**
	 * Constructor
	 * @param messageText Non-null, non-empty
	 * @param importance Non-null
	 */
	public NotificationMessage(
			final String messageText,
			final NotificationImportance importance) {
		if (messageText == null) {
			throw new IllegalArgumentException("messageText cannot be null");
		}
		if (messageText.isEmpty()) {
			throw new IllegalArgumentException("messageText cannot be empty");
		}
		if (importance == null) {
			throw new IllegalArgumentException("importance cannot be null");
		}
		this.messageText = messageText;
		this.importance = importance;
	}

	public String getMessageText() {
		return messageText;
	}

	public NotificationImportance getImportance() {
		return importance;
	}
	
}
