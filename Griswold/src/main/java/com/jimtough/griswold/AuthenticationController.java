package com.jimtough.griswold;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class AuthenticationController {

	private static final Logger logger =
			LoggerFactory.getLogger(AuthenticationController.class);
	
	private final static String MY_PASS = "xxx";
	private final static BooleanProperty GRANTED_ACCESS = new SimpleBooleanProperty(false);
	private final static int MAX_ATTEMPTS = 3;
	private final IntegerProperty ATTEMPTS = new SimpleIntegerProperty(0);
	
	private final Stage stage;
	private final NavigationController navController;
	
	public AuthenticationController(
			NavigationController navController) {
		if (navController == null) {
			throw new IllegalArgumentException("navController cannot be null");
		}
		this.navController = navController;
		this.stage = new Stage();
	}

	public void onSuccessfulAuthentication() {
		logger.info("onSuccessfulAuthentication() | INVOKED");
		this.navController.authenticationSuccessful();
		this.stage.close();
	}
	
	public void createAuthenticationDialogScene(Stage parent) {
		
		// create a model representing a user
		User user = new User();

		// create a transparent stage
		stage.initStyle(StageStyle.TRANSPARENT);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.initOwner(parent);
		
		Group root = new Group();
		Scene scene = new Scene(root, 320, 112, Color.rgb(0, 0, 0, 0));
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

		// all text, borders, svg paths will use white
		Color foregroundColor = Color.rgb(255, 255, 255, .9);

		// rounded rectangular background 
		Rectangle background = new Rectangle(320, 112);
		background.setX(0);
		background.setY(0);
		background.setArcHeight(15);
		background.setArcWidth(15);
		background.setFill(Color.rgb(0, 0, 0, .55));
		background.setStrokeWidth(1.5);
		background.setStroke(foregroundColor);

		// a read only field holding the user name.
		Text userName = new Text();
		userName.setFont(Font.font("SanSerif", FontWeight.BOLD, 30));
		userName.setFill(foregroundColor);
		userName.setSmooth(true);
		userName.textProperty().bind(user.userNameProperty());

		// wrap text node
		HBox userNameCell = new HBox();
		userNameCell.prefWidthProperty().bind(stage.widthProperty().subtract(45));
		userNameCell.getChildren().add(userName);

		// pad lock 
		SVGPath padLock = new SVGPath();
		padLock.setFill(foregroundColor);
		padLock.setContent("M24.875,15.334v-4.876c0-4.894-3.981-8.875-8.875-8.875s-8.875,3.981-8.875,8.875v4.876H5.042v15.083h21.916V15.334H24.875zM10.625,10.458c0-2.964,2.411-5.375,5.375-5.375s5.375,2.411,5.375,5.375v4.876h-10.75V10.458zM18.272,26.956h-4.545l1.222-3.667c-0.782-0.389-1.324-1.188-1.324-2.119c0-1.312,1.063-2.375,2.375-2.375s2.375,1.062,2.375,2.375c0,0.932-0.542,1.73-1.324,2.119L18.272,26.956z");

		// first row 
		HBox row1 = new HBox();
		row1.getChildren().addAll(userNameCell, padLock);

		//---------------------------------------------------------------
		// password text field 
		PasswordField passwordField = new PasswordField();
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
				"Fake password is '" + MY_PASS + "'.");
		SVGPath passwordTtIcon = new SVGPath();
		passwordTtIcon.setFill(foregroundColor);
		passwordTtIcon.setContent("M16,1.466C7.973,1.466,1.466,7.973,1.466,16c0,8.027,6.507,14.534,14.534,14.534c8.027,0,14.534-6.507,14.534-14.534C30.534,7.973,24.027,1.466,16,1.466z M14.757,8h2.42v2.574h-2.42V8z M18.762,23.622H16.1c-1.034,0-1.475-0.44-1.475-1.496v-6.865c0-0.33-0.176-0.484-0.484-0.484h-0.88V12.4h2.662c1.035,0,1.474,0.462,1.474,1.496v6.887c0,0.309,0.176,0.484,0.484,0.484h0.88V23.622z");
		passwordTT.setGraphic(passwordTtIcon);
		passwordField.setTooltip(passwordTT);
		//---------------------------------------------------------------
		
		user.passwordProperty().bind(passwordField.textProperty());

		// error icon 
		SVGPath deniedIcon = new SVGPath();
		deniedIcon.setFill(Color.rgb(255, 0, 0, .9));
		deniedIcon.setStroke(Color.WHITE);// 
		deniedIcon.setContent("M24.778,21.419 19.276,15.917 24.777,10.415 21.949,7.585 16.447,13.087 10.945,7.585 8.117,10.415 13.618,15.917 8.116,21.419 10.946,24.248 16.447,18.746 21.948,24.248z");
		deniedIcon.setVisible(false);

		SVGPath grantedIcon = new SVGPath();
		grantedIcon.setFill(Color.rgb(0, 255, 0, .9));
		grantedIcon.setStroke(Color.WHITE);// 
		grantedIcon.setContent("M2.379,14.729 5.208,11.899 12.958,19.648 25.877,6.733 28.707,9.561 12.958,25.308z");
		grantedIcon.setVisible(false);

		StackPane accessIndicator = new StackPane();
		accessIndicator.getChildren().addAll(deniedIcon, grantedIcon);
		accessIndicator.setAlignment(Pos.CENTER_RIGHT);

		grantedIcon.visibleProperty().bind(GRANTED_ACCESS);

		// second row
		HBox row2 = new HBox(3);
		row2.getChildren().addAll(passwordField, accessIndicator);
		HBox.setHgrow(accessIndicator, Priority.ALWAYS);

		// user hits the enter key
		passwordField.setOnAction(actionEvent -> {
			if (GRANTED_ACCESS.get()) {
				logger.info("User is granted access.");
				this.onSuccessfulAuthentication();
				//Platform.exit();
			} else {
				deniedIcon.setVisible(true); 
			}
			ATTEMPTS.set(ATTEMPTS.add(1).get());
			logger.info("Attempts: " + ATTEMPTS.get());
		});

		// listener when the user types into the password field
		passwordField.textProperty().addListener((obs, ov, nv) -> {
			boolean granted = passwordField.getText().equals(MY_PASS);
			GRANTED_ACCESS.set(granted);
			if (granted) {
				deniedIcon.setVisible(false);
			}
		});

		// listener on number of attempts
		ATTEMPTS.addListener((obs, ov, nv) -> {
			if (MAX_ATTEMPTS == nv.intValue()) {
				// failed attemps
				logger.info("User is denied access.");
				Platform.exit();
			}
		});

		VBox formLayout = new VBox(4);
		formLayout.getChildren().addAll(row1, row2);
		formLayout.setLayoutX(12);
		formLayout.setLayoutY(12);
		
		root.getChildren().addAll(background, formLayout);

		stage.show();

	}
	
}
