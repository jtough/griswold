package com.jimtough.griswold;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jimtough.griswold.auth.AuthenticatedUser;
import com.jimtough.griswold.beans.AppAlphaStatus;
import com.jimtough.griswold.beans.AppBetaStatus;
import com.jimtough.griswold.beans.Person;
import com.jimtough.griswold.notification.AuthenticatedUserInfoMessageSource;
import com.jimtough.griswold.notification.CurrentTimeMessageSource;
import com.jimtough.griswold.notification.MovieQuotesMessageSource;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
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

	private static final String SVG_TOOLS = "M28.537,9.859c-0.473-0.259-1.127-0.252-1.609-0.523c-0.943-0.534-1.186-1.316-1.226-2.475c-2.059-2.215-5.138-4.176-9.424-4.114c-1.162,0.017-2.256-0.035-3.158,0.435c-0.258,0.354-0.004,0.516,0.288,0.599c-0.29,0.138-0.692,0.147-0.626,0.697c2.72-0.383,7.475,0.624,7.116,2.966c-0.08,0.521-0.735,1.076-1.179,1.563c-1.263,1.382-2.599,2.45-3.761,3.667l0.336,0.336c0.742-0.521,1.446-0.785,2.104-0.785c0.707,0,1.121,0.297,1.276,0.433c0.575-0.618,1.166-1.244,1.839-1.853c0.488-0.444,1.047-1.099,1.566-1.178l0.949-0.101c1.156,0.047,1.937,0.29,2.471,1.232c0.27,0.481,0.262,1.139,0.521,1.613c0.175,0.324,0.937,1.218,1.316,1.228c0.294,0.009,0.603-0.199,0.899-0.49l1.033-1.034c0.291-0.294,0.501-0.6,0.492-0.896C29.754,10.801,28.861,10.035,28.537,9.859zM13.021,15.353l-0.741-0.741c-3.139,2.643-6.52,5.738-9.531,8.589c-0.473,0.443-1.452,1.021-1.506,1.539c-0.083,0.781,0.95,1.465,1.506,2c0.556,0.533,1.212,1.602,1.994,1.51c0.509-0.043,1.095-1.029,1.544-1.502c2.255-2.374,4.664-4.976,6.883-7.509c-0.312-0.371-0.498-0.596-0.498-0.596C12.535,18.451,11.779,17.272,13.021,15.353zM20.64,15.643c-0.366-0.318-1.466,0.143-1.777-0.122c-0.311-0.266,0.171-1.259-0.061-1.455c-0.482-0.406-0.77-0.646-0.77-0.646s-0.862-0.829-2.812,0.928L7.44,6.569C7.045,6.173,7.203,4.746,7.203,4.746L3.517,2.646L2.623,3.541l2.1,3.686c0,0,1.428-0.158,1.824,0.237l7.792,7.793c-1.548,1.831-0.895,2.752-0.895,2.752s0.238,0.288,0.646,0.771c0.196,0.23,1.188-0.249,1.455,0.061c0.264,0.312-0.196,1.41,0.12,1.777c2.666,3.064,6.926,7.736,8.125,7.736c0.892,0,2.021-0.724,2.948-1.64c0.925-0.917,1.639-2.055,1.639-2.947C28.377,22.567,23.704,18.309,20.64,15.643z";
	private static final String SVG_CLOCK_FORWARD = "M17.001,15.5l-0.5-7.876c0-0.552-0.448-1-1-1c-0.552,0-1,0.448-1,1l-0.466,7.343l-3.004,1.96c-0.478,0.277-0.642,0.89-0.365,1.365c0.275,0.479,0.889,0.644,1.365,0.367l3.305-1.677C15.39,16.99,15.444,17,15.501,17C16.329,17,17.001,16.329,17.001,15.5zM18.939,21.455c-0.479,0.277-0.644,0.889-0.366,1.367c0.274,0.477,0.888,0.643,1.366,0.365c0.478-0.275,0.642-0.89,0.365-1.365C20.027,21.344,19.417,21.18,18.939,21.455zM19.938,7.813c-0.477-0.276-1.09-0.111-1.364,0.366c-0.275,0.48-0.111,1.091,0.366,1.367c0.477,0.276,1.088,0.112,1.365-0.366C20.581,8.702,20.418,8.089,19.938,7.813zM21.823,20.305c0.477,0.274,1.089,0.112,1.364-0.365c0.276-0.479,0.112-1.092-0.364-1.367c-0.48-0.275-1.093-0.111-1.367,0.365C21.182,19.416,21.344,20.029,21.823,20.305zM22.822,12.428c0.478-0.275,0.643-0.888,0.365-1.366c-0.274-0.478-0.89-0.642-1.365-0.366c-0.479,0.278-0.643,0.89-0.366,1.367S22.344,12.705,22.822,12.428zM24.378,15.5c0-0.551-0.448-1-1-1c-0.554,0.002-1.001,0.45-1.001,1c0.001,0.552,0.448,1,1.001,1C23.93,16.5,24.378,16.053,24.378,15.5zM9.546,12.062c0.275-0.478,0.111-1.089-0.366-1.366c-0.479-0.276-1.09-0.112-1.366,0.366s-0.111,1.09,0.365,1.366C8.658,12.704,9.269,12.541,9.546,12.062zM6.624,15.5c0,0.553,0.449,1,1,1c0.552,0,1-0.447,1.001-1c-0.001-0.552-0.448-0.999-1.001-1C7.071,14.5,6.624,14.948,6.624,15.5zM9.179,20.305c0.479-0.275,0.643-0.888,0.367-1.367c-0.276-0.477-0.888-0.641-1.367-0.365c-0.478,0.277-0.642,0.889-0.365,1.367C8.089,20.418,8.703,20.58,9.179,20.305zM12.062,9.545c0.479-0.276,0.642-0.888,0.366-1.366c-0.276-0.478-0.888-0.642-1.366-0.366s-0.642,0.888-0.366,1.366C10.973,9.658,11.584,9.822,12.062,9.545zM14.501,23.377c0,0.553,0.448,1,1,1c0.552,0,1-0.447,1-1s-0.448-1-1-1C14.949,22.377,14.501,22.824,14.501,23.377zM10.696,21.822c-0.275,0.479-0.111,1.09,0.366,1.365c0.478,0.276,1.091,0.11,1.365-0.365c0.277-0.479,0.113-1.09-0.365-1.367C11.584,21.18,10.973,21.344,10.696,21.822zM28.674,14.087l-3.27-1.186c0.291,1.105,0.41,2.274,0.309,3.478c-0.492,5.639-5.449,9.809-11.091,9.333c-5.639-0.495-9.809-5.45-9.333-11.09c0.494-5.641,5.449-9.812,11.089-9.335c2.428,0.212,4.567,1.266,6.194,2.833l-1.637,1.377l7.031,2.548l-1.309-7.364l-1.771,1.492c-2.133-2.151-4.996-3.597-8.25-3.877C9.346,1.67,2.926,7.072,2.297,14.364c-0.625,7.291,4.777,13.71,12.066,14.339c7.293,0.625,13.713-4.776,14.342-12.066C28.779,15.771,28.762,14.919,28.674,14.087z";
	private static final String SVG_LIGHTBULB_A = "M15.5,2.833c-3.866,0-7,3.134-7,7c0,3.859,3.945,4.937,4.223,9.499h5.553c0.278-4.562,4.224-5.639,4.224-9.499C22.5,5.968,19.366,2.833,15.5,2.833zM15.5,28.166c1.894,0,2.483-1.027,2.667-1.666h-5.334C13.017,27.139,13.606,28.166,15.5,28.166zM12.75,25.498h5.5v-5.164h-5.5V25.498z";
	private static final String SVG_LIGHTBULB_B = "M12.75,25.498h5.5v-5.164h-5.5V25.498zM15.5,28.166c1.894,0,2.483-1.027,2.667-1.666h-5.334C13.017,27.139,13.606,28.166,15.5,28.166zM15.5,2.833c-3.866,0-7,3.134-7,7c0,3.859,3.945,4.937,4.223,9.499h1.271c-0.009-0.025-0.024-0.049-0.029-0.078L11.965,8.256c-0.043-0.245,0.099-0.485,0.335-0.563c0.237-0.078,0.494,0.026,0.605,0.25l0.553,1.106l0.553-1.106c0.084-0.17,0.257-0.277,0.446-0.277c0.189,0,0.362,0.107,0.446,0.277l0.553,1.106l0.553-1.106c0.084-0.17,0.257-0.277,0.448-0.277c0.189,0,0.36,0.107,0.446,0.277l0.554,1.106l0.553-1.106c0.111-0.224,0.368-0.329,0.604-0.25s0.377,0.318,0.333,0.563l-1.999,10.998c-0.005,0.029-0.02,0.053-0.029,0.078h1.356c0.278-4.562,4.224-5.639,4.224-9.499C22.5,5.968,19.366,2.833,15.5,2.833zM17.458,10.666c-0.191,0-0.364-0.107-0.446-0.275l-0.554-1.106l-0.553,1.106c-0.086,0.168-0.257,0.275-0.446,0.275c-0.191,0-0.364-0.107-0.449-0.275l-0.553-1.106l-0.553,1.106c-0.084,0.168-0.257,0.275-0.446,0.275c-0.012,0-0.025,0-0.037-0.001l1.454,8.001h1.167l1.454-8.001C17.482,10.666,17.47,10.666,17.458,10.666z";
	private static final String SVG_INFO = "M16,1.466C7.973,1.466,1.466,7.973,1.466,16c0,8.027,6.507,14.534,14.534,14.534c8.027,0,14.534-6.507,14.534-14.534C30.534,7.973,24.027,1.466,16,1.466z M14.757,8h2.42v2.574h-2.42V8z M18.762,23.622H16.1c-1.034,0-1.475-0.44-1.475-1.496v-6.865c0-0.33-0.176-0.484-0.484-0.484h-0.88V12.4h2.662c1.035,0,1.474,0.462,1.474,1.496v6.887c0,0.309,0.176,0.484,0.484,0.484h0.88V23.622z";
	private static final String SVG_POWER = "M25.542,8.354c-1.47-1.766-2.896-2.617-3.025-2.695c-0.954-0.565-2.181-0.241-2.739,0.724c-0.556,0.961-0.24,2.194,0.705,2.763c0,0,0.001,0,0.002,0.001c0.001,0,0.002,0.001,0.003,0.002c0.001,0,0.003,0.001,0.004,0.001c0.102,0.062,1.124,0.729,2.08,1.925c1.003,1.261,1.933,3.017,1.937,5.438c-0.001,2.519-1.005,4.783-2.64,6.438c-1.637,1.652-3.877,2.668-6.368,2.669c-2.491-0.001-4.731-1.017-6.369-2.669c-1.635-1.654-2.639-3.919-2.64-6.438c0.005-2.499,0.995-4.292,2.035-5.558c0.517-0.625,1.043-1.098,1.425-1.401c0.191-0.152,0.346-0.263,0.445-0.329c0.049-0.034,0.085-0.058,0.104-0.069c0.005-0.004,0.009-0.006,0.012-0.008s0.004-0.002,0.004-0.002l0,0c0.946-0.567,1.262-1.802,0.705-2.763c-0.559-0.965-1.785-1.288-2.739-0.724c-0.128,0.079-1.555,0.93-3.024,2.696c-1.462,1.751-2.974,4.511-2.97,8.157C2.49,23.775,8.315,29.664,15.5,29.667c7.186-0.003,13.01-5.892,13.012-13.155C28.516,12.864,27.005,10.105,25.542,8.354zM15.5,17.523c1.105,0,2.002-0.907,2.002-2.023h-0.001V3.357c0-1.118-0.896-2.024-2.001-2.024s-2.002,0.906-2.002,2.024V15.5C13.498,16.616,14.395,17.523,15.5,17.523z";
	private static final String SVG_DOUBLE_QUOTE = "M14.505,5.873c-3.937,2.52-5.904,5.556-5.904,9.108c0,1.104,0.192,1.656,0.576,1.656l0.396-0.107c0.312-0.12,0.563-0.18,0.756-0.18c1.128,0,2.07,0.411,2.826,1.229c0.756,0.82,1.134,1.832,1.134,3.037c0,1.157-0.408,2.14-1.224,2.947c-0.816,0.807-1.801,1.211-2.952,1.211c-1.608,0-2.935-0.661-3.979-1.984c-1.044-1.321-1.565-2.98-1.565-4.977c0-2.259,0.443-4.327,1.332-6.203c0.888-1.875,2.243-3.57,4.067-5.085c1.824-1.514,2.988-2.272,3.492-2.272c0.336,0,0.612,0.162,0.828,0.486c0.216,0.324,0.324,0.606,0.324,0.846L14.505,5.873zM27.465,5.873c-3.937,2.52-5.904,5.556-5.904,9.108c0,1.104,0.192,1.656,0.576,1.656l0.396-0.107c0.312-0.12,0.563-0.18,0.756-0.18c1.104,0,2.04,0.411,2.808,1.229c0.769,0.82,1.152,1.832,1.152,3.037c0,1.157-0.408,2.14-1.224,2.947c-0.816,0.807-1.801,1.211-2.952,1.211c-1.608,0-2.935-0.661-3.979-1.984c-1.044-1.321-1.565-2.98-1.565-4.977c0-2.284,0.449-4.369,1.35-6.256c0.9-1.887,2.256-3.577,4.068-5.067c1.812-1.49,2.97-2.236,3.474-2.236c0.336,0,0.612,0.162,0.828,0.486c0.216,0.324,0.324,0.606,0.324,0.846L27.465,5.873z";
	
	private static final String APPLICATION_TITLE = "Griswold";
	private static final int NOTIFICATION_AREA_HEIGHT = 50;
	
	private static final int NOTIFICATION_AUTOCYCLE_MILLISECONDS = 15000;
	
	private final Stage primaryStage;
	private final NavigationController navController;
	//private final MovieQuoteCycler movieQuoteCycler;
	private final NotificationAreaUpdater notificationAreaUpdater;
	private final ReadOnlyStringWrapper notificationAreaTextString;
	
	private Scene scene = null;
	private Group rootNode = null;
	private VBox sceneFrame = null;
	private HBox frameMiddleRegion = null;
	private HBox middleRightContentArea = null;
	private MenuBar menuBar = null;
	private VBox toolbar = null;
	private HBox notificationArea = null;
	private ScheduledService<Void> autoCycler = null;

	private TableView<Person> tvPerson = null;
	private TableView<AppAlphaStatus> tvAlpha = null;
	private TableView<AppBetaStatus> tvBeta = null;
	
	private ObservableList<Person> observablePersonList;
	private ObservableList<AppAlphaStatus> observableAppAlphaStatusList;
	private ObservableList<AppBetaStatus> observableAppBetaStatusList;
	
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
		//this.movieQuoteCycler = new MovieQuoteCycler(
		//		this.notificationAreaTextString);
		this.notificationAreaUpdater = new NotificationAreaUpdater(
				this.notificationAreaTextString);
		this.notificationAreaUpdater.addMessageSource(
				new CurrentTimeMessageSource());
		this.notificationAreaUpdater.addMessageSource(
				new MovieQuotesMessageSource());
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
	
	ObservableList<AppBetaStatus> createObservableAppBetaStatusList() {
		observableAppBetaStatusList = FXCollections.observableArrayList();
		
		List<AppBetaStatus> sampleStatusList = new ArrayList<AppBetaStatus>();
		Random r = new Random();
		for (int i=0; i<200; i++) {
			//int exactlyFiveDays =  432000000;
			//int oneHourAndOneMinuteAndOneMillisecond = 3660001;
			long FIVE_HUNDRED_DAYS = 43200000000L; // 500 days max
			long randomDurationMilliseconds = Math.abs(r.nextLong()) % FIVE_HUNDRED_DAYS;
			//int randomDurationMilliseconds = r.nextInt(432000000); // 5 days max
			AppBetaStatus abs = new AppBetaStatus("host-" + i + ".jimtough.com");
			abs.setLastUpdated(new Date());
			abs.setStatusString("stucco");
			abs.setUptime(new org.joda.time.Duration(randomDurationMilliseconds));
			//abs.setUptime(new org.joda.time.Duration(exactlyFiveDays + oneHourAndOneMinuteAndOneMillisecond));
			sampleStatusList.add(abs);
		}
		
		observableAppBetaStatusList.clear();
		observableAppBetaStatusList.setAll(sampleStatusList);
		
		return observableAppBetaStatusList;
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

	private void transitionOldToNewContentA(
			final HBox container,
			final Node newContent) {
		FadeTransition fadeOut = 
				new FadeTransition(javafx.util.Duration.millis(200), container);
		fadeOut.setFromValue(1.0);
		fadeOut.setToValue(0.0);
		
		fadeOut.setOnFinished(actionEvent -> {
			container.getChildren().clear();
			container.getChildren().add(newContent);
		});
		
		FadeTransition fadeIn = 
				new FadeTransition(javafx.util.Duration.millis(200), container);
		fadeIn.setFromValue(0.0);
		fadeIn.setToValue(1.0);
		
		SequentialTransition seqTransition = 
				new SequentialTransition(fadeOut, fadeIn);
		seqTransition.play();
	}

	private void transitionOldToNewContentB(
			final HBox container,
			final Node newContent) {
		final Duration durationOut = Duration.millis(500);
		final Duration durationIn = Duration.millis(500);
		final Node oldContent;
		ParallelTransition ptOut = null;
		ParallelTransition ptIn = null;
		
		if (!container.getChildren().isEmpty()) {
			oldContent = container.getChildren().get(0);
			FadeTransition fadeOutT = new FadeTransition(durationOut);
			fadeOutT.setFromValue(1.0);
			fadeOutT.setToValue(0.1);
			ScaleTransition scaleDownT = new ScaleTransition(durationOut);
			scaleDownT.setFromX(1.0);
			scaleDownT.setFromY(1.0);
			scaleDownT.setToX(0.1);
			scaleDownT.setToY(0.1);
			ptOut = new ParallelTransition(oldContent, fadeOutT, scaleDownT);
			ptOut.setOnFinished(actionEvent -> {
				logger.trace("ptOut finished");
			});
		} else {
			oldContent = null;
			ptOut = null;
		}

		PauseTransition pauseT = new PauseTransition(Duration.millis(1));
		pauseT.setOnFinished(actionEvent -> {
			container.getChildren().clear();
			if (oldContent != null) {
				// Restore old Node to its regular property values
				oldContent.scaleXProperty().set(1.0);
				oldContent.scaleYProperty().set(1.0);
				oldContent.opacityProperty().set(1.0);
			}
			// Start new Node at its 'start of animation' property values
			newContent.scaleXProperty().set(0.1);
			newContent.scaleYProperty().set(0.1);
			newContent.opacityProperty().set(0.1);
			container.getChildren().add(newContent);
		});
		
		FadeTransition fadeInT = new FadeTransition(durationIn);
		fadeInT.setFromValue(0.1);
		fadeInT.setToValue(1.0);
		ScaleTransition scaleUpT = new ScaleTransition(durationOut);
		scaleUpT.setFromX(0.1);
		scaleUpT.setFromY(0.1);
		scaleUpT.setToX(1.0);
		scaleUpT.setToY(1.0);
		ptIn = new ParallelTransition(newContent, fadeInT, scaleUpT);
		ptIn.setOnFinished(actionEvent -> {
			logger.trace("ptIn finished");
		});
		
		//SequentialTransition seqTransition = 
		//		new SequentialTransition(ptOut, pauseT, ptIn);
		SequentialTransition seqTransition = new SequentialTransition();
		if (ptOut != null) {
			seqTransition.getChildren().add(ptOut);
		}
		seqTransition.getChildren().addAll(pauseT, ptIn);
		seqTransition.play();
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
				new BackgroundFill(Color.CORNFLOWERBLUE, null, null)));
		Tooltip tt = new Tooltip(tooltipText);
		b.setTooltip(tt);
		SVGPath icon = new SVGPath();
		icon.setFill(Color.BLACK);
		icon.setContent(iconSVGString);
		b.setGraphic(icon);
		
		b.setOnMouseEntered(mouseEvent -> {
			Button btn = (Button)mouseEvent.getSource();
			// This cast should be safe because we know this handler can
			// only be called on the button created above
			SVGPath svgPath = (SVGPath)btn.getGraphic();
			svgPath.setFill(Color.rgb(0, 255, 0));
			Bloom bloom = new Bloom();
			bloom.setThreshold(0.3);
			btn.setEffect(bloom);
		});
		b.setOnMouseExited(mouseEvent -> {
			Button btn = (Button)mouseEvent.getSource();
			btn.setEffect(null);
			SVGPath svgPath = (SVGPath)btn.getGraphic();
			svgPath.setFill(Color.BLACK);
		});
		
		
		return b;
	}
	
	VBox createToolbarContent(final NavigationController navController) {
		VBox vBox = new VBox();

		Button bTools = createToolbarButton(
				SVG_TOOLS, "Tools");
		bTools.setOnAction(actionEvent -> {
			try {
				this.toolbar.disableProperty().set(true);
				this.transitionOldToNewContentB(this.middleRightContentArea, this.tvPerson);
			} finally {
				this.toolbar.disableProperty().set(false);
			}
		});

		Button bMonitorAppAlpha = createToolbarButton(
				SVG_CLOCK_FORWARD, "Monitor App Alpha");
		bMonitorAppAlpha.setOnAction(actionEvent -> {
			try {
				this.toolbar.disableProperty().set(true);
				this.transitionOldToNewContentB(this.middleRightContentArea, this.tvAlpha);
			} finally {
				this.toolbar.disableProperty().set(false);
			}
		});

		Button bMonitorAppBeta = createToolbarButton(
				SVG_LIGHTBULB_B, "Monitor App Beta");
		bMonitorAppBeta.setOnAction(actionEvent -> {
			try {
				this.toolbar.disableProperty().set(true);
				this.transitionOldToNewContentB(this.middleRightContentArea, this.tvBeta);
			} finally {
				this.toolbar.disableProperty().set(false);
			}
		});

		Button bCycleQuote = createToolbarButton(
				SVG_DOUBLE_QUOTE, "Cycle to next movie quote");
		bCycleQuote.setOnAction(actionEvent -> {
			try {
				this.toolbar.disableProperty().set(true);
				cycleToNextNotificationMessage();
			} finally {
				this.toolbar.disableProperty().set(false);
			}
		});

		Button bExit = createToolbarButton(
				SVG_POWER, "Close application");
		bExit.setOnAction(actionEvent -> navController.exitApplication());
		
		vBox.getChildren().add(bTools);
		vBox.getChildren().add(bMonitorAppAlpha);
		vBox.getChildren().add(bMonitorAppBeta);
		vBox.getChildren().add(bCycleQuote);
		vBox.getChildren().add(bExit);

		this.toolbar = vBox;
		//toolbar.heightProperty().addListener(
		//		(observable, oldValue, newValue) -> {
		//			logger.info("toolbar height has changed: " +
		//					" | old: " + oldValue +
		//					" | new: " + newValue);
		//		});
		
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
		
		this.notificationAreaTextString.addListener(
				(observable, oldValue, newValue) -> {
			logger.debug("Notification area text has changed from [" + oldValue +
					"] to [" + newValue + "]");
		});
		//this.notificationArea.heightProperty().addListener(
		//		(observable, oldValue, newValue) -> {
		//	logger.info("notificationArea height has changed: " +
		//			" | old: " + oldValue +
		//			" | new: " + newValue);
		//});
		
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
		//this.frameMiddleRegion.heightProperty().addListener(
		//		(observable, oldValue, newValue) -> {
		//			logger.info("frameMiddleRegion height has changed: " +
		//					" | old: " + oldValue +
		//					" | new: " + newValue);
		//		});
		
		return this.frameMiddleRegion;
	}

	HBox createMiddleRightContentArea() {
		this.middleRightContentArea = new HBox();
		return this.middleRightContentArea;
	}
	
	public synchronized void cycleToNextNotificationMessage() {
		logger.info("cycleToNextNotificationMessage() | INVOKED");
		SequentialTransition sequentialTransition = 
				this.transitionByFading(this.notificationArea);
		sequentialTransition.play();
	}
	
	private SequentialTransition transitionByFading(Node node) {
		FadeTransition fadeOut = 
				new FadeTransition(javafx.util.Duration.millis(3000), node);
		fadeOut.setFromValue(1.0);
		fadeOut.setToValue(0.0);
		
		fadeOut.setOnFinished(
				//actionEvent -> this.movieQuoteCycler.cycleToNextQuote());
				actionEvent -> this.notificationAreaUpdater.rotateText());
		
		FadeTransition fadeIn = 
				new FadeTransition(javafx.util.Duration.millis(1000), node);
		fadeIn.setFromValue(0.0);
		fadeIn.setToValue(1.0);
		
		SequentialTransition seqTransition = 
				new SequentialTransition(fadeOut, fadeIn);
		return seqTransition;
	}

	TableView<Person> createPersonTableView(
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

	TableView<AppBetaStatus> createAppBetaStatusTableView(
			final ObservableList<AppBetaStatus> observableList) {
		TableView<AppBetaStatus> tv = new TableView<AppBetaStatus>();
		
		tv.setItems(observableList);
		
		tv.setOnMouseClicked(event -> {
			if (event.getButton().equals(MouseButton.PRIMARY)) {
				if (event.getClickCount() > 1) {
					logger.info("Mouse click count: " + event.getClickCount());
					AppBetaStatus status =
							tv.getSelectionModel().getSelectedItem();
					// TODO Do something with the selected item
					logger.info("Double-clicked on: " + status);
				}
			}
		});
		
		TableColumn<AppBetaStatus, String> hostnameCol = new TableColumn<>("Host");
		hostnameCol.setEditable(false);
		// Convenience form (preferred way for simple string-based properties)
		hostnameCol.setCellValueFactory(new PropertyValueFactory<AppBetaStatus,String>("hostname"));
		// Long form where I write the Callback implementation myself
		//hostnameCol.setCellValueFactory(new Callback<CellDataFeatures<AppBetaStatus, String>, ObservableValue<String>>() {
		//	public ObservableValue<String> call(CellDataFeatures<AppBetaStatus, String> p) {
		//		logger.info("called for hostname - " + p.getValue().hostnameProperty().get());
		//		return p.getValue().hostnameProperty();
		//	}
		//});
		
		TableColumn<AppBetaStatus, String> statusStringCol = new TableColumn<>("Status");
		statusStringCol.setEditable(false);
		statusStringCol.setCellValueFactory(new PropertyValueFactory<AppBetaStatus,String>("statusString"));

		TableColumn<AppBetaStatus, String> uptimeCol = new TableColumn<>("Uptime");
		uptimeCol.setEditable(false);
		uptimeCol.setCellValueFactory(new PropertyValueFactory<AppBetaStatus,String>("uptimeString"));
		
		List<TableColumn<AppBetaStatus,? extends Object>> tableColumnList = new ArrayList<>();
		tableColumnList.add(hostnameCol);
		tableColumnList.add(statusStringCol);
		tableColumnList.add(uptimeCol);
		
		tv.getColumns().setAll(tableColumnList);
		
		// selection listening
		tv.getSelectionModel().selectedItemProperty().addListener(
				(ObservableValue<? extends AppBetaStatus> observable, AppBetaStatus oldValue, AppBetaStatus newValue) -> {
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
	
	void createNotificationAreaAutoCycler() {
		if (this.primaryStage == null) {
			throw new IllegalStateException("primaryStage is null");
		}
		if (this.notificationArea == null) {
			throw new IllegalStateException("notificationArea is null");
		}
		//if (this.movieQuoteCycler == null) {
		//	throw new IllegalStateException("movieQuoteCycler is null");
		//}
		if (this.notificationAreaUpdater == null) {
			throw new IllegalStateException("notificationAreaUpdater is null");
		}
		this.autoCycler = new ScheduledService<Void>() {
			@Override
			protected Task<Void> createTask() {
				Task<Void> task = new Task<Void>() {
					@Override
					protected Void call() throws Exception {
						Platform.runLater(() -> {
							try {
								logger.info("cycling...");
								cycleToNextNotificationMessage();
							} catch (RuntimeException re) {
								logger.error("Exception in call()", re);
							} finally {}
						});
						return null;
					}
				};
				return task;
			}
		};
		this.autoCycler.setDelay(new javafx.util.Duration(NOTIFICATION_AUTOCYCLE_MILLISECONDS));
		this.autoCycler.setPeriod(new javafx.util.Duration(NOTIFICATION_AUTOCYCLE_MILLISECONDS));
	}
	
	public Scene createMainStageScene() {
		primaryStage.setWidth(800);
		primaryStage.setHeight(600);
		primaryStage.setTitle(APPLICATION_TITLE);
		
		createRootNode();
		createScene(this.rootNode);
		createMenuBar(this.navController);
		this.menuBar.setBackground(new Background(new BackgroundFill(Color.LIGHTPINK, null, null)));
		
		createMiddleRightContentArea();
		tvPerson = createPersonTableView(createObservablePersonList());
		tvAlpha = createAppAlphaStatusTableView(createObservableAppAlphaStatusList());
		tvBeta = createAppBetaStatusTableView(createObservableAppBetaStatusList());

		createNotificationArea();
		createNotificationAreaAutoCycler();
		
		createToolbarContent(navController);
		this.toolbar.setBackground(new Background(new BackgroundFill(Color.CHARTREUSE, null, null)));
		
		this.sceneFrame = new VBox();
		this.sceneFrame.setBackground(new Background(new BackgroundFill(Color.ORANGE, null, null)));
		this.rootNode.getChildren().add(sceneFrame);
		sceneFrame.prefHeightProperty().bind(scene.heightProperty());
		sceneFrame.prefWidthProperty().bind(scene.widthProperty());
		
		sceneFrame.getChildren().add(this.menuBar);
		createFrameMiddleRegion();
		this.frameMiddleRegion.getChildren().add(this.toolbar);
		this.frameMiddleRegion.getChildren().add(this.middleRightContentArea);
		
		tvPerson.prefWidthProperty().bind(frameMiddleRegion.widthProperty());
		tvAlpha.prefWidthProperty().bind(frameMiddleRegion.widthProperty());
		tvBeta.prefWidthProperty().bind(frameMiddleRegion.widthProperty());
		
		sceneFrame.getChildren().add(this.frameMiddleRegion);
		sceneFrame.getChildren().add(this.notificationArea);
		
		// Play with some effects and stuff
		//setSillyProperties(borderPane.getCenter());
		//setSillyProperties(hbox);
		//setSillyProperties(rootNode);
		
		primaryStage.setScene(scene);
		
		logger.info("Adding handlers to primaryStage to start/stop autoCycler");
		
		this.primaryStage.setOnShown(
			new EventHandler<WindowEvent>() {
				public void handle(final WindowEvent event) {
					logger.info("setOnShown() - starting autoCycler");
					autoCycler.start();
					AuthenticatedUser authUser = navController.getAuthUser();
					if (authUser != null) {
						logger.info("setOnShown() - adding auth user message source to notification updater");
						notificationAreaUpdater.addMessageSource(
								new AuthenticatedUserInfoMessageSource(authUser));
					} else {
						logger.warn("setOnShown() - authUser is null");
					}
				}
			});
		this.primaryStage.setOnHiding(
			new EventHandler<WindowEvent>() {
				public void handle(final WindowEvent event) {
					logger.info("setOnHiding() - canceling autoCycler");
					autoCycler.cancel();
				}
			});
		this.primaryStage.setOnCloseRequest(
			new EventHandler<WindowEvent>() {
				public void handle(final WindowEvent event) {
					logger.info("setOnCloseRequest() - canceling autoCycler");
					autoCycler.cancel();
				}
			});
		
		return scene;
	}
	
}
