package com.jimtough.griswold;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.StringProperty;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jimtough.griswold.notification.NotificationMessage;
import com.jimtough.griswold.notification.NotificationMessageSource;

public class NotificationAreaUpdater {

	private static final Logger logger =
			LoggerFactory.getLogger(NotificationAreaUpdater.class);

	private final ReadOnlyStringWrapper notificationAreaTextStringWrapper;
	private final StringProperty notificationAreaIconSVGString;
	private final ObjectProperty<Paint> notificationAreaIconFillPaint;
	private final ObjectProperty<Background> notificationAreaBackground;
	private final List<NotificationMessageSource> messageSourceList =
			new ArrayList<NotificationMessageSource>();
	private final Random random = new Random();
	
	public NotificationAreaUpdater(
			ReadOnlyStringWrapper notificationAreaTextStringWrapper,
			StringProperty notificationAreaIconSVGString,
			ObjectProperty<Paint> notificationAreaIconFillPaint,
			ObjectProperty<Background> notificationAreaBackground) {
		if (notificationAreaTextStringWrapper == null) {
			throw new IllegalArgumentException(
					"notificationAreaTextStringWrapper cannot be null");
		}
		if (notificationAreaIconSVGString == null) {
			throw new IllegalArgumentException(
					"notificationAreaIconSVGString cannot be null");
		}
		if (notificationAreaIconFillPaint == null) {
			throw new IllegalArgumentException(
					"notificationAreaIconFillPaint cannot be null");
		}
		if (notificationAreaBackground == null) {
			throw new IllegalArgumentException(
					"notificationAreaBackground cannot be null");
		}
		this.notificationAreaTextStringWrapper = 
				notificationAreaTextStringWrapper;
		this.notificationAreaIconSVGString = 
				notificationAreaIconSVGString;
		this.notificationAreaIconFillPaint =
				notificationAreaIconFillPaint;
		this.notificationAreaBackground =
				notificationAreaBackground;
	}
	
	public synchronized void addMessageSource(
			NotificationMessageSource messageSource) {
		if (messageSource == null) {
			throw new IllegalArgumentException("messageSource cannot be null");
		}
		messageSourceList.add(messageSource);
	}

	NotificationMessage selectMessagePreferringHighestImportance(
			List<NotificationMessageSource> messageSourceList) {
		NotificationMessage messageToUse = null;
		for (NotificationMessageSource source : messageSourceList) {
			NotificationMessage notificationMessage = source.offerMessage();
			if (notificationMessage == null) {
				logger.info("Null message offered");
			} else if (messageToUse == null) {
				messageToUse = notificationMessage;
				logger.info("Found first offered message: [" +
						messageToUse.getMessageText() + "]");
			} else {
				if (messageToUse.getImportance().ordinal() < 
						notificationMessage.getImportance().ordinal()) {
					logger.info("Replacing less important message: [" +
							messageToUse.getMessageText() + 
							"] with more important message: [" +
							notificationMessage.getMessageText() + "]");
					messageToUse = notificationMessage;
				} else if (messageToUse.getImportance().ordinal() == 
						notificationMessage.getImportance().ordinal()) {
					boolean replaceWithNewMessage = random.nextBoolean();
					if (replaceWithNewMessage) {
						logger.info("Randomly decided to replace message [" +
								messageToUse.getMessageText() + 
								"] with equally important message: [" +
								notificationMessage.getMessageText() + "]");
						messageToUse = notificationMessage;
					} else {
						logger.info("Randomly decided to keep message [" +
								messageToUse.getMessageText() + 
								"] because equally important message: [" +
								notificationMessage.getMessageText() + 
								"] lost the coin toss");
					}
				} else {
					logger.info("New message is less important - ignore");
				}
			}
		}
		return messageToUse;
	}
	
	public synchronized void rotateText() {
		logger.info("rotateText() | INVOKED");
		if (messageSourceList.isEmpty()) {
			logger.warn("rotateText() | No message sources have been set");
			this.notificationAreaTextStringWrapper.set("");
		}
		//int messageSourceIndex = random.nextInt(this.messageSourceList.size());
		//NotificationMessageSource messageSource =
		//		this.messageSourceList.get(messageSourceIndex);
		//NotificationMessage message = messageSource.getMessage();
		
		NotificationMessage message = 
				selectMessagePreferringHighestImportance(messageSourceList);

		final Color backgroundColor;
		switch (message.getCategory()) {
			case INFO_NEUTRAL:
				backgroundColor = Color.TRANSPARENT;
				break;
			case INFO_POSITIVE:
				backgroundColor = Color.LIGHTGREEN;
				break;
			case WARNING:
				backgroundColor = Color.PALEGOLDENROD;
				break;
			case ERROR:
				backgroundColor = Color.LIGHTSALMON;
				break;
			default:
				throw new IllegalArgumentException(
						"Unknown enum: " + message.getCategory());
		}
		
		this.notificationAreaTextStringWrapper.set(message.getMessageText());
		this.notificationAreaIconSVGString.set(message.getIcon().svgPathString);
		this.notificationAreaIconFillPaint.set(message.getIcon().fillPaint);
		this.notificationAreaBackground.set(new Background(
				new BackgroundFill(backgroundColor, null, null)));
	}
	
	public ReadOnlyStringProperty notificationAreaTextProperty() {
		return notificationAreaTextStringWrapper.getReadOnlyProperty();
	}
	
}
