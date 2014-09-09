package com.jimtough.griswold.auth;

import com.jimtough.griswold.auth.UserProperties.UserPropertyKey;

/**
 * Stub implementation of the interface that returns an
 * AuthenticatedUser object containing arbitrary test data
 * 
 * @author JTOUGH
 */
public class UserAuthenticatorStub implements UserAuthenticator {

	public final static String STUB_PASSWORD = "xxx";
	
	@Override
	public AuthenticatedUser authenticate(
			Credentials credentials)
			throws AuthenticationFailureException {
		if (credentials.getPassword().equals(STUB_PASSWORD)) {
			AuthenticatedUser authUser = new AuthenticatedUser();
			addSomeFunProperties(authUser, credentials);
			return authUser;
		}
		throw new AuthenticationFailureException("The demo password is '" +
				STUB_PASSWORD + "' but you entered '" +
				credentials.getPassword() + "'");
	}

	private void addSomeFunProperties(
			AuthenticatedUser authUser,
			Credentials credentials) {
		final UserProperties uProps = new UserProperties();
		uProps.setUserPropertyValue(UserPropertyKey.CITY_NAME, 
				"Faketon");
		uProps.setUserPropertyValue(UserPropertyKey.PROV_OR_STATE_ABBREV, 
				"BC");
		uProps.setUserPropertyValue(UserPropertyKey.COUNTRY_ABBREV, 
				"CA");
		uProps.setUserPropertyValue(UserPropertyKey.DEPARTMENT, 
				"Software Development");
		uProps.setUserPropertyValue(UserPropertyKey.EMAIL, 
				credentials.getUsername() + "@fakeco.com");
		uProps.setUserPropertyValue(UserPropertyKey.GIVEN_NAME, 
				"Foo");
		uProps.setUserPropertyValue(UserPropertyKey.SURNAME, 
				"Bar");
		uProps.setUserPropertyValue(UserPropertyKey.JOB_TITLE, 
				"Code Monkey");
		uProps.setUserPropertyValue(UserPropertyKey.MAIL_NICKNAME, 
				credentials.getUsername());
		uProps.setUserPropertyValue(UserPropertyKey.USER_PRINCIPAL_NAME, 
				credentials.getUsername() + "@fakeco.com");
		
		authUser.setUserProperties(uProps);
	}
	
}
