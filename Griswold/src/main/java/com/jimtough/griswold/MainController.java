package com.jimtough.griswold;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.animation.TranslateTransition;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

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

	private static final String SVG_INFO = "M16,1.466C7.973,1.466,1.466,7.973,1.466,16c0,8.027,6.507,14.534,14.534,14.534c8.027,0,14.534-6.507,14.534-14.534C30.534,7.973,24.027,1.466,16,1.466z M14.757,8h2.42v2.574h-2.42V8z M18.762,23.622H16.1c-1.034,0-1.475-0.44-1.475-1.496v-6.865c0-0.33-0.176-0.484-0.484-0.484h-0.88V12.4h2.662c1.035,0,1.474,0.462,1.474,1.496v6.887c0,0.309,0.176,0.484,0.484,0.484h0.88V23.622z";
	private static final String SVG_POWER = "M25.542,8.354c-1.47-1.766-2.896-2.617-3.025-2.695c-0.954-0.565-2.181-0.241-2.739,0.724c-0.556,0.961-0.24,2.194,0.705,2.763c0,0,0.001,0,0.002,0.001c0.001,0,0.002,0.001,0.003,0.002c0.001,0,0.003,0.001,0.004,0.001c0.102,0.062,1.124,0.729,2.08,1.925c1.003,1.261,1.933,3.017,1.937,5.438c-0.001,2.519-1.005,4.783-2.64,6.438c-1.637,1.652-3.877,2.668-6.368,2.669c-2.491-0.001-4.731-1.017-6.369-2.669c-1.635-1.654-2.639-3.919-2.64-6.438c0.005-2.499,0.995-4.292,2.035-5.558c0.517-0.625,1.043-1.098,1.425-1.401c0.191-0.152,0.346-0.263,0.445-0.329c0.049-0.034,0.085-0.058,0.104-0.069c0.005-0.004,0.009-0.006,0.012-0.008s0.004-0.002,0.004-0.002l0,0c0.946-0.567,1.262-1.802,0.705-2.763c-0.559-0.965-1.785-1.288-2.739-0.724c-0.128,0.079-1.555,0.93-3.024,2.696c-1.462,1.751-2.974,4.511-2.97,8.157C2.49,23.775,8.315,29.664,15.5,29.667c7.186-0.003,13.01-5.892,13.012-13.155C28.516,12.864,27.005,10.105,25.542,8.354zM15.5,17.523c1.105,0,2.002-0.907,2.002-2.023h-0.001V3.357c0-1.118-0.896-2.024-2.001-2.024s-2.002,0.906-2.002,2.024V15.5C13.498,16.616,14.395,17.523,15.5,17.523z";
	
	private static final String APPLICATION_TITLE = "Griswold";
	private static final int TICKER_HEIGHT = 30;
	
	private final Stage primaryStage;
	private final NavigationController navController;
	
	private Group rootNode = null;
	private Scene scene = null;
	private BorderPane borderPane = null;
	private MenuBar menuBar = null;
	private Group tickerArea = null;
	
	/**
	 * Constructor
	 * @param primaryStage Non-null
	 * @param navController Non-null
	 */
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

	Group createRootNode() {
		this.rootNode = new Group();
		return this.rootNode;
	}

	Scene createScene(final Group rootNode) {
		this.scene = new Scene(rootNode, 300, 250);
		this.scene.setFill(new Color(0, 0.25, 0.25, 0.5));
		return this.scene;
	}

	BorderPane createBorderPane() {
		this.borderPane = new BorderPane();
		return this.borderPane;
	}
	
	MenuBar createMenuBar(final NavigationController navController) {
		this.menuBar = new MenuBar();

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
		return menuBar;
	}

	private Button createToolbarButton(
			String iconSVGString,
			String tooltipText) {
		Button b = new Button();
		b.setMinHeight(50);
		b.setMinWidth(75);
		b.setMaxHeight(50);
		b.setMaxWidth(75);
		b.setBorder(new Border(
				new BorderStroke(Color.LIGHTGREY, BorderStrokeStyle.SOLID, null, null)));
		b.setBackground(new Background(
				new BackgroundFill(Color.ALICEBLUE, null, null)));
		Tooltip tt = new Tooltip(tooltipText);
		b.setTooltip(tt);
		SVGPath icon = new SVGPath();
		icon.setFill(Color.GREEN);
		icon.setContent(iconSVGString);
		b.setGraphic(icon);
		return b;
	}
	
	Node createToolbarContent(final NavigationController navController) {
		VBox vBox = new VBox();

		Button b1 = createToolbarButton(
				SVG_INFO, "button 1");

		Button b2 = createToolbarButton(
				SVG_INFO, "button 2");

		Button bExit = createToolbarButton(
				SVG_POWER, "Close application");
		bExit.setOnAction(actionEvent -> navController.exitApplication());
		
		vBox.getChildren().add(b1);
		vBox.getChildren().add(b2);
		vBox.getChildren().add(bExit);

		return vBox;
	}

	Rectangle createPlaceholderCenterRectangle() {
		Rectangle rect = new Rectangle();
		rect.widthProperty().bind(this.scene.widthProperty());
		rect.heightProperty().bind(
				this.scene.heightProperty()
					.subtract(this.menuBar.getHeight())
					.subtract(TICKER_HEIGHT));
		return rect;
	}
	
	HBox createHBox() {
		HBox hbox = new HBox(5);         // pixels space between child nodes
		hbox.setPadding(new Insets(1));  // padding between child nodes only
		Rectangle r1 = new Rectangle(10, 10);
		Rectangle r2 = new Rectangle(20, 20);
		Rectangle r3 = new Rectangle(5, 20);
		Rectangle r4 = new Rectangle(20, 5);
		
		HBox.setMargin(r1, new Insets(2,2,2,2));
		
		hbox.getChildren().addAll(r1, r2, r3, r4);
		
		hbox.prefHeightProperty().bind(
				this.scene.heightProperty()
				.subtract(this.menuBar.heightProperty())
				.subtract(TICKER_HEIGHT));
		
		BackgroundFill bgFill = new BackgroundFill(Color.CORNFLOWERBLUE, null, null);
		Background bg = new Background(bgFill);
		hbox.setBackground(bg);
		return hbox;
	}
	
	private Group createTickerAreaGroup(
//			final Stage stage, 
//			final double rightPadding) {
			final Stage stage) {
//		Scene scene = stage.getScene();

		// create ticker area
		this.tickerArea = new Group();
		Rectangle tickerRect = new Rectangle(scene.getWidth(), TICKER_HEIGHT);
		tickerRect.getStyleClass().add("ticker-border");

		Rectangle clipRegion = new Rectangle(scene.getWidth(), TICKER_HEIGHT);
		clipRegion.getStyleClass().add("ticker-clip-region");
		this.tickerArea.setClip(clipRegion);

		// Resize the ticker area when the window is resized
		//this.tickerArea.setTranslateX(6);
		//this.tickerArea.translateYProperty().bind(
		//		scene.heightProperty().subtract(tickerRect.getHeight() + 6));
		//tickerRect.widthProperty().bind(
		//		scene.widthProperty().subtract(rightPadding));
		//clipRegion.widthProperty().bind(
		//		scene.widthProperty().subtract(rightPadding));
		//this.tickerArea.getChildren().add(tickerRect);
		tickerRect.widthProperty().bind(scene.widthProperty());
		clipRegion.widthProperty().bind(scene.widthProperty());
		this.tickerArea.getChildren().add(tickerRect);
		
		// news feed container
		FlowPane tickerContent = new FlowPane();

		// add some news
		Text news = new Text();
		news.setText("JavaFX 8.0 News! | 85 and sunny | :)");
		news.setFill(Color.WHITE);
		tickerContent.getChildren().add(news);

		DoubleProperty centerContentY = new SimpleDoubleProperty();
		centerContentY.bind(clipRegion
				.heightProperty()
				.divide(2)
				.subtract(tickerContent.heightProperty().divide(2)));

		tickerContent.translateYProperty().bind(centerContentY);

		this.tickerArea.getChildren().add(tickerContent);

		// scroll news feed 
		TranslateTransition tickerScroller = new TranslateTransition();
		tickerScroller.setNode(tickerContent);
		tickerScroller.setDuration(
				Duration.millis(scene.getWidth() * 40));
		tickerScroller.fromXProperty().bind(scene.widthProperty());
		tickerScroller.toXProperty().bind(
				tickerContent.widthProperty().negate());

		// when ticker has finished reset and replay ticker animation
		tickerScroller.setOnFinished((ActionEvent ae) -> {
			tickerScroller.stop();
			tickerScroller.setDuration(
					Duration.millis(scene.getWidth() * 40));
			tickerScroller.playFromStart();
		});
		// start ticker after nodes are shown.
		stage.setOnShown( windowEvent -> {
			logger.info("stage.setOnShown() | INVOKED");
			tickerScroller.play();
		});

		this.borderPane.setBottom(this.tickerArea);
		
		return this.tickerArea;
	}
	
	private void setSillyProperties(Node node) {
		node.setOpacity(0.5);
		node.setEffect(new GaussianBlur());
	}
	
	
	
	public Scene createMainStageScene() {
		primaryStage.setWidth(800);
		primaryStage.setHeight(600);
		
		createRootNode();
		createScene(this.rootNode);
		createBorderPane();
		
		// Add BorderPane as the first child of the control Group for Scene
		//BorderPane borderPane = new BorderPane();
		this.rootNode.getChildren().add(borderPane);

		// Create a MenuBar and place it in the top border of the BorderPane
		//createAndAddMenu(primaryStage, borderPane, navController);
		createMenuBar(this.navController);
		this.menuBar.prefWidthProperty().bind(this.primaryStage.widthProperty());
		this.borderPane.setTop(this.menuBar);

		Node leftContent = createToolbarContent(navController);
		this.borderPane.setLeft(leftContent);
		
		// Create a dummy node to display in the center of the BorderPane
		//Rectangle rect = createPlaceholderCenterRectangle();
		//this.borderPane.setCenter(rect);
		HBox hbox = createHBox();
		borderPane.setCenter(hbox);

		
		createTickerAreaGroup(primaryStage);
		
		// Play with some effects and stuff
//		setSillyProperties(borderPane.getCenter());
//		setSillyProperties(hbox);

		//primaryStage.setWidth(800);
		//primaryStage.setHeight(600);
		primaryStage.setOnShown((WindowEvent we) -> {
			logger.info("hbox width  " + hbox.getBoundsInParent().getWidth());
			logger.info("hbox height " + hbox.getBoundsInParent().getHeight());
			//logger.info("rect width  " + rect.getBoundsInParent().getWidth());
			//logger.info("rect height " + rect.getBoundsInParent().getHeight());
		});
		primaryStage.setTitle(APPLICATION_TITLE);
		primaryStage.setScene(scene);
		//primaryStage.show();
		
		return scene;
	}
	
}
