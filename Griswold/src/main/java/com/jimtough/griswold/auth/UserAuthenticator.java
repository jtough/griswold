package com.jimtough.griswold.auth;

/**
 * Authenticates a user in whatever manner is appropriate for the application
 * @author JTOUGH
 */
public interface UserAuthenticator {

	/**
	 * Attempts to authenticate a user with the credentials provided,
	 * throwing an exception if the attempt is unsuccessful
	 * @param credentials Non-null
	 * @throws AuthenticationFailureException
	 * @return Non-null {@code AuthenticatedUser}
	 */
	AuthenticatedUser authenticate(
			Credentials credentials) 
			throws AuthenticationFailureException;
	
}
