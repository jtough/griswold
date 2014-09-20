package com.jimtough.griswold;

import static com.jimtough.griswold.SVGStringConstants.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jimtough.griswold.auth.AuthenticatedUser;
import com.jimtough.griswold.beans.AppAlphaStatus;
import com.jimtough.griswold.beans.AppBetaStatus;
import com.jimtough.griswold.beans.GenericStatusCode;
import com.jimtough.griswold.beans.Person;
import com.jimtough.griswold.notification.AuthenticatedUserInfoMessageSource;
import com.jimtough.griswold.notification.CurrentTimeMessageSource;
import com.jimtough.griswold.notification.MovieQuotesMessageSource;
import com.jimtough.griswold.workers.AppBetaStatusUpdater;

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
import javafx.collections.transformation.SortedList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
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
	
	private static final int NOTIFICATION_AREA_HEIGHT = 50;
	
	private static final int NOTIFICATION_AUTOCYCLE_MILLISECONDS = 15000;
	private static final int APP_BETA_STATUS_REFRESH_MILLISECONDS = 3000;
	
	//private static final Color TOOLBAR_BACKGROUND_COLOR = Color.LIGHTGREY;
	private static final Color TOOLBAR_BACKGROUND_COLOR = Color.TRANSPARENT;
	
	private final Stage primaryStage;
	private final NavigationController navController;
	//private final MovieQuoteCycler movieQuoteCycler;
	//private final NotificationAreaUpdater notificationAreaUpdater;
	private final ReadOnlyStringWrapper notificationAreaTextString;
	
	private Scene scene = null;
	private Group rootNode = null;
	private VBox sceneFrame = null;
	private HBox frameMiddleRegion = null;
	private HBox middleRightContentArea = null;
	private MenuBar menuBar = null;
	private VBox toolbar = null;
	private HBox notificationArea = null;
	private SVGPath notificationAreaIcon = null;
	private NotificationAreaUpdater notificationAreaUpdater = null;
	private ScheduledService<Void> autoCycler = null;
	private AppBetaStatusUpdater appBetaStatusUpdater = null;

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
		
		this.notificationAreaTextString = new ReadOnlyStringWrapper();
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
			aas.setLastUpdatedDateTime(DateTime.now());
			aas.setStatusCode(GenericStatusCode.NORMAL);
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
			abs.setLastUpdatedDateTime(DateTime.now());
			abs.setStatusCode(GenericStatusCode.NORMAL);
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
		
		final LinearGradient linearGradient = new LinearGradient(
				0, 
				0, 
				50, 
				75,
				false, 
				CycleMethod.REFLECT, 
				new Stop(0.0f, Color.rgb(0, 255, 0, 0.784)),
				new Stop(1.0f, Color.rgb(0, 0, 0, 0.784)));
		
		Button b = new Button();
		b.setMinHeight(50);
		b.setMinWidth(75);
		b.setMaxHeight(50);
		b.setMaxWidth(75);
		b.setBorder(new Border(
				new BorderStroke(TOOLBAR_BACKGROUND_COLOR, BorderStrokeStyle.SOLID, null, null)));
		b.setBackground(new Background(
				new BackgroundFill(linearGradient, null, null)));
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
		VBox vBox = new VBox(1);

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

	HBox createNotificationArea() 
			throws IOException {
		this.notificationArea = new HBox(2);
		this.notificationArea.prefWidthProperty().bind(this.primaryStage.widthProperty());
		this.notificationArea.prefHeight(NOTIFICATION_AREA_HEIGHT);
		this.notificationArea.setMinHeight(NOTIFICATION_AREA_HEIGHT);
		this.notificationArea.setMaxHeight(NOTIFICATION_AREA_HEIGHT);
		
		this.notificationAreaIcon = new SVGPath();
		notificationAreaIcon.setContent(SVG_INFO);

		Rectangle rect = new Rectangle(
				NOTIFICATION_AREA_HEIGHT, NOTIFICATION_AREA_HEIGHT);
		rect.setFill(Color.TRANSPARENT);
		
		DropShadow dropShadow = new DropShadow();
		dropShadow.setRadius(5.0);
		dropShadow.setOffsetX(3.0);
		dropShadow.setOffsetY(3.0);
		dropShadow.setColor(Color.color(0.4, 0.5, 0.5));		
		notificationAreaIcon.setEffect(dropShadow);
		notificationAreaIcon.visibleProperty().set(false);
		notificationAreaIcon.setOpacity(1.0);
		
		StackPane stackPane = new StackPane(rect, notificationAreaIcon);
		stackPane.setAlignment(Pos.CENTER);
		
		Text text = new Text();
		text.textProperty().bind(this.notificationAreaTextString);
		TextFlow textFlow = new TextFlow(text);
		
		this.notificationArea.getChildren().addAll(stackPane, textFlow);
		
		this.notificationAreaTextString.addListener(
				(observable, oldValue, newValue) -> {
			logger.debug("Notification area text has changed from [" + oldValue +
					"] to [" + newValue + "]");
		});
		this.notificationAreaIcon.pressedProperty().addListener(
				(observable, oldValue, newValue) -> {
			logger.info("notificationAreaIcon 'pressed' property has changed: "+
					" | old: " + oldValue +
					" | new: " + newValue);
		});
	
		this.notificationAreaUpdater = new NotificationAreaUpdater(
				this.notificationAreaTextString,
				this.notificationAreaIcon.contentProperty(),
				this.notificationAreaIcon.fillProperty(),
				this.notificationArea.backgroundProperty());
		this.notificationAreaUpdater.addMessageSource(
				new CurrentTimeMessageSource());
		this.notificationAreaUpdater.addMessageSource(
				new MovieQuotesMessageSource());
		this.notificationAreaUpdater.addMessageSource(
				this.appBetaStatusUpdater);
		
		return this.notificationArea;
	}

	HBox createFrameMiddleRegion() {
		this.frameMiddleRegion = new HBox();
		//this.frameMiddleRegion.setBackground(new Background(new BackgroundFill(Color.CYAN, null, null)));
		
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
				new FadeTransition(javafx.util.Duration.millis(2500), node);
		fadeOut.setFromValue(1.0);
		fadeOut.setToValue(0.0);
		
		fadeOut.setOnFinished(actionEvent -> {
			this.notificationAreaUpdater.rotateText();
			this.notificationAreaIcon.visibleProperty().set(true);
		});
		
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
		
		TableColumn<AppAlphaStatus, String> lastUpdatedStringCol = new TableColumn<>("Last Updated");
		lastUpdatedStringCol.setEditable(false);
		lastUpdatedStringCol.setCellValueFactory(new PropertyValueFactory<AppAlphaStatus,String>("lastUpdatedString"));
		
		List<TableColumn<AppAlphaStatus,String>> tableColumnList = new ArrayList<>();
		tableColumnList.add(hostnameCol);
		tableColumnList.add(statusStringCol);
		tableColumnList.add(lastUpdatedStringCol);
		
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
		
		// create a SortedList based on the provided ObservableList
		SortedList<AppBetaStatus> sortedList = 
				new SortedList<AppBetaStatus>(observableList);
		
		TableView<AppBetaStatus> tv = new TableView<AppBetaStatus>(sortedList);
		//tv.setItems(observableList);

		// bind the sortedList comparator to the TableView comparator
		sortedList.comparatorProperty().bind(tv.comparatorProperty());
		
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
		//hostnameCol.setCellValueFactory(new PropertyValueFactory<AppBetaStatus,String>("hostname"));
		// Long form where I write the Callback implementation myself
		hostnameCol.setCellValueFactory(new Callback<CellDataFeatures<AppBetaStatus, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<AppBetaStatus, String> p) {
				logger.info("called for hostname - " + p.getValue().hostnameProperty().get());
				return p.getValue().hostnameProperty();
			}
		});
		
		TableColumn<AppBetaStatus, String> statusStringCol = new TableColumn<>("Status");
		statusStringCol.setEditable(false);
		statusStringCol.setCellValueFactory(new PropertyValueFactory<AppBetaStatus,String>("statusString"));
		statusStringCol.setCellFactory(new Callback<TableColumn<AppBetaStatus, String>, TableCell<AppBetaStatus, String>>() {
			public TableCell<AppBetaStatus, String> call(TableColumn<AppBetaStatus, String> tc) {
				return new GenericAppStatusTableCell();
			}
		});
		
		TableColumn<AppBetaStatus, String> lastUpdatedStringCol = new TableColumn<>("Last Updated");
		lastUpdatedStringCol.setEditable(false);
		lastUpdatedStringCol.setCellValueFactory(new PropertyValueFactory<AppBetaStatus,String>("lastUpdatedString"));

		TableColumn<AppBetaStatus, String> uptimeCol = new TableColumn<>("Uptime");
		uptimeCol.setEditable(false);
		uptimeCol.setCellValueFactory(new PropertyValueFactory<AppBetaStatus,String>("uptimeString"));
		
		List<TableColumn<AppBetaStatus,? extends Object>> tableColumnList = new ArrayList<>();
		tableColumnList.add(hostnameCol);
		tableColumnList.add(statusStringCol);
		tableColumnList.add(lastUpdatedStringCol);
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
	
	void createAppBetaStatusUpdater() {
		if (this.primaryStage == null) {
			throw new IllegalStateException("primaryStage is null");
		}
		if (this.observableAppBetaStatusList == null) {
			throw new IllegalStateException("observableAppBetaStatusList is null");
		}
		this.appBetaStatusUpdater = new AppBetaStatusUpdater(
				this.observableAppBetaStatusList, this.tvBeta);
		this.appBetaStatusUpdater.setDelay(new javafx.util.Duration(APP_BETA_STATUS_REFRESH_MILLISECONDS));
		this.appBetaStatusUpdater.setPeriod(new javafx.util.Duration(APP_BETA_STATUS_REFRESH_MILLISECONDS));
	}
	
	public Scene createMainStageScene() throws IOException {
		primaryStage.setWidth(800);
		primaryStage.setHeight(600);
		primaryStage.setTitle(MainApp.APPLICATION_NAME);
		
		createRootNode();
		createScene(this.rootNode);
		createMenuBar(this.navController);
		//this.menuBar.setBackground(new Background(new BackgroundFill(Color.LIGHTPINK, null, null)));
		
		createMiddleRightContentArea();
		tvPerson = createPersonTableView(createObservablePersonList());
		tvAlpha = createAppAlphaStatusTableView(createObservableAppAlphaStatusList());
		tvBeta = createAppBetaStatusTableView(createObservableAppBetaStatusList());
		createAppBetaStatusUpdater();

		createNotificationArea();
		createNotificationAreaAutoCycler();
		
		createToolbarContent(navController);
		this.toolbar.setBackground(new Background(new BackgroundFill(TOOLBAR_BACKGROUND_COLOR, null, null)));
		
		this.sceneFrame = new VBox();
		//this.sceneFrame.setBackground(new Background(new BackgroundFill(Color.ORANGE, null, null)));
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
					logger.info("setOnShown() - starting background services");
					autoCycler.start();
					appBetaStatusUpdater.start();
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
					logger.info("setOnHiding() - canceling background services");
					autoCycler.cancel();
					appBetaStatusUpdater.cancel();
				}
			});
		this.primaryStage.setOnCloseRequest(
			new EventHandler<WindowEvent>() {
				public void handle(final WindowEvent event) {
					logger.info("setOnCloseRequest() - canceling background services");
					autoCycler.cancel();
					appBetaStatusUpdater.cancel();
				}
			});
		
		return scene;
	}
	
}
