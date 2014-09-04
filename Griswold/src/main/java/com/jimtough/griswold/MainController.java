package com.jimtough.griswold;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jimtough.griswold.beans.AppAlphaStatus;
import com.jimtough.griswold.beans.Person;

import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
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
	private static final String SVG_DOUBLE_QUOTE = "M14.505,5.873c-3.937,2.52-5.904,5.556-5.904,9.108c0,1.104,0.192,1.656,0.576,1.656l0.396-0.107c0.312-0.12,0.563-0.18,0.756-0.18c1.128,0,2.07,0.411,2.826,1.229c0.756,0.82,1.134,1.832,1.134,3.037c0,1.157-0.408,2.14-1.224,2.947c-0.816,0.807-1.801,1.211-2.952,1.211c-1.608,0-2.935-0.661-3.979-1.984c-1.044-1.321-1.565-2.98-1.565-4.977c0-2.259,0.443-4.327,1.332-6.203c0.888-1.875,2.243-3.57,4.067-5.085c1.824-1.514,2.988-2.272,3.492-2.272c0.336,0,0.612,0.162,0.828,0.486c0.216,0.324,0.324,0.606,0.324,0.846L14.505,5.873zM27.465,5.873c-3.937,2.52-5.904,5.556-5.904,9.108c0,1.104,0.192,1.656,0.576,1.656l0.396-0.107c0.312-0.12,0.563-0.18,0.756-0.18c1.104,0,2.04,0.411,2.808,1.229c0.769,0.82,1.152,1.832,1.152,3.037c0,1.157-0.408,2.14-1.224,2.947c-0.816,0.807-1.801,1.211-2.952,1.211c-1.608,0-2.935-0.661-3.979-1.984c-1.044-1.321-1.565-2.98-1.565-4.977c0-2.284,0.449-4.369,1.35-6.256c0.9-1.887,2.256-3.577,4.068-5.067c1.812-1.49,2.97-2.236,3.474-2.236c0.336,0,0.612,0.162,0.828,0.486c0.216,0.324,0.324,0.606,0.324,0.846L27.465,5.873z";
	
	private static final String APPLICATION_TITLE = "Griswold";
	private static final int NOTIFICATION_AREA_HEIGHT = 50;
	
	private static final int NOTIFICATION_AUTOCYCLE_MILLISECONDS = 20000;
	
	private final Stage primaryStage;
	private final NavigationController navController;
	private final MovieQuoteCycler movieQuoteCycler;
	private final ReadOnlyStringWrapper notificationAreaTextString;
	
	private Scene scene = null;
	private Group rootNode = null;
	private VBox sceneFrame = null;
	private HBox frameMiddleRegion = null;
	private MenuBar menuBar = null;
	private VBox toolbar = null;
	private HBox notificationArea = null;

	private ObservableList<Person> observablePersonList;
	private ObservableList<AppAlphaStatus> observableAppAlphaStatusList;
	
	/**
	 * Constructor
	 * @param primaryStage Non-null
	 * @param navController Non-null
	 * @throws IOException 
	 */
	public MainController(
			Stage primaryStage,
			NavigationController navController) 
			throws IOException {
		if (primaryStage == null) {
			throw new IllegalArgumentException("primaryStage cannot be null");
		}
		if (navController == null) {
			throw new IllegalArgumentException("navController cannot be null");
		}
		this.primaryStage = primaryStage;
		this.navController = navController;
		this.notificationAreaTextString = new ReadOnlyStringWrapper();
		this.movieQuoteCycler = new MovieQuoteCycler(
				this.notificationAreaTextString);
	}

	Group createRootNode() {
		this.rootNode = new Group();
		return this.rootNode;
	}

	Scene createScene(final Group rootNode) {
		this.scene = new Scene(rootNode, 300, 250);
		this.scene.setFill(Color.LIGHTGREY);
		return this.scene;
	}
	
	ObservableList<Person> createObservablePersonList() {
		observablePersonList = FXCollections.observableArrayList();
		
		List<Person> samplePersonList = new ArrayList<Person>();
		samplePersonList.add(new Person("Jim", "James", "Tough", "jim@jimtough.com"));
		samplePersonList.add(new Person("The Duke", "John", "Wayne", null));
		samplePersonList.add(new Person("Ol' Pudgy", "Stephen", "Harper", null));
		samplePersonList.add(new Person("L'il Cheech", "Justin", "Trudeau", null));
		
		observablePersonList.clear();
		observablePersonList.setAll(samplePersonList);
		
		return observablePersonList;
	}
	
	ObservableList<AppAlphaStatus> createObservableAppAlphaStatusList() {
		observableAppAlphaStatusList = FXCollections.observableArrayList();
		
		List<AppAlphaStatus> sampleAppAlphaStatusList = new ArrayList<AppAlphaStatus>();
		for (int i=0; i<100; i++) {
			AppAlphaStatus aas = new AppAlphaStatus("svr-" + i + ".jimtough.com");
			aas.setLastUpdated(new Date());
			aas.setStatusString("Coolio");
			sampleAppAlphaStatusList.add(aas);
		}
		
		observableAppAlphaStatusList.clear();
		observableAppAlphaStatusList.setAll(sampleAppAlphaStatusList);
		
		return observableAppAlphaStatusList;
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

		this.menuBar.getMenus().addAll(fileMenu, cameraMenu, alarmMenu);
		this.menuBar.prefWidthProperty().bind(this.primaryStage.widthProperty());
		return this.menuBar;
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
		
		b.setOnMouseEntered(mouseEvent -> {
			Bloom bloom = new Bloom();
			bloom.setThreshold(0.3);
			((Button)mouseEvent.getSource()).setEffect(bloom);
		});
		b.setOnMouseExited(mouseEvent -> {
			((Button)mouseEvent.getSource()).setEffect(null);
		});
		
		
		return b;
	}
	
	VBox createToolbarContent(final NavigationController navController) {
		VBox vBox = new VBox();

		Button b1 = createToolbarButton(
				SVG_INFO, "button 1");

		Button b2 = createToolbarButton(
				SVG_DOUBLE_QUOTE, "Cycle to next movie quote");
		b2.setOnAction(actionEvent -> {
				cycleToNextNotificationMessage();
			});

		Button bExit = createToolbarButton(
				SVG_POWER, "Close application");
		bExit.setOnAction(actionEvent -> navController.exitApplication());
		
		vBox.getChildren().add(b1);
		vBox.getChildren().add(b2);
		vBox.getChildren().add(bExit);

		this.toolbar = vBox;
		toolbar.heightProperty().addListener(
				(observable, oldValue, newValue) -> {
					logger.trace("toolbar height has changed: " +
							" | old: " + oldValue +
							" | new: " + newValue);
				});
		
		return vBox;
	}

	HBox createNotificationArea() {
		this.notificationArea = new HBox(5);
		this.notificationArea.prefWidthProperty().bind(this.primaryStage.widthProperty());
		this.notificationArea.prefHeight(NOTIFICATION_AREA_HEIGHT);
		this.notificationArea.setMinHeight(NOTIFICATION_AREA_HEIGHT);
		this.notificationArea.setMaxHeight(NOTIFICATION_AREA_HEIGHT);
		
		SVGPath icon = new SVGPath();
		icon.setContent(SVG_INFO);
		
		DropShadow dropShadow = new DropShadow();
		dropShadow.setRadius(5.0);
		dropShadow.setOffsetX(3.0);
		dropShadow.setOffsetY(3.0);
		dropShadow.setColor(Color.color(0.4, 0.5, 0.5));		
		icon.setEffect(dropShadow);
		icon.setFill(Color.BLUE);
		icon.setOpacity(1.0);
		
		Text text = new Text();
		text.textProperty().bind(this.notificationAreaTextString);
		TextFlow textFlow = new TextFlow(text);
		
		this.notificationArea.getChildren().addAll(icon, textFlow);
		
		return this.notificationArea;
	}

	HBox createFrameMiddleRegion() {
		this.frameMiddleRegion = new HBox();
		this.frameMiddleRegion.setBackground(new Background(new BackgroundFill(Color.CYAN, null, null)));
		
		this.frameMiddleRegion.prefWidthProperty().bind(
				this.sceneFrame.widthProperty());
		this.frameMiddleRegion.prefHeightProperty().bind(
				this.sceneFrame.heightProperty()
					.subtract(this.menuBar.heightProperty())
					.subtract(this.notificationArea.heightProperty()));
		this.frameMiddleRegion.maxHeightProperty().bind(
				this.sceneFrame.heightProperty()
					.subtract(this.menuBar.heightProperty())
					.subtract(this.notificationArea.heightProperty()));
		this.frameMiddleRegion.heightProperty().addListener(
				(observable, oldValue, newValue) -> {
					logger.info("frameMiddleRegion height has changed: " +
							" | old: " + oldValue +
							" | new: " + newValue);
				});
		
		return this.frameMiddleRegion;
	}
	
	public synchronized void cycleToNextNotificationMessage() {
		logger.info("cycleToNextNotificationMessage() | INVOKED");
		SequentialTransition sequentialTransition = 
				this.transitionByFading(this.notificationArea);
		sequentialTransition.play();
	}
	
	private SequentialTransition transitionByFading(Node node) {
		FadeTransition fadeOut = 
				new FadeTransition(Duration.millis(3000), node);
		fadeOut.setFromValue(1.0);
		fadeOut.setToValue(0.0);
		
		fadeOut.setOnFinished(
				actionEvent -> this.movieQuoteCycler.cycleToNextQuote());
		
		FadeTransition fadeIn = 
				new FadeTransition(Duration.millis(1000), node);
		fadeIn.setFromValue(0.0);
		fadeIn.setToValue(1.0);
		
		SequentialTransition seqTransition = 
				new SequentialTransition(fadeOut, fadeIn);
		return seqTransition;
	}

	TableView<Person> createTableView(
			final ObservableList<Person> observablePersonList) {
		TableView<Person> tv = new TableView<Person>();
		tv.prefWidthProperty().bind(
				this.scene.widthProperty()
				.subtract(this.menuBar.widthProperty()));
		tv.prefHeightProperty().bind(
				this.scene.heightProperty()
				.subtract(this.menuBar.heightProperty())
				.subtract(NOTIFICATION_AREA_HEIGHT));
		
		//final ObservableList<Person> teamMembers = FXCollections.observableArrayList();
		tv.setItems(observablePersonList);
		
		tv.setOnMouseClicked(event -> {
			if (event.getButton().equals(MouseButton.PRIMARY)) {
				if (event.getClickCount() > 1) {
					logger.info("Mouse click count: " + event.getClickCount());
					Person selectedPerson = 
							tv.getSelectionModel().getSelectedItem();
					PersonDetailsController pdc = 
							new PersonDetailsController(navController);
					pdc.createPersonDetailsModalDialogScene(primaryStage, selectedPerson);
				}
			}
		});
		
		TableColumn<Person, String> aliasNameCol = new TableColumn<>("Alias");
		aliasNameCol.setEditable(true);
		aliasNameCol.setCellValueFactory(new PropertyValueFactory<Person,String>("aliasName"));
		
		aliasNameCol.setPrefWidth(tv.getPrefWidth() / 4);
		
		TableColumn<Person, String> firstNameCol = new TableColumn<>("First Name");
		firstNameCol.setCellValueFactory(new PropertyValueFactory<Person,String>("firstName"));
		firstNameCol.setPrefWidth(tv.getPrefWidth() / 4);
		
		TableColumn<Person, String> lastNameCol = new TableColumn<>("Last Name");
		lastNameCol.setCellValueFactory(new PropertyValueFactory<Person,String>("lastName"));
		lastNameCol.setPrefWidth(tv.getPrefWidth() / 4);
		
		TableColumn<Person, String> emailCol = new TableColumn<>("Email");
		emailCol.setCellValueFactory(new PropertyValueFactory<Person,String>("email"));
		emailCol.setPrefWidth(tv.getPrefWidth() / 4);

		List<TableColumn<Person,String>> tableColumnList = new ArrayList<>();
		tableColumnList.add(aliasNameCol);
		tableColumnList.add(firstNameCol);
		tableColumnList.add(lastNameCol);
		tableColumnList.add(emailCol);
		
		tv.getColumns().setAll(tableColumnList);
		
		// selection listening
		tv.getSelectionModel().selectedItemProperty().addListener(
				(ObservableValue<? extends Person> observable, Person oldValue, Person newValue) -> {
			if (observable != null && observable.getValue() != null) {
				logger.info("New item selected: " +
						" | old: " + oldValue +
						" | new: " + newValue);
			}
		});
		
		return tv;
	}

	TableView<AppAlphaStatus> createAppAlphaStatusTableView(
			final ObservableList<AppAlphaStatus> observableAppAlphaStatusList) {
		TableView<AppAlphaStatus> tv = new TableView<AppAlphaStatus>();
		
		tv.setItems(observableAppAlphaStatusList);
		
		tv.setOnMouseClicked(event -> {
			if (event.getButton().equals(MouseButton.PRIMARY)) {
				if (event.getClickCount() > 1) {
					logger.info("Mouse click count: " + event.getClickCount());
					AppAlphaStatus status =
							tv.getSelectionModel().getSelectedItem();
					// TODO Do something with the selected item
					logger.info("Double-clicked on: " + status);
					//PersonDetailsController pdc = 
					//		new PersonDetailsController(navController);
					//pdc.createPersonDetailsModalDialogScene(primaryStage, selectedPerson);
				}
			}
		});
		
		TableColumn<AppAlphaStatus, String> hostnameCol = new TableColumn<>("Host");
		hostnameCol.setEditable(false);
		hostnameCol.setCellValueFactory(new PropertyValueFactory<AppAlphaStatus,String>("hostname"));
		
		TableColumn<AppAlphaStatus, String> statusStringCol = new TableColumn<>("Status");
		statusStringCol.setEditable(false);
		statusStringCol.setCellValueFactory(new PropertyValueFactory<AppAlphaStatus,String>("statusString"));
		
		List<TableColumn<AppAlphaStatus,String>> tableColumnList = new ArrayList<>();
		tableColumnList.add(hostnameCol);
		tableColumnList.add(statusStringCol);
		
		tv.getColumns().setAll(tableColumnList);
		
		// selection listening
		tv.getSelectionModel().selectedItemProperty().addListener(
				(ObservableValue<? extends AppAlphaStatus> observable, AppAlphaStatus oldValue, AppAlphaStatus newValue) -> {
			if (observable != null && observable.getValue() != null) {
				logger.info("New item selected: " +
						" | old: " + oldValue +
						" | new: " + newValue);
			}
		});
		
		return tv;
	}
	
	private void setSillyProperties(Node node) {
		node.setOpacity(0.5);
		node.setEffect(new GaussianBlur(5.5));
	}
	
	public Scene createMainStageScene() {
		primaryStage.setWidth(800);
		primaryStage.setHeight(600);
		
		createRootNode();
		createScene(this.rootNode);
		createMenuBar(this.navController);

		createNotificationArea();

		ScheduledService<Void> autoCycler =
				new ScheduledService<Void>() {
			@Override
			protected Task<Void> createTask() {
				Task<Void> task = new Task<Void>() {
					@Override
					protected Void call() throws Exception {
						Platform.runLater(
							new Runnable() {
								@Override
								public void run() {
									try {
										logger.info("cycling...");
										cycleToNextNotificationMessage();
									} catch (RuntimeException re) {
										logger.error("Exception in call()", re);
									} finally {}
								}
							});
						return null;
					}
				};
				return task;
			}
		};
		autoCycler.setDelay(new Duration(NOTIFICATION_AUTOCYCLE_MILLISECONDS));
		autoCycler.setPeriod(new Duration(NOTIFICATION_AUTOCYCLE_MILLISECONDS));

		this.notificationAreaTextString.addListener(
				(observable, oldValue, newValue) -> {
			logger.info("Current movie quote has changed from [" + oldValue +
					"] to [" + newValue + "]");
		});
		this.notificationArea.heightProperty().addListener(
				(observable, oldValue, newValue) -> {
					logger.info("notificationArea height has changed: " +
							" | old: " + oldValue +
							" | new: " + newValue);
				});
		
		createToolbarContent(navController);
		
		this.sceneFrame = new VBox();
		this.sceneFrame.setBackground(new Background(new BackgroundFill(Color.ORANGE, null, null)));
		this.rootNode.getChildren().add(sceneFrame);
		sceneFrame.prefHeightProperty().bind(scene.heightProperty());
		sceneFrame.prefWidthProperty().bind(scene.widthProperty());
		
		sceneFrame.getChildren().add(this.menuBar);
		createFrameMiddleRegion();
		this.frameMiddleRegion.getChildren().add(this.toolbar);

		// TODO Add controls that allow me to swap the content in the center of the panel
		//ObservableList<Person> opl = createObservablePersonList();
		//TableView<Person> tv = createTableView(opl);
		ObservableList<AppAlphaStatus> ol = createObservableAppAlphaStatusList();
		TableView<AppAlphaStatus> tv = createAppAlphaStatusTableView(ol);
		tv.prefWidthProperty().bind(frameMiddleRegion.widthProperty());
		this.frameMiddleRegion.getChildren().add(tv);
		
		sceneFrame.getChildren().add(this.frameMiddleRegion);
		sceneFrame.getChildren().add(this.notificationArea);
		
		// Play with some effects and stuff
		//setSillyProperties(borderPane.getCenter());
		//setSillyProperties(hbox);
		//setSillyProperties(rootNode);
		
		
		//primaryStage.setWidth(800);
		//primaryStage.setHeight(600);
		primaryStage.setOnShown((WindowEvent we) -> {
			logger.info("primaryStage.setOnShown() | INVOKED");
			//logger.info("hbox width  " + hbox.getBoundsInParent().getWidth());
			//logger.info("hbox height " + hbox.getBoundsInParent().getHeight());
			//logger.info("rect width  " + rect.getBoundsInParent().getWidth());
			//logger.info("rect height " + rect.getBoundsInParent().getHeight());
		});
		primaryStage.setTitle(APPLICATION_TITLE);
		primaryStage.setScene(scene);

		primaryStage.setOnShown(
			new EventHandler<WindowEvent>() {
				public void handle(final WindowEvent event) {
					logger.info("setOnShown()");
					autoCycler.start();
				}
			});
		primaryStage.setOnHiding(
			new EventHandler<WindowEvent>() {
				public void handle(final WindowEvent event) {
					logger.info("setOnHiding()");
					autoCycler.cancel();
				}
			});
		primaryStage.setOnCloseRequest(
			new EventHandler<WindowEvent>() {
				public void handle(final WindowEvent event) {
					logger.info("setOnCloseRequest()");
					autoCycler.cancel();
				}
			});
		
		//primaryStage.show();
		
		return scene;
	}
	
}
