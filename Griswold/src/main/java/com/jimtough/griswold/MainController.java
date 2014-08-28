package com.jimtough.griswold;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.Window;
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
	
	private static final String APPLICATION_TITLE = "Griswold";
	
	private final Stage primaryStage;
	private final NavigationController navController;
	
	public MainController(
			Stage primaryStage,
			NavigationController navController) {
		if (primaryStage == null) {
			throw new IllegalArgumentException("primaryStage cannot be null");
		}
		if (navController == null) {
			throw new IllegalArgumentException("navController cannot be null");
		}
		this.primaryStage = primaryStage;
		this.navController = navController;
	}
	
	//private void createAndAddMenu(BorderPane root) {
	private void createAndAddMenu(
			Window window,
			BorderPane container,
			NavigationController navController) {
		
		MenuBar menuBar = new MenuBar();
		menuBar.prefWidthProperty().bind(window.widthProperty());
		container.setTop(menuBar); 

		// File menu - new, save, exit
		Menu fileMenu = new Menu("File");
		MenuItem newMenuItem = new MenuItem("New");
		MenuItem saveMenuItem = new MenuItem("Save");
		MenuItem exitMenuItem = new MenuItem("E_xit");
		exitMenuItem.setMnemonicParsing(true);
		exitMenuItem.setAccelerator(new KeyCodeCombination(
				KeyCode.X, KeyCombination.SHORTCUT_DOWN));
		
		exitMenuItem.setOnAction(
				//actionEvent -> Platform.exit() 
				actionEvent -> navController.exitApplication()
				);

		fileMenu.getItems().addAll(newMenuItem, 
				saveMenuItem, 
				new SeparatorMenuItem(), 
				exitMenuItem
				);

		// Cameras menu - camera 1, camera 2
		Menu cameraMenu = new Menu("Cameras");
		CheckMenuItem cam1MenuItem = new CheckMenuItem("Show Camera 1");
		cam1MenuItem.setSelected(true);
		cameraMenu.getItems().add(cam1MenuItem);

		CheckMenuItem cam2MenuItem = new CheckMenuItem("Show Camera 2");
		cam2MenuItem.setSelected(true);
		cameraMenu.getItems().add(cam2MenuItem);

		// Alarm menu
		Menu alarmMenu = new Menu("Alarm");

		// sound or turn alarm off
		ToggleGroup tGroup = new ToggleGroup();
		RadioMenuItem soundAlarmItem = new RadioMenuItem("Sound Alarm"); 
		soundAlarmItem.setToggleGroup(tGroup);

		RadioMenuItem stopAlarmItem = new RadioMenuItem("Alarm Off");
		stopAlarmItem.setToggleGroup(tGroup);
		stopAlarmItem.setSelected(true);

		alarmMenu.getItems().addAll(
				soundAlarmItem, 
				stopAlarmItem, 
				new SeparatorMenuItem());

		Menu contingencyPlans = new Menu("Contingent Plans");
		contingencyPlans.getItems().addAll(
				new CheckMenuItem("Self Destruct in T minus 50"),
				new CheckMenuItem("Turn off the coffee machine "),
				new CheckMenuItem("Run for your lives! "));

		alarmMenu.getItems().add(contingencyPlans);

		menuBar.getMenus().addAll(fileMenu, cameraMenu, alarmMenu);
	}

	private void setSillyProperties(Node node) {
		node.setOpacity(0.5);
		node.setEffect(new GaussianBlur());
	}
	
	public void createMainStageScene() {
		Group root = new Group();
		Scene scene = new Scene(root, 300, 250);
		scene.setFill(new Color(0, 0.25, 0.25, 0.5));

		// Add BorderPane as the first child of the control Group for Scene
		BorderPane borderPane = new BorderPane();
		root.getChildren().add(borderPane);
		createAndAddMenu(primaryStage, borderPane, navController);
		
		HBox hbox = new HBox(5);         // pixels space between child nodes
		hbox.setPadding(new Insets(1));  // padding between child nodes only
		Rectangle r1 = new Rectangle(10, 10);
		Rectangle r2 = new Rectangle(20, 20);
		Rectangle r3 = new Rectangle(5, 20);
		Rectangle r4 = new Rectangle(20, 5);
		
		HBox.setMargin(r1, new Insets(2,2,2,2));
		
		hbox.getChildren().addAll(r1, r2, r3, r4);

		// Display the HBox in the center portion of the BorderPane
		borderPane.setCenter(hbox);
		//root.getChildren().add(hbox);
		
		// Play with some effects and stuff
		setSillyProperties(borderPane.getCenter());
		setSillyProperties(hbox);

		
		primaryStage.setWidth(800);
		primaryStage.setHeight(600);
		primaryStage.setOnShown((WindowEvent we) -> {
			logger.info("hbox width  " + hbox.getBoundsInParent().getWidth());
			logger.info("hbox height " + hbox.getBoundsInParent().getHeight());
		});
		primaryStage.setTitle(APPLICATION_TITLE);
		primaryStage.setScene(scene);
		//primaryStage.show();
	}
	
}
