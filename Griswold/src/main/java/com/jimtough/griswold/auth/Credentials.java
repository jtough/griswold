package com.jimtough.griswold.auth;

/**
 * Immutable container for authentication credentials
 * @author JTOUGH
 */
public class Credentials {

	private final String username;
	private final String password;
	
	public Credentials(final String username, final String password) {
		this.username = username;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
	
}
