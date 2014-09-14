package com.jimtough.griswold.notification;

/**
 * Immutable container for a notification message and its associated properties
 * @author JTOUGH
 */
public class NotificationMessage {

	private final String messageText;
	private final NotificationImportance importance;
	private final NotificationCategory category;
	private final NotificationIcon icon;

	/**
	 * Constructor
	 * @param messageText Non-null, non-empty
	 * @param importance Non-null
	 */
	public NotificationMessage(
			final String messageText,
			final NotificationImportance importance,
			final NotificationCategory category,
			final NotificationIcon icon) {
		if (messageText == null) {
			throw new IllegalArgumentException("messageText cannot be null");
		}
		if (messageText.isEmpty()) {
			throw new IllegalArgumentException("messageText cannot be empty");
		}
		if (importance == null) {
			throw new IllegalArgumentException("importance cannot be null");
		}
		if (category == null) {
			throw new IllegalArgumentException("category cannot be null");
		}
		if (icon == null) {
			throw new IllegalArgumentException("icon cannot be null");
		}
		this.messageText = messageText;
		this.importance = importance;
		this.category = category;
		this.icon = icon;
	}

	public String getMessageText() {
		return messageText;
	}

	public NotificationImportance getImportance() {
		return importance;
	}

	public NotificationCategory getCategory() {
		return category;
	}

	public NotificationIcon getIcon() {
		return icon;
	}
	
}
