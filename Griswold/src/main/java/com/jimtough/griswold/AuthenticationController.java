package com.jimtough.griswold;

import static com.jimtough.griswold.SVGStringConstants.*;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jimtough.griswold.auth.AuthenticatedUser;
import com.jimtough.griswold.auth.AuthenticationFailureException;
import com.jimtough.griswold.auth.Credentials;
import com.jimtough.griswold.auth.UserAuthenticator;
import com.jimtough.griswold.auth.UserAuthenticatorStub;
import com.jimtough.griswold.auth.UserProperties.UserPropertyKey;
import com.jimtough.griswold.beans.User;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;


public class AuthenticationController {

	private static final Logger logger =
			LoggerFactory.getLogger(AuthenticationController.class);

	// all text, borders, svg paths will use white
	private static final Color FOREGROUND_COLOR_FOR_INPUT_CONTROLS = 
			Color.rgb(255, 255, 255, .9);
	
	private static final Color WELCOME_TEXT_FILL_COLOR = Color.rgb(255, 153, 51);

	private static final int SCENE_WIDTH = 640;
	private static final int SCENE_HEIGHT = 650;
	private static final int BACKGROUND_RECT_HEIGHT = 100;
	
	private static final double OPACITY_LEVEL = 0.9;
	
	// create a model representing a user
	private final User user = new User();
	
	//private final static String MY_PASS = "xxx";
	private final static String TEST_PASSWORD = UserAuthenticatorStub.STUB_PASSWORD;
	private final static BooleanProperty GRANTED_ACCESS = new SimpleBooleanProperty(false);
	private final static int MAX_ATTEMPTS = 3;
	private final IntegerProperty ATTEMPTS = new SimpleIntegerProperty(0);
	private AuthenticatedUser authUser = null;

	private final Stage stage;
	private final NavigationController navController;
	private final UserAuthenticator userAuthenticator;
	private Group root;
	private VBox formLayout;
	private StackPane sceneStackPane;

	
	public AuthenticationController(
			NavigationController navController,
			UserAuthenticator userAuthenticator) {
		if (navController == null) {
			throw new IllegalArgumentException("navController cannot be null");
		}
		if (userAuthenticator == null) {
			throw new IllegalArgumentException("userAuthenticator cannot be null");
		}
		this.navController = navController;
		this.userAuthenticator = userAuthenticator;
		this.stage = new Stage();
	}
	
	public synchronized AuthenticatedUser getAuthUser() {
		return authUser;
	}

	private synchronized void setAuthUser(AuthenticatedUser authUser) {
		this.authUser = authUser;
	}

	public void onSuccessfulAuthentication(final AuthenticatedUser authUser) {
		logger.info("onSuccessfulAuthentication() | INVOKED");
		if (authUser == null) {
			throw new IllegalArgumentException("authUser cannot be null");
		}
		String firstName = authUser.getUserProperties().getUserPropertyValue(
				UserPropertyKey.GIVEN_NAME);
		String lastName = authUser.getUserProperties().getUserPropertyValue(
				UserPropertyKey.SURNAME);
		displayWelcomeTextAndCloseDialog(firstName, lastName);
	}
	
