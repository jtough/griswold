package com.jimtough.griswold.notification;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.jimtough.griswold.auth.AuthenticatedUser;
import com.jimtough.griswold.auth.UserProperties;
import com.jimtough.griswold.auth.UserProperties.UserPropertyKey;

/**
 * Randomly returns some 'fun fact' about the authenticated user as a
 * TRIVIAL importance message
 * @author JTOUGH
 */
public class AuthenticatedUserInfoMessageSource 
		implements NotificationMessageSource {

	private final List<NotificationMessage> messagePool;
	private final Random random = new Random();
	
	public AuthenticatedUserInfoMessageSource(AuthenticatedUser authUser) {
		if (authUser == null) {
			throw new IllegalArgumentException("authUser cannot be null");
		}
		this.messagePool = initializeMessagePool(authUser);
	}
	
	private List<NotificationMessage> initializeMessagePool(
			AuthenticatedUser authUser) {
		List<NotificationMessage> messagePool = 
				new ArrayList<NotificationMessage>();
		UserProperties userProperties = authUser.getUserProperties();
		if (userProperties == null) {
			// No messages! :(
			return messagePool;
		}
		
		for (UserPropertyKey propKey : UserPropertyKey.values()) {
			switch (propKey) {
				case GIVEN_NAME:
					String givenName = userProperties
							.getUserPropertyValue(propKey);
					if (givenName != null) {
						messagePool.add(
							new NotificationMessage(
									"Nice to see you, " + givenName + "!",
								NotificationImportance.TRIVIAL,
								NotificationCategory.INFO_POSITIVE,
								NotificationIcon.CHECKMARK)
						);
					}
					break;
				case CITY_NAME:
					String city = userProperties
							.getUserPropertyValue(propKey);
					if (city != null) {
						messagePool.add(
							new NotificationMessage(
								"How's the weather in " + city + "?",
								NotificationImportance.TRIVIAL,
								NotificationCategory.INFO_NEUTRAL,
								NotificationIcon.QUESTION)
						);
					}
					break;
				case JOB_TITLE:
					String title = userProperties
							.getUserPropertyValue(propKey);
					if (title != null) {
						messagePool.add(
							new NotificationMessage(
								"Do you like being a " + title + "?",
								NotificationImportance.TRIVIAL,
								NotificationCategory.INFO_NEUTRAL,
								NotificationIcon.QUESTION)
						);
					}
					break;
				case MOBILE_NUMBER:
					String mobile = userProperties
							.getUserPropertyValue(propKey);
					if (mobile != null) {
						messagePool.add(
							new NotificationMessage(
									"Can I reach you at " + mobile + "?",
									NotificationImportance.TRIVIAL,
									NotificationCategory.INFO_NEUTRAL,
									NotificationIcon.QUESTION)
						);
					}
					break;
				case SURNAME:
					String surname = userProperties
							.getUserPropertyValue(propKey);
					if (surname != null) {
						messagePool.add(
							new NotificationMessage(
									"You bring honor to the name " + surname + "!",
									NotificationImportance.TRIVIAL,
									NotificationCategory.INFO_POSITIVE,
									NotificationIcon.COOL_STUFF)
						);
					}
					break;
				case EMAIL:
					String email = userProperties
							.getUserPropertyValue(propKey);
					if (email != null) {
						messagePool.add(
							new NotificationMessage(
									"Can I email you at " + email + "?",
									NotificationImportance.TRIVIAL,
									NotificationCategory.INFO_NEUTRAL,
									NotificationIcon.QUESTION)
						);
					}
					break;
				default:
					// ignore all other enum values (for now...)
			}
		}
		return messagePool;
	}

	@Override
	public NotificationMessage peek() {
		if (this.messagePool.isEmpty()) {
			return null;
		}
		int messagePoolIndex = random.nextInt(this.messagePool.size());
		return messagePool.get(messagePoolIndex);
	}

	@Override
	public NotificationMessage take() {
		return peek();
	}

}
