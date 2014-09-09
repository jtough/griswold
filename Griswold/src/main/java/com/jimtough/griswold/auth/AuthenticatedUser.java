package com.jimtough.griswold.auth;

/**
 * Represents a user that has been authenticated as a valid user of the
 * application
 * 
 * @author JTOUGH
 */
public class AuthenticatedUser {

	// TODO Add some additional info, such as 'user roles', etc
	
	private UserProperties userProperties;

	public synchronized void setUserProperties(UserProperties userProperties) {
		this.userProperties = userProperties;
	}

	public synchronized UserProperties getUserProperties() {
		return this.userProperties;
	}
	
}
