package com.jimtough.griswold;

import javafx.application.Platform;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NavigationController {
	
	private static final Logger logger =
			LoggerFactory.getLogger(NavigationController.class);

	private final Stage primaryStage;
	
	public NavigationController(Stage primaryStage) {
		if (primaryStage == null) {
			throw new IllegalArgumentException("primaryStage cannot be null");
		}
		this.primaryStage = primaryStage;
	}
	
	public void authenticationSuccessful() {
		this.primaryStage.show();
	}
	
	public void exitApplication() {
		logger.info("Exiting application");
		Platform.exit();
	}
	
}
