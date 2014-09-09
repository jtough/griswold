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

	private final List<String> messagePool;
	private final Random random = new Random();
	
	public AuthenticatedUserInfoMessageSource(AuthenticatedUser authUser) {
		if (authUser == null) {
			throw new IllegalArgumentException("authUser cannot be null");
		}
		this.messagePool = initializeMessagePool(authUser);
	}
	
	private List<String> initializeMessagePool(AuthenticatedUser authUser) {
		List<String> messagePool = new ArrayList<String>();
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
						messagePool.add("Nice to see you, " + givenName + "!");
					}
					break;
				case CITY_NAME:
					String city = userProperties
							.getUserPropertyValue(propKey);
					if (city != null) {
						messagePool.add("How's the weather in " + city + "?");
					}
					break;
				case JOB_TITLE:
					String title = userProperties
							.getUserPropertyValue(propKey);
					if (title != null) {
						messagePool.add("Do you like being a " + title + "?");
					}
					break;
				case MOBILE_NUMBER:
					String mobile = userProperties
							.getUserPropertyValue(propKey);
					if (mobile != null) {
						messagePool.add("Can I reach you at " + mobile + "?");
					}
					break;
				case SURNAME:
					String surname = userProperties
							.getUserPropertyValue(propKey);
					if (surname != null) {
						messagePool.add("You bring honor to the name " + surname + "!");
					}
					break;
				case EMAIL:
					String email = userProperties
							.getUserPropertyValue(propKey);
					if (email != null) {
						messagePool.add("Can I email you at " + email + "?");
					}
					break;
				default:
					// ignore all other enum values (for now...)
			}
		}
		return messagePool;
	}

	@Override
	public synchronized NotificationMessage getMessage() {
		if (this.messagePool.isEmpty()) {
			return null;
		}
		int messagePoolIndex = random.nextInt(this.messagePool.size());
		String messageText = messagePool.get(messagePoolIndex);
		return new NotificationMessage(
				messageText, NotificationImportance.TRIVIAL);
	}

}
