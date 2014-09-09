package com.jimtough.griswold.auth;

/**
 * Thrown when an attempt to authenticate fails for any reason
 * @author JTOUGH
 */
public class AuthenticationFailureException extends Exception {

	private static final long serialVersionUID = 1L;

	public AuthenticationFailureException(String message) {
		super(message);
	}

	public AuthenticationFailureException(String message, Throwable cause) {
		super(message, cause);
	}

	public AuthenticationFailureException(Throwable cause) {
		super(cause);
	}

}
