package com.jimtough.griswold;

import static com.jimtough.griswold.SVGStringConstants.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jimtough.griswold.auth.AuthenticatedUser;
import com.jimtough.griswold.beans.AppAlphaStatus;
import com.jimtough.griswold.beans.AppBetaStatus;
import com.jimtough.griswold.beans.GenericStatusCode;
import com.jimtough.griswold.beans.Person;
import com.jimtough.griswold.notification.AuthenticatedUserInfoMessageSource;
import com.jimtough.griswold.ui.TableViewCreationUtilities;
import com.jimtough.griswold.workers.AppBetaStatusUpdater;
import com.jimtough.griswold.workers.AppBetaStatusUpdaterRunnable;

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
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
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
	
	private final TableViewCreationUtilities tableViewCreationUtilities;
	
	private Scene scene = null;
	private Group rootNode = null;
	private VBox sceneFrame = null;
	private HBox frameMiddleRegion = null;
	private Pane middleRightContentArea = null;
	private MenuBar menuBar = null;
	private VBox toolbar = null;
	private HBox notificationArea = null;
	private SVGPath notificationAreaIcon = null;
	private NotificationAreaUpdater notificationAreaUpdater = null;
	private ScheduledService<Void> autoCycler = null;
	private AppBetaStatusUpdater appBetaStatusUpdater = null;
	private Thread appBetaUpdaterThread = null;


	private TableView<Person> tvPerson = null;
	private TableView<AppAlphaStatus> tvAlpha = null;
	private TableView<AppBetaStatus> tvBeta = null;
	private HBox chartsArea = null;
	private PieChart appBetaStatusPieChart = null;
	private BarChart<Number,String> appBetaUptimeBarChart = null;
	
	private ObservableList<Person> observablePersonList;
	private ObservableList<AppAlphaStatus> observableAppAlphaStatusList;
	private ObservableList<AppBetaStatus> observableAppBetaStatusList;
	private ObservableList<PieChart.Data> observableAppBetaPieChartData;
	
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
		this.tableViewCreationUtilities = new TableViewCreationUtilities();
	}

	Group createRootNode() {
		this.rootNode = new Group();
		return this.rootNode;
	}

	Scene createScene(final Group rootNode) {
		this.scene = new Scene(rootNode, 300, 250);
		this.scene.setFill(Color.LIGHTGREY);
		this.scene.getStylesheets().add("chart-styles.css");
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
	
	// TODO Javadoc
	List<String> readAppBetaHostnamesFromFile() throws IOException {
		final List<String> hostnameList = new ArrayList<>();
		InputStream is = this.getClass().getResourceAsStream(
				"/app-beta-hostname-list.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String s;
		while ((s = br.readLine()) != null) {
			hostnameList.add(s);
		}
		if (hostnameList.isEmpty()) {
			throw new IllegalStateException("No hostnames loaded");
		}
		return hostnameList;
	}
	
	ObservableList<AppBetaStatus> createObservableAppBetaStatusList() 
			throws IOException {
		observableAppBetaStatusList = FXCollections.observableArrayList();
		
		List<String> hostnameList = readAppBetaHostnamesFromFile();
		List<AppBetaStatus> sampleStatusList = new ArrayList<AppBetaStatus>();
		//Random r = new Random();
		for (String hostname : hostnameList) {
			//long FIVE_HUNDRED_DAYS = 43200000000L; // 500 days max
			//long randomDurationMilliseconds = Math.abs(r.nextLong()) % FIVE_HUNDRED_DAYS;
			AppBetaStatus abs = new AppBetaStatus(hostname);
			abs.setLastUpdatedDateTime(DateTime.now());
			abs.setStatusCode(GenericStatusCode.UNKNOWN);
			//abs.setUptime(new org.joda.time.Duration(randomDurationMilliseconds));
			abs.setUptime(new org.joda.time.Duration(0));
			//abs.setMemoryUsedPercent(0.25);
			abs.setMemoryUsedPercent(-1.0);
			sampleStatusList.add(abs);
		}
		
		observableAppBetaStatusList.clear();
		observableAppBetaStatusList.setAll(sampleStatusList);
		
		return observableAppBetaStatusList;
	}
	
	ObservableList<PieChart.Data> createObservableAppBetaPieChartData() {
		observableAppBetaPieChartData = FXCollections.observableArrayList();
		// Initialize all status code counts to zero
		for (GenericStatusCode curEnum : GenericStatusCode.values()) {
			Data data = new Data(curEnum.displayString, 0.0D);
			observableAppBetaPieChartData.add(data);
		}
		return observableAppBetaPieChartData;
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

		// Things menu
		Menu thingsMenu = new Menu("Things");
		CheckMenuItem thing1MenuItem = new CheckMenuItem("Thing 1");
		thing1MenuItem.setSelected(true);
		thingsMenu.getItems().add(thing1MenuItem);

		CheckMenuItem thing2MenuItem = new CheckMenuItem("Thing 2");
		thing2MenuItem.setSelected(true);
		thingsMenu.getItems().add(thing2MenuItem);

		// Stuff menu
		Menu stuffMenu = new Menu("Stuff");

		// sound or turn alarm off
		ToggleGroup tGroup = new ToggleGroup();
		RadioMenuItem soundAlarmItem = new RadioMenuItem("Sound Alarm"); 
		soundAlarmItem.setToggleGroup(tGroup);

		RadioMenuItem stopAlarmItem = new RadioMenuItem("Alarm Off");
		stopAlarmItem.setToggleGroup(tGroup);
		stopAlarmItem.setSelected(true);

		stuffMenu.getItems().addAll(
				soundAlarmItem, 
				stopAlarmItem, 
				new SeparatorMenuItem());

		Menu contingencyPlans = new Menu("Contingency Plans");
		contingencyPlans.getItems().addAll(
				new CheckMenuItem("Self Destruct in T minus 50"),
				new CheckMenuItem("Turn off the coffee machine"),
				new CheckMenuItem("Run for your lives!"));

		stuffMenu.getItems().add(contingencyPlans);

		this.menuBar.getMenus().addAll(fileMenu, thingsMenu, stuffMenu);
		this.menuBar.prefWidthProperty().bind(this.primaryStage.widthProperty());
		return this.menuBar;
	}

	PieChart createAppBetaStatusPieChart(ObservableList<PieChart.Data> dataList) {
		if (dataList == null) {
			throw new IllegalArgumentException("data cannot be null");
		}
		final PieChart chart = new PieChart(dataList);
		chart.setCenterShape(true);
		chart.setLegendVisible(true);
		chart.setLegendSide(Side.LEFT);
		chart.setLabelsVisible(false);
		//chart.setLabelLineLength(20.0);
		chart.setTitle(MainApp.APP_BETA_NAME + " Statuses");
		chart.setBorder(new Border(new BorderStroke(
				Color.BLACK, 
				BorderStrokeStyle.SOLID, 
				new CornerRadii(10.0), 
				null)));
		//--------------------------------------------------------
		// Set text to display when a pie slice is clicked
		final Label caption = new Label("");
		caption.setTextFill(Color.DARKORANGE);
		caption.setStyle("-fx-font: 24 arial;");
		for (final PieChart.Data data : chart.getData()) {
			data.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED,
				new EventHandler<MouseEvent>() {
					@Override public void handle(MouseEvent e) {
						logger.info("Mouse clicked on a pie slice");
						// FIXME The log entry above is appearing, but the caption below is not displayed
						caption.setTranslateX(e.getSceneX());
						caption.setTranslateY(e.getSceneY());
						caption.setText(String.valueOf(data.getPieValue()) + " instances");
					 }
				});
		}		
		//--------------------------------------------------------
		return chart;
	}

	BarChart<Number,String> createAppBetaUptimeBarChart() {
		ObservableList<XYChart.Data<Number,String>> seriesData =
				FXCollections.observableArrayList();
		
		XYChart.Data<Number,String> oneYearOrMore = 
				new XYChart.Data<>(5, "One Year +");
		XYChart.Data<Number,String> sixToTwelveMonths = 
				new XYChart.Data<>(15, "6 to 12 Months");
		XYChart.Data<Number,String> threeToSixMonths = 
				new XYChart.Data<>(13, "3 to 6 Months");
		XYChart.Data<Number,String> oneToThreeMonths = 
				new XYChart.Data<>(8, "1 to 3 Months");
		XYChart.Data<Number,String> lessThanOneMonth = 
				new XYChart.Data<>(26, "< 1 Month");
		
		seriesData.add(lessThanOneMonth);
		seriesData.add(oneToThreeMonths);
		seriesData.add(threeToSixMonths);
		seriesData.add(sixToTwelveMonths);
		seriesData.add(oneYearOrMore);
		
		final NumberAxis xAxis = new NumberAxis();
		final CategoryAxis yAxis = new CategoryAxis();
		final BarChart<Number,String> bc = 
				new BarChart<Number,String>(xAxis,yAxis);
		bc.setTitle(MainApp.APP_BETA_NAME + " Uptimes Breakdown");
		bc.setBorder(new Border(new BorderStroke(
				Color.BLACK, 
				BorderStrokeStyle.SOLID, 
				new CornerRadii(10.0), 
				null)));
		bc.setCenterShape(true);
		bc.setLegendVisible(false);
		
		xAxis.setLabel("Count");
		//xAxis.setTickLabelRotation(90);
		yAxis.setLabel("Uptime Category");

		XYChart.Series<Number,String> series1 = new XYChart.Series<>();
		series1.setData(seriesData);
		bc.getData().add(series1);
		return bc;
	}
	
	private void transitionOldToNewContentB(
			final Pane container,
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
				//new Stop(0.0, Color.rgb(0, 255, 0, 0.784)),
				//new Stop(1.0, Color.rgb(0, 0, 0, 0.784)));
				new Stop(0.0, Color.rgb(120, 120, 120, 0.784)),
				new Stop(0.3, Color.rgb(160, 160, 160, 0.784)),
				new Stop(0.7, Color.rgb(180, 180, 180, 0.784)),
				new Stop(1.0, Color.rgb(120, 120, 120, 0.784)));
		
		Button b = new Button();
		b.setMinHeight(50);
		b.setMinWidth(75);
		b.setMaxHeight(50);
		b.setMaxWidth(75);
		b.setBorder(new Border(
				new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, null)));
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

		//Button bTools = createToolbarButton(
		//		SVG_TOOLS, "Tools");
		//bTools.setOnAction(actionEvent -> {
		//	try {
		//		this.toolbar.disableProperty().set(true);
		//		this.transitionOldToNewContentB(this.middleRightContentArea, this.tvPerson);
		//	} finally {
		//		this.toolbar.disableProperty().set(false);
		//	}
		//});

		Button bMonitorAppAlpha = createToolbarButton(
				SVG_CLOCK_FORWARD, "Monitor " + MainApp.APP_ALPHA_NAME);
		bMonitorAppAlpha.setOnAction(actionEvent -> {
			try {
				this.toolbar.disableProperty().set(true);
				this.transitionOldToNewContentB(this.middleRightContentArea, this.tvAlpha);
			} finally {
				this.toolbar.disableProperty().set(false);
			}
		});

		Button bMonitorAppBeta = createToolbarButton(
				SVG_LIGHTBULB_B, "Monitor " + MainApp.APP_BETA_NAME);
		bMonitorAppBeta.setOnAction(actionEvent -> {
			try {
				this.toolbar.disableProperty().set(true);
				this.transitionOldToNewContentB(this.middleRightContentArea, this.tvBeta);
			} finally {
				this.toolbar.disableProperty().set(false);
			}
		});

		Button bCharts = createToolbarButton(
				SVG_CHARTS, "Charts");
		bCharts.setOnAction(actionEvent -> {
			try {
				this.toolbar.disableProperty().set(true);
				this.transitionOldToNewContentB(this.middleRightContentArea, this.chartsArea);
			} finally {
				this.toolbar.disableProperty().set(false);
			}
		});

		Button bCycleQuote = createToolbarButton(
				SVG_DOUBLE_QUOTE, "Cycle to next notification message");
		bCycleQuote.setOnAction(actionEvent -> {
			try {
				this.toolbar.disableProperty().set(true);
				cycleToNextNotificationMessage();
			} finally {
				this.toolbar.disableProperty().set(false);
			}
		});

		Button bPlayVideoTutorial = createToolbarButton(
				SVG_PLAY_MEDIA, "Play video tutorial");
		bPlayVideoTutorial.setOnAction(actionEvent -> {
			try {
				this.toolbar.disableProperty().set(true);
				this.navController.showVideoPlayer();
			} finally {
				this.toolbar.disableProperty().set(false);
			}
		});
		
		Button bExit = createToolbarButton(
				SVG_POWER, "Close application");
		bExit.setOnAction(actionEvent -> navController.exitApplication());
		
		//vBox.getChildren().add(bTools);
		vBox.getChildren().add(bMonitorAppAlpha);
		vBox.getChildren().add(bMonitorAppBeta);
		vBox.getChildren().add(bCharts);
		vBox.getChildren().add(bCycleQuote);
		vBox.getChildren().add(bPlayVideoTutorial);
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
		//this.notificationAreaUpdater.addMessageSource(
		//		new CurrentTimeMessageSource());
		//this.notificationAreaUpdater.addMessageSource(
		//		new MovieQuotesMessageSource());
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

	Pane createMiddleRightContentArea() {
		this.middleRightContentArea = new Pane();
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
				this.observableAppBetaStatusList, 
				this.observableAppBetaPieChartData, 
				this.tvBeta);
		this.appBetaStatusUpdater.setDelay(new javafx.util.Duration(APP_BETA_STATUS_REFRESH_MILLISECONDS));
		this.appBetaStatusUpdater.setPeriod(new javafx.util.Duration(APP_BETA_STATUS_REFRESH_MILLISECONDS));
	}
	
	public Scene createMainStageScene() throws IOException {
		primaryStage.setWidth(1024);
		primaryStage.setHeight(768);
		primaryStage.setTitle(MainApp.APPLICATION_NAME);
		
		createRootNode();
		createScene(this.rootNode);
		createMenuBar(this.navController);
		//this.menuBar.setBackground(new Background(new BackgroundFill(Color.LIGHTPINK, null, null)));
		
		createMiddleRightContentArea();
		tvPerson = createPersonTableView(createObservablePersonList());
		tvAlpha = tableViewCreationUtilities.createAppAlphaStatusTableView(
				createObservableAppAlphaStatusList());
		tvBeta = tableViewCreationUtilities.createAppBetaStatusTableView(
				createObservableAppBetaStatusList(),
				this.primaryStage);
		chartsArea = new HBox(5);
		appBetaStatusPieChart = createAppBetaStatusPieChart(
				createObservableAppBetaPieChartData());
		appBetaUptimeBarChart = createAppBetaUptimeBarChart();
		chartsArea.getChildren().addAll(
				appBetaStatusPieChart, appBetaUptimeBarChart);
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
		middleRightContentArea.prefHeightProperty().bind(frameMiddleRegion.heightProperty());
		middleRightContentArea.prefWidthProperty().bind(
				sceneFrame.widthProperty()
					.subtract(this.toolbar.widthProperty()));
		
		tvPerson.prefWidthProperty().bind(middleRightContentArea.widthProperty());
		tvPerson.prefHeightProperty().bind(middleRightContentArea.heightProperty());
		tvAlpha.prefWidthProperty().bind(middleRightContentArea.widthProperty());
		tvAlpha.prefHeightProperty().bind(middleRightContentArea.heightProperty());
		tvBeta.prefWidthProperty().bind(middleRightContentArea.widthProperty());
		tvBeta.prefHeightProperty().bind(middleRightContentArea.heightProperty());

		chartsArea.prefWidthProperty().bind(middleRightContentArea.widthProperty());
		chartsArea.prefHeightProperty().bind(middleRightContentArea.heightProperty());
		this.appBetaStatusPieChart.prefWidthProperty().bind(
				middleRightContentArea.widthProperty().divide(2));
		this.appBetaUptimeBarChart.prefWidthProperty().bind(
				middleRightContentArea.widthProperty().divide(2));
		
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
					AppBetaStatusUpdaterRunnable absUpdaterRunnable =
							new AppBetaStatusUpdaterRunnable(observableAppBetaStatusList);
					appBetaUpdaterThread = new Thread(absUpdaterRunnable, "AppBetaUpdater");
					appBetaUpdaterThread.setDaemon(true);
					appBetaUpdaterThread.start();
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
					appBetaUpdaterThread.interrupt();
				}
			});
		
		return scene;
	}
	
}
