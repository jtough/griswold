package com.jimtough.griswold;

import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jimtough.griswold.beans.Person;

public class PersonDetailsController {

	private static final Logger logger =
			LoggerFactory.getLogger(PersonDetailsController.class);

	private final Stage stage;
	private final NavigationController navController;
	
	public PersonDetailsController(
			NavigationController navController) {
		if (navController == null) {
			throw new IllegalArgumentException("navController cannot be null");
		}
		this.stage = new Stage();
		this.navController = navController;
	}

	public void onClose() {
		logger.info("onClose() | INVOKED");
		this.navController.authenticationSuccessful();
		this.stage.close();
	}

	public void createPersonDetailsModalDialogScene(
			final Stage parent,
			final Person person) {

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
							logger.info("Escape key pressed - closing dialog");
							onClose();
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

		
		// TODO add useful content here...
		
		
		root.getChildren().addAll(background);
		stage.show();
	}
	
}
