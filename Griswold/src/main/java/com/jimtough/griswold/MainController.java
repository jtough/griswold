package com.jimtough.griswold;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Controller for the primary Stage (the main application window).
 * Since I here am new, I don't know the best way to structure a
 * JavaFX application. Right now I'm just playing around with features
 * of the framework to see what it can do.
 * 
 * DO NOT use this code as an example of proper code structure.
 * 
 * @author JTOUGH
 */
public class MainController {

	private static final Logger logger =
			LoggerFactory.getLogger(MainController.class);
	
	private final Stage primaryStage;
	
	public MainController(Stage primaryStage) {
		if (primaryStage == null) {
			throw new IllegalArgumentException("primaryStage cannot be null");
		}
		this.primaryStage = primaryStage;
	}
	
	public void createMainStageScene() {
		Group root = new Group();
		Scene scene = new Scene(root, 300, 250);
		scene.setFill(new Color(0, 0.25, 0.25, 0.5));
		
		root.setOpacity(0.5);
		root.setEffect(new GaussianBlur());
		
		HBox hbox = new HBox(5);         // pixels space between child nodes
		hbox.setPadding(new Insets(1));  // padding between child nodes only
		Rectangle r1 = new Rectangle(10, 10);
		Rectangle r2 = new Rectangle(20, 20);
		Rectangle r3 = new Rectangle(5, 20);
		Rectangle r4 = new Rectangle(20, 5);
		
		HBox.setMargin(r1, new Insets(2,2,2,2));
		
		hbox.getChildren().addAll(r1, r2, r3, r4);
		
		root.getChildren().add(hbox);
		
		primaryStage.setWidth(800);
		primaryStage.setHeight(600);
		primaryStage.setOnShown((WindowEvent we) -> {
			logger.info("hbox width  " + hbox.getBoundsInParent().getWidth());
			logger.info("hbox height " + hbox.getBoundsInParent().getHeight());
		});
		primaryStage.setTitle("HBox Example");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
}
