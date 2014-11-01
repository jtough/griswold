package com.jimtough.griswold;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.jar.JarFile;

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
	private VideoPlayerController videoPlayerController;

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

	public synchronized void showVideoPlayer() {
		if (this.videoPlayerController == null) {
			this.videoPlayerController = 
					new VideoPlayerController(this, this.primaryStage);
		}
		this.videoPlayerController.getStage().show();
		URL sampleVideoUrl = this.getClass().getResource("/big_buck_bunny.mp4");
		this.videoPlayerController.playMedia(sampleVideoUrl.toExternalForm());
	}
	
	public void exitApplication() {
		logger.info("Exiting application");
		Platform.exit();
	}
	
}