	private void displayWelcomeTextAndCloseDialog(
			final String firstNameOfUser,
			final String lastNameOfUser) {
		VBox welcomeTextVBox = new VBox(20);

		welcomeTextVBox.setAlignment(Pos.CENTER);

		Text textWelcome = new Text("Welcome");
		textWelcome.setFont(new Font(132.0));
		textWelcome.setFill(WELCOME_TEXT_FILL_COLOR);
		textWelcome.setOpacity(1.0);
		textWelcome.setStroke(Color.BLACK);
		textWelcome.visibleProperty().set(false);

		LinearGradient linearGradient = new LinearGradient(
				0, 
				0, 
				0, 
				1, 
				true, 
				CycleMethod.NO_CYCLE,
				//new Stop(0.1, Color.rgb(255, 200, 0, 0.784)),
				//new Stop(1.0, Color.rgb(0, 0, 0, 0.784))
				//new Stop(0.1, Color.rgb(83, 133, 166, 0.784)),
				new Stop(0.1, Color.rgb(101, 219, 219)),
				new Stop(0.5, Color.CYAN),
				new Stop(1.0, Color.rgb(0, 0, 0))
			);
		
		Text textFirstNameOfUser = new Text(
				firstNameOfUser != null ? firstNameOfUser : "");
		textFirstNameOfUser.setFont(new Font(108.0));
		textFirstNameOfUser.setFill(linearGradient);
		textFirstNameOfUser.setOpacity(0.0);
		textFirstNameOfUser.setStroke(Color.BLACK);
		textFirstNameOfUser.visibleProperty().set(false);
		
		
		Text textLastNameOfUser = new Text(
				lastNameOfUser != null ? lastNameOfUser : "");
		textLastNameOfUser.setFont(new Font(108.0));
		textLastNameOfUser.setFill(linearGradient);
		textLastNameOfUser.setOpacity(0.0);
		textLastNameOfUser.setStroke(Color.BLACK);
		textLastNameOfUser.visibleProperty().set(false);

		welcomeTextVBox.getChildren().addAll(
				textFirstNameOfUser, 
				textWelcome, 
				textLastNameOfUser);
		
		this.sceneStackPane.getChildren().add(welcomeTextVBox);
		
		textWelcome.scaleXProperty().set(0.01);
		textWelcome.scaleYProperty().set(0.01);
		textWelcome.visibleProperty().set(true);

		textFirstNameOfUser.visibleProperty().set(true);
		textLastNameOfUser.visibleProperty().set(true);

		//----------------------------------------------------------------
		// "Welcome" text expand in transition
		final Duration welcomeTextInRotateDur = Duration.millis(2000);
		final Duration welcomeTextInScaleDur = Duration.millis(2000);
		ParallelTransition welcomeTextInTrans = null;
		RotateTransition rotateT = new RotateTransition(welcomeTextInRotateDur);
		rotateT.setFromAngle(0.0);
		rotateT.setToAngle(1440.0);
		ScaleTransition scaleUpT = new ScaleTransition(welcomeTextInScaleDur);
		scaleUpT.setFromX(0.01);
		scaleUpT.setFromY(0.01);
		scaleUpT.setToX(1.0);
		scaleUpT.setToY(1.0);
		welcomeTextInTrans = new ParallelTransition(textWelcome, rotateT, scaleUpT);
		welcomeTextInTrans.setOnFinished(actionEvent -> {
			logger.trace("welcomeTextInTrans finished");
		});
		//----------------------------------------------------------------
		
		//----------------------------------------------------------------
		// Application image and input form fades away transition
		final Duration dialogFadeOutDur = Duration.millis(2000);
		FadeTransition dialogFadeOutTrans = new FadeTransition(dialogFadeOutDur);
		dialogFadeOutTrans.setNode(formLayout);
		dialogFadeOutTrans.setFromValue(1.0);
		dialogFadeOutTrans.setToValue(0.0);
		//----------------------------------------------------------------
		
		ParallelTransition welcomeInAndDialogOutTrans = new ParallelTransition(
				dialogFadeOutTrans, welcomeTextInTrans);
		welcomeInAndDialogOutTrans.setOnFinished(actionEvent -> {
			logger.trace("welcomeInAndDialogOutTrans finished");
		});

		//----------------------------------------------------------------
		// User first and last name fade in transition
		final Duration firstNameFadeInDur = Duration.millis(1500);
		final Duration lastNameFadeInDur = Duration.millis(1500);
		FadeTransition firstNameFadeInTrans = new FadeTransition(firstNameFadeInDur);
		firstNameFadeInTrans.setNode(textFirstNameOfUser);
		firstNameFadeInTrans.setFromValue(0.0);
		firstNameFadeInTrans.setToValue(1.0);
		FadeTransition lastNameFadeInTrans = new FadeTransition(lastNameFadeInDur);
		lastNameFadeInTrans.setNode(textLastNameOfUser);
		lastNameFadeInTrans.setFromValue(0.0);
		lastNameFadeInTrans.setToValue(1.0);
		lastNameFadeInTrans.setDelay(Duration.millis(750));
		//----------------------------------------------------------------
		
		ParallelTransition namesFadeInTrans = new ParallelTransition(
				firstNameFadeInTrans, lastNameFadeInTrans);
		namesFadeInTrans.setOnFinished(actionEvent -> {
			logger.trace("namesFadeInTrans finished");
		});

		final Duration welcomeTextVBoxFadeOutDur = Duration.millis(2000);
		FadeTransition welcomeTextVBoxFadeOutTrans = new FadeTransition(welcomeTextVBoxFadeOutDur);
		welcomeTextVBoxFadeOutTrans.setNode(welcomeTextVBox);
		welcomeTextVBoxFadeOutTrans.setFromValue(1.0);
		welcomeTextVBoxFadeOutTrans.setToValue(0.0);
		welcomeTextVBoxFadeOutTrans.setDelay(Duration.millis(2000));
		
		PauseTransition finalPauseT = new PauseTransition(Duration.millis(1000));
		finalPauseT.setOnFinished((actionEvent) -> {
			this.navController.authenticationSuccessful(authUser);
			this.stage.close();
		});
		
		SequentialTransition seqTransition = new SequentialTransition();
		seqTransition.getChildren().addAll(welcomeInAndDialogOutTrans, namesFadeInTrans, welcomeTextVBoxFadeOutTrans, finalPauseT);
		seqTransition.play();
	}
	
