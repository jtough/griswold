package com.jimtough.griswold;

import javafx.application.Platform;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jimtough.griswold.auth.AuthenticatedUser;

public class NavigationController {
	
	private static final Logger logger =
			LoggerFactory.getLogger(NavigationController.class);

	private final Stage primaryStage;
	private AuthenticatedUser authUser;

	public NavigationController(Stage primaryStage) {
		if (primaryStage == null) {
			throw new IllegalArgumentException("primaryStage cannot be null");
		}
		this.primaryStage = primaryStage;
	}
	
	public synchronized AuthenticatedUser getAuthUser() {
		logger.info("getAuthUser() | INVOKED");
		return authUser;
	}

	private synchronized void setAuthUser(AuthenticatedUser authUser) {
		logger.info("setAuthUser() | INVOKED");
		this.authUser = authUser;
	}
	
	public void authenticationSuccessful(final AuthenticatedUser authUser) {
		if (authUser == null) {
			throw new IllegalArgumentException("authUser cannot be null");
		}
		setAuthUser(authUser);
		this.primaryStage.show();
	}
	
	public void exitApplication() {
		logger.info("Exiting application");
		Platform.exit();
	}
	
}
