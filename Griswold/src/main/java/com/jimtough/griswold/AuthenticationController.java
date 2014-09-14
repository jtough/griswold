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
import com.jimtough.griswold.beans.User;

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
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class AuthenticationController {

	private static final Logger logger =
			LoggerFactory.getLogger(AuthenticationController.class);

	// all text, borders, svg paths will use white
	private static final Color FOREGROUND_COLOR_FOR_INPUT_CONTROLS = 
			Color.rgb(255, 255, 255, .9);

	private static final int SCENE_WIDTH = 640;
	private static final int SCENE_HEIGHT = 650;
	private static final int BACKGROUND_RECT_HEIGHT = 100;
	
	private static final double OPACITY_LEVEL = 0.75;
	
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
		this.navController.authenticationSuccessful(authUser);
		this.stage.close();
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
		// rounded rectangular background 
		Rectangle background = new Rectangle(SCENE_WIDTH, BACKGROUND_RECT_HEIGHT);
		background.setX(0);
		background.setY(0);
		background.setArcHeight(15);
		background.setArcWidth(15);
		//background.setFill(Color.rgb(0, 0, 0, .55));
		background.setFill(Color.rgb(0, 0, 0, OPACITY_LEVEL));
		background.setStrokeWidth(1.5);
		background.setStroke(FOREGROUND_COLOR_FOR_INPUT_CONTROLS);
		return background;
	}
	
	Text createUsernameText() {
		Text userName = new Text();
		userName.setFont(Font.font("SanSerif", FontWeight.BOLD, 30));
		userName.setFill(FOREGROUND_COLOR_FOR_INPUT_CONTROLS);
		userName.setSmooth(true);
		userName.textProperty().bind(user.userNameProperty());
		
		return userName;
	}

	void addUsernameInputToGridPane(
			GridPane gridpane, 
			Text userName) {
		TextFlow textFlow = new TextFlow(userName);
		GridPane.setHalignment(textFlow, HPos.LEFT);
		gridpane.add(textFlow, 1, 0);
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
				+ "-fx-background-color: rgba(255, 255, 255, .80); ");
		passwordField.prefWidthProperty().bind(stage.widthProperty().subtract(55));
		Tooltip passwordTT = new Tooltip(
				"Heyo! Press Escape key to quit. " + 
				"Fake password is '" + TEST_PASSWORD + "'.");
		SVGPath passwordTtIcon = new SVGPath();
		passwordTtIcon.setFill(FOREGROUND_COLOR_FOR_INPUT_CONTROLS);
		passwordTtIcon.setContent("M16,1.466C7.973,1.466,1.466,7.973,1.466,16c0,8.027,6.507,14.534,14.534,14.534c8.027,0,14.534-6.507,14.534-14.534C30.534,7.973,24.027,1.466,16,1.466z M14.757,8h2.42v2.574h-2.42V8z M18.762,23.622H16.1c-1.034,0-1.475-0.44-1.475-1.496v-6.865c0-0.33-0.176-0.484-0.484-0.484h-0.88V12.4h2.662c1.035,0,1.474,0.462,1.474,1.496v6.887c0,0.309,0.176,0.484,0.484,0.484h0.88V23.622z");
		passwordTT.setGraphic(passwordTtIcon);
		passwordField.setTooltip(passwordTT);
		//---------------------------------------------------------------
		
		user.passwordProperty().bind(passwordField.textProperty());
		
		passwordField.setBackground(new Background(new BackgroundFill(Color.LAWNGREEN, null, null)));
		
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
		
		Group root = new Group();
		Scene scene = new Scene(
				root, SCENE_WIDTH, SCENE_HEIGHT, Color.rgb(0, 0, 0, 0));
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
		
		// TODO Replace Text with TextField
		Text userName = createUsernameText();
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

		VBox formLayout = new VBox(10);
		formLayout.getChildren().addAll(imageView, stackPane);
		
		root.getChildren().addAll(formLayout);

		stage.show();

	}
	
}