	ImageView createImageView(
			ReadOnlyDoubleProperty sceneWidthProperty) 
			throws IOException {
		ImageView imageView = new ImageView();
		// Keep the aspect ratio of the original image
		imageView.setPreserveRatio(true);
		imageView.fitWidthProperty().bind(sceneWidthProperty);
		InputStream is = this.getClass().getResourceAsStream(
				"/login-background-image.jpg");
		BufferedInputStream bis = new BufferedInputStream(is);
		Image image = new Image(bis);
		imageView.setImage(image);
		bis.close();
		imageView.setOpacity(OPACITY_LEVEL);
		return imageView;
	}

	Rectangle createInputTextBackgroundRectangle() {
		Rectangle background = new Rectangle(SCENE_WIDTH, BACKGROUND_RECT_HEIGHT);
		background.setX(0);
		background.setY(0);
		background.setArcHeight(15);
		background.setArcWidth(15);
		background.setFill(Color.rgb(160, 160, 160, OPACITY_LEVEL));
		return background;
	}
	
	TextField createUsernameText() {
		TextField usernameField = new TextField();
		//---------------------------------------------------------------
		// password text field 
		usernameField.setFont(Font.font("SanSerif", 20));
		usernameField.setPromptText("Username");
		usernameField.setStyle("-fx-text-fill:black; "
				+ "-fx-prompt-text-fill:gray; "
				+ "-fx-highlight-text-fill:black; "
				+ "-fx-highlight-fill: gray; "
				+ "-fx-background-color: rgba(255, 153, 51, .9); ");
		usernameField.prefWidthProperty().bind(stage.widthProperty().subtract(55));
		//---------------------------------------------------------------
		
		usernameField.textProperty().bind(user.userNameProperty());
		
		return usernameField;
	}

	void addUsernameInputToGridPane(
			GridPane gridpane, 
			TextField userName) {
		GridPane.setHalignment(userName, HPos.LEFT);
		gridpane.add(userName, 1, 0);
	}

	PasswordField createPasswordField() {
		PasswordField passwordField = new PasswordField();
		//---------------------------------------------------------------
		// password text field 
		passwordField.setFont(Font.font("SanSerif", 20));
		passwordField.setPromptText("Password");
		passwordField.setStyle("-fx-text-fill:black; "
				+ "-fx-prompt-text-fill:gray; "
				+ "-fx-highlight-text-fill:black; "
				+ "-fx-highlight-fill: gray; "
				//+ "-fx-background-color: rgba(255, 255, 255, .80); ");
				+ "-fx-background-color: rgba(255, 153, 51, .9); ");
		passwordField.prefWidthProperty().bind(stage.widthProperty().subtract(55));
		Tooltip passwordTT = new Tooltip(
				"Press Escape key to quit. " + 
				"Fake password is '" + TEST_PASSWORD + "'.");
		SVGPath passwordTtIcon = new SVGPath();
		passwordTtIcon.setFill(FOREGROUND_COLOR_FOR_INPUT_CONTROLS);
		passwordTtIcon.setContent(SVG_INFO);
		passwordTT.setGraphic(passwordTtIcon);
		passwordField.setTooltip(passwordTT);
		//---------------------------------------------------------------
		
		user.passwordProperty().bind(passwordField.textProperty());
		
		return passwordField;
	}

	SVGPath createDeniedIcon() {
		SVGPath deniedIcon = new SVGPath();
		// error X icon 
		deniedIcon.setFill(Color.rgb(255, 0, 0, .9));
		deniedIcon.setStroke(Color.WHITE);// 
		deniedIcon.setContent(SVG_X);
		deniedIcon.setVisible(false);
		return deniedIcon;
	}

	SVGPath createGrantedIcon() {
		SVGPath grantedIcon = new SVGPath();
		// checkmark icon
		grantedIcon.setFill(Color.rgb(0, 255, 0, .9));
		grantedIcon.setStroke(Color.WHITE);// 
		grantedIcon.setContent(SVG_CHECKMARK);
		grantedIcon.setVisible(false);
		grantedIcon.visibleProperty().bind(GRANTED_ACCESS);
		return grantedIcon;
	}
	
	void addPasswordInputToGridPane(
			GridPane gridpane,
			PasswordField passwordField,
			SVGPath deniedIcon,
			SVGPath grantedIcon) {
		StackPane accessIndicator = new StackPane();
		accessIndicator.getChildren().addAll(deniedIcon, grantedIcon);
		accessIndicator.setAlignment(Pos.CENTER);
		
		GridPane.setHalignment(passwordField, HPos.LEFT);
		gridpane.add(passwordField, 1, 1);
		GridPane.setHalignment(accessIndicator, HPos.RIGHT);
		gridpane.add(accessIndicator, 2, 1);
	}
	
	public void createAuthenticationDialogScene(Stage parent) {
		// create a transparent stage
		stage.initStyle(StageStyle.TRANSPARENT);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.initOwner(parent);
		stage.setResizable(false);
		stage.centerOnScreen();
		
		this.root = new Group();
		Scene scene = new Scene(
				this.root, SCENE_WIDTH, SCENE_HEIGHT, Color.rgb(0, 0, 0, 0));
		stage.setScene(scene);
		scene.addEventHandler(KeyEvent.KEY_RELEASED,
			new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent event) {
					if (event.getCode().equals(KeyCode.ESCAPE)) {
						logger.info("Escape key pressed - exiting application");
						Platform.exit();
					}
				}
			});
		
		StackPane stackPane = new StackPane();
		Rectangle backgroundRect = createInputTextBackgroundRectangle();

		GridPane gridpaneX = new GridPane();
		gridpaneX.setPadding(new Insets(5));
		gridpaneX.setHgap(5);
		gridpaneX.setVgap(5);
		ColumnConstraints col1Size = new ColumnConstraints(8);
		ColumnConstraints col2Size = new ColumnConstraints(550);
		ColumnConstraints col3Size = new ColumnConstraints(48);
		gridpaneX.getColumnConstraints().addAll(col1Size, col2Size, col3Size);
		gridpaneX.setGridLinesVisible(false);
		//gridpaneX.setBackground(new Background(new BackgroundFill(Color.SALMON, null, null)));
		//gridpaneX.setGridLinesVisible(true);

		stackPane.getChildren().addAll(backgroundRect, gridpaneX);

		ImageView imageView = null;
		try {
			imageView = createImageView(scene.widthProperty());
		} catch (Exception e) {
			throw new RuntimeException("Cannot load image", e);
		}
		
		Text applicationNameText = new Text(MainApp.APPLICATION_NAME);
		applicationNameText.setFont(new Font(32.0));
		applicationNameText.setFill(WELCOME_TEXT_FILL_COLOR);
		applicationNameText.setOpacity(OPACITY_LEVEL);
		applicationNameText.setStroke(Color.BLACK);
		//applicationNameText.visibleProperty().set(false);
		
		TextField userName = createUsernameText();
		addUsernameInputToGridPane(gridpaneX, userName);
		
		PasswordField passwordField = createPasswordField();
		SVGPath deniedIcon = createDeniedIcon();
		SVGPath grantedIcon = createGrantedIcon();
		
		addPasswordInputToGridPane(gridpaneX, passwordField, deniedIcon, grantedIcon);

		// user hits the enter key
		passwordField.setOnAction(actionEvent -> {
			Credentials credentials = new Credentials(
					user.userNameProperty().get(), 
					user.passwordProperty().get());
			setAuthUser(null);
			try {
				AuthenticatedUser authUser = 
						userAuthenticator.authenticate(credentials);
				setAuthUser(authUser);
				logger.info("User is granted access.");
				this.onSuccessfulAuthentication(getAuthUser());
			} catch (AuthenticationFailureException e) {
				logger.info("Authentication failed | " + e.getMessage());
				deniedIcon.setVisible(true);
				ATTEMPTS.set(ATTEMPTS.add(1).get());
				logger.info("Attempts: " + ATTEMPTS.get());
			}
		});

		// listener when the user types into the password field
		passwordField.textProperty().addListener((obs, ov, nv) -> {
			boolean granted = passwordField.getText().equals(TEST_PASSWORD);
			GRANTED_ACCESS.set(granted);
			if (granted) {
				deniedIcon.setVisible(false);
			}
		});
		
		
		// listener on number of attempts
		ATTEMPTS.addListener((obs, ov, nv) -> {
			if (MAX_ATTEMPTS == nv.intValue()) {
				// failed attempts
				logger.info("User is denied access.");
				Platform.exit();
			}
		});

		this.formLayout = new VBox(10);
		this.formLayout.getChildren().addAll(applicationNameText, imageView, stackPane);
		
		// Create a StackPane and put the entire dialog content in it.
		// We will stack welcome text on top later when the user has
		// successfully logged in.
		this.sceneStackPane = new StackPane();
		this.sceneStackPane.getChildren().add(this.formLayout);
		//this.sceneStackPane.setBackground(new Background(new BackgroundFill(Color.YELLOW, null, null)));
		this.root.getChildren().addAll(this.sceneStackPane);
		
		// Set focus to the password input field when window initially displays
		passwordField.requestFocus();

		stage.show();

	}
	
}
