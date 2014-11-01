package com.jimtough.griswold;

import java.net.MalformedURLException;
import java.net.URL;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adapted from JavaFX 8: Introduction by Example book examples, chapter 7
 */
public class VideoPlayerController {

	private static final Logger logger =
			LoggerFactory.getLogger(VideoPlayerController.class);

	private MediaPlayer mediaPlayer;
	private Point2D anchorPt;
	private Point2D previousLocation;
	private ChangeListener<Duration> progressListener;

	private final Stage stage;
	private final NavigationController navController;
	
	private static Stage VIDEO_PLAYER_STAGE;
	private static final String MEDIA_VIEW_ID = "media-view";
	private static final String STOP_BUTTON_ID = "stop-button";
	private static final String PLAY_BUTTON_ID = "play-button";
	private static final String PAUSE_BUTTON_ID = "pause-button";
	private static final String CLOSE_BUTTON_ID = "close-button";
	private static final String SEEK_POS_SLIDER_ID = "seek-position-slider";

	public VideoPlayerController(
			NavigationController navController,
			Stage primaryStage) {
		logger.info("constructor | START");
		if (navController == null) {
			throw new IllegalArgumentException("navController cannot be null");
		}
		this.navController = navController;
		this.stage = new Stage();
		this.stage.initModality(Modality.NONE);
		this.stage.initOwner(primaryStage);
		this.stage.setResizable(false);
		this.stage.centerOnScreen();
		
		//VIDEO_PLAYER_STAGE = primaryStage;
		VIDEO_PLAYER_STAGE = this.stage;
		VIDEO_PLAYER_STAGE.initStyle(StageStyle.TRANSPARENT);
		VIDEO_PLAYER_STAGE.centerOnScreen();

		Group root = new Group();
		Scene scene = new Scene(root, 540, 300, Color.rgb(0, 0, 0, 0));
		URL cssStylesheetResource = 
				this.getClass().getResource("/playing-video.css");
		logger.info("cssStylesheetResource: " + cssStylesheetResource.toString());
		
		// load JavaFX CSS style
		//scene.getStylesheets().add(
		//		getClass().getResource("playing-video.css").toExternalForm());
		scene.getStylesheets().add(cssStylesheetResource.toExternalForm());
		//primaryStage.setScene(scene);
		this.stage.setScene(scene);

		// Initialize stage to be movable via mouse
		initMovablePlayer();

		// Initialize stage to have fullscreen ability
		initFullScreenMode();

		// application area
		Node applicationArea = createApplicationArea();

		// Create the button panel
		Node buttonPanel = createButtonPanel();

		// allows the user to see the progress of the video playing
		Slider progressSlider = createSlider();

		// update slider as video is progressing (later removal)
		progressListener = (observable, oldValue, newValue) -> 
		progressSlider.setValue(newValue.toSeconds());

		// Create a media view to display video
		MediaView mediaView = createMediaView();

		// Initializing to accept files 
		// dragged over surface to load media
		initFileDragNDrop();

		// Create the close button
		Node closeButton = createCloseButton();

		root.getChildren().addAll(
				applicationArea, mediaView, buttonPanel, 
				progressSlider, closeButton);
		
		//primaryStage.show();
		logger.info("constructor | END");
	}
	
	public Stage getStage() {
		return this.stage;
	}
	
//	@Override
//	public void start(final Stage primaryStage) {
//		VIDEO_PLAYER_STAGE = primaryStage;
//		VIDEO_PLAYER_STAGE.initStyle(StageStyle.TRANSPARENT);
//		VIDEO_PLAYER_STAGE.centerOnScreen();
//
//		Group root = new Group();
//		Scene scene = new Scene(root, 540, 300, Color.rgb(0, 0, 0, 0));
//		// load JavaFX CSS style
//		scene.getStylesheets()
//		.add(getClass().getResource("playing-video.css")
//				.toExternalForm());
//		primaryStage.setScene(scene);
//
//		// Initialize stage to be movable via mouse
//		initMovablePlayer();
//
//		// Initialize stage to have fullscreen ability
//		initFullScreenMode();
//
//		// application area
//		Node applicationArea = createApplicationArea();
//
//		// Create the button panel
//		Node buttonPanel = createButtonPanel();
//
//		// allows the user to see the progress of the video playing
//		Slider progressSlider = createSlider();
//
//		// update slider as video is progressing (later removal)
//		progressListener = (observable, oldValue, newValue) -> 
//		progressSlider.setValue(newValue.toSeconds());
//
//		// Create a media view to display video
//		MediaView mediaView = createMediaView();
//
//		// Initializing to accept files 
//		// dragged over surface to load media
//		initFileDragNDrop();
//
//		// Create the close button
//		Node closeButton = createCloseButton();
//
//		root.getChildren().addAll(
//				applicationArea, mediaView, buttonPanel, 
//				progressSlider, closeButton);
//
//		primaryStage.show();
//	}

	/**
	 * Attaches event handler code (mouse event) to 
	 * toggle to Full screen mode. Double click the scene. 
	 */
	private void initFullScreenMode() {
		Scene scene = VIDEO_PLAYER_STAGE.getScene();
		// Full screen toggle
		scene.setOnMouseClicked((MouseEvent event) -> {
			if (event.getClickCount() == 2) {
				VIDEO_PLAYER_STAGE.setFullScreen(!VIDEO_PLAYER_STAGE.isFullScreen());
			}
		});
	}
	
	/**
	 * Initialize the stage to allow the mouse cursor 
	 * to move the application using dragging.
	 *
	 * @param stage
	 */
	private void initMovablePlayer() {
		Scene scene = VIDEO_PLAYER_STAGE.getScene();

		// starting initial anchor point
		scene.setOnMousePressed(mouseEvent -> {
			if (!VIDEO_PLAYER_STAGE.isFullScreen()) {
				anchorPt = new Point2D(mouseEvent.getScreenX(),
						mouseEvent.getScreenY());
			}
		});

		// dragging the entire stage
		scene.setOnMouseDragged(mouseEvent -> {
			if (anchorPt != null && previousLocation != null
					&& !VIDEO_PLAYER_STAGE.isFullScreen()) {
				VIDEO_PLAYER_STAGE.setX(previousLocation.getX()
						+ mouseEvent.getScreenX()
						- anchorPt.getX());
				VIDEO_PLAYER_STAGE.setY(previousLocation.getY()
						+ mouseEvent.getScreenY()
						- anchorPt.getY());
			}
		});

		// set the current location
		scene.setOnMouseReleased(mouseEvent -> {
			if (!VIDEO_PLAYER_STAGE.isFullScreen()) {
				previousLocation = new Point2D(VIDEO_PLAYER_STAGE.getX(),
						VIDEO_PLAYER_STAGE.getY());
			}
		});

		// Initialize previousLocation after Stage is shown
		VIDEO_PLAYER_STAGE.addEventHandler(WindowEvent.WINDOW_SHOWN,
				(WindowEvent t) -> {
					previousLocation = new Point2D(VIDEO_PLAYER_STAGE.getX(),
							VIDEO_PLAYER_STAGE.getY());
				});
	}

	/**
	 * Initialize the Drag and Drop ability for audio files.
	 *
	 * @param stage Primary Stage
	 */
	private void initFileDragNDrop() {

		Scene scene = VIDEO_PLAYER_STAGE.getScene();
		scene.setOnDragOver(dragEvent -> {
			Dragboard db = dragEvent.getDragboard();
			if (db.hasFiles() || db.hasUrl()) {
				dragEvent.acceptTransferModes(TransferMode.LINK);
			} else {
				dragEvent.consume();
			}
		});
		// Dropping over surface
		scene.setOnDragDropped(dragEvent -> {
			Dragboard db = dragEvent.getDragboard();
			boolean success = false;
			String filePath = null;
			if (db.hasFiles()) {
				success = true;
				if (db.getFiles().size() > 0) {
					try {
						filePath = db.getFiles()
								.get(0)
								.toURI().toURL().toString();
						playMedia(filePath);
					} catch (MalformedURLException ex) {
						ex.printStackTrace();
					}
				}
			} else {
				// audio file from some host
				playMedia(db.getUrl());
			}

			dragEvent.setDropCompleted(success);
			dragEvent.consume();
		}); // end of setOnDragDropped
	}

	/**
	 * After a file is dragged into the application 
	 * a new MediaPlayer is created with a media file.
	 *
	 * @param stage The stage window (primaryStage)
	 * @param url The url pointing to an audio file
	 */
	//private void playMedia(String url) {
	public void playMedia(String url) {
		Scene scene = VIDEO_PLAYER_STAGE.getScene();

		// stop previous media player and clean up
		if (mediaPlayer != null) {
			mediaPlayer.pause();
			mediaPlayer.setOnPaused(null);
			mediaPlayer.setOnPlaying(null);
			mediaPlayer.setOnReady(null);
			mediaPlayer.currentTimeProperty()
			.removeListener(progressListener);
		}
		Media media = new Media(url);
		// create a new media player
		mediaPlayer = new MediaPlayer(media);

		// as the media is playing move the slider for progress
		mediaPlayer.currentTimeProperty()
		.addListener(progressListener);

		// play video when ready status
		mediaPlayer.setOnReady(() -> {
			// display media's metadata 
			media.getMetadata().forEach( (name, val) -> {
				System.out.println(name + ": " + val);
			});
			updatePlayAndPauseButtons(false);
			Slider progressSlider
			= (Slider) scene.lookup("#" + SEEK_POS_SLIDER_ID);
			progressSlider.setValue(0);

			progressSlider.setMax(mediaPlayer.getMedia()
					.getDuration()
					.toSeconds());
			mediaPlayer.play();
		});
		// back to the beginning
		mediaPlayer.setOnEndOfMedia(() -> {
			updatePlayAndPauseButtons(true);
			// change buttons to play and rewind 
			mediaPlayer.stop();
		});
		// set the media player to display video
		MediaView mediaView
		= (MediaView) scene.lookup("#" + MEDIA_VIEW_ID);
		mediaView.setMediaPlayer(mediaPlayer);
	} // playMedia()

	/**
	 * Create a MediaView node.
	 *
	 * @return MediaView
	 */
	private MediaView createMediaView() {
		MediaView mediaView = new MediaView();
		mediaView.setId(MEDIA_VIEW_ID);

		mediaView.setPreserveRatio(true);
		mediaView.setSmooth(true);
		mediaView.fitWidthProperty()
		.bind(VIDEO_PLAYER_STAGE.getScene()
				.widthProperty()
				.subtract(220));
		mediaView.fitHeightProperty()
		.bind(VIDEO_PLAYER_STAGE.getScene()
				.heightProperty()
				.subtract(30));
		// sometimes loading errors occur
		mediaView.setOnError(mediaErrorEvent -> {
			mediaErrorEvent.getMediaError()
			.printStackTrace();
		});

		return mediaView;
	}

	/**
	 * Creates a node containing the audio player's 
	 * stop, pause and play buttons.
	 *
	 * @param primaryStage
	 * @return Node A button panel having play, pause and stop buttons.
	 */
	private Node createButtonPanel() {
		Scene scene = VIDEO_PLAYER_STAGE.getScene();
		// create button control panel
		Group buttonGroup = new Group();

		// Button area
		Rectangle buttonArea = new Rectangle(60, 30);
		buttonArea.setId("button-area");

		buttonGroup.getChildren()
		.add(buttonArea);
		// stop button control
		Node stopButton = new Rectangle(10, 10);
		stopButton.setId(STOP_BUTTON_ID);

		stopButton.setOnMousePressed(mouseEvent -> {
			if (mediaPlayer != null) {
				updatePlayAndPauseButtons(true);
				mediaPlayer.stop();
			}
		});
		buttonGroup.getChildren()
		.add(stopButton);

		// play button
		Arc playButton = new Arc(12, // center x 
				16, // center y                 
				15, // radius x
				15, // radius y
				150, // start angle
				60);  // length
		playButton.setId(PLAY_BUTTON_ID);
		playButton.setType(ArcType.ROUND);
		playButton.setOnMousePressed(mouseEvent -> mediaPlayer.play());
		buttonGroup.getChildren()
		.add(playButton);
		// pause control
		Group pauseButton = new Group();
		pauseButton.setId(PAUSE_BUTTON_ID);
		Node pauseBackground = new Circle(12, 16, 10);
		pauseBackground.getStyleClass().add("pause-circle");

		Node firstLine = new Line(6, // start x 
				6, // start y  
				6, // end x 
				14); // end y 
		firstLine.getStyleClass()
		.add("pause-line");
		firstLine.setStyle("-fx-translate-x: 34;");

		Node secondLine = new Line(6, // start x 
				6, // start y  
				6, // end x 
				14); // end y 
		secondLine.getStyleClass().add("pause-line");
		secondLine.setStyle("-fx-translate-x: 38;");

		pauseButton.getChildren()
		.addAll(pauseBackground, firstLine, secondLine);

		pauseButton.setOnMousePressed(mouseEvent -> {
			if (mediaPlayer != null) {
				updatePlayAndPauseButtons(true);
				mediaPlayer.pause();
			}
		});
		buttonGroup.getChildren()
		.add(pauseButton);

		playButton.setOnMousePressed(mouseEvent -> {
			if (mediaPlayer != null) {
				updatePlayAndPauseButtons(false);
				mediaPlayer.play();
			}
		});

		// move button group relative to scene
		buttonGroup.translateXProperty()
		.bind(scene.widthProperty()
				.subtract(buttonArea.getWidth() + 6));
		buttonGroup.translateYProperty()
		.bind(scene.heightProperty()
				.subtract(buttonArea.getHeight() + 6));
		return buttonGroup;
	}

	/**
	 * A simple rectangular area as the surface of the app.
	 *
	 * @param stage the primary stage window.
	 * @return Node a Rectangle node.
	 */
	private Node createApplicationArea() {
		Scene scene = VIDEO_PLAYER_STAGE.getScene();
		Rectangle applicationArea = new Rectangle();
		// add selector to style app-area
		applicationArea.setId("app-area");

		// make the app area rectangle the size of the scene.
		applicationArea.widthProperty()
		.bind(scene.widthProperty());
		applicationArea.heightProperty()
		.bind(scene.heightProperty());
		return applicationArea;
	}

	/**
	 * A slider to seek backward and forward that is 
	 * bound to a media player control.
	 *
	 * @return Slider control bound to media player.
	 */
	private Slider createSlider() {
		Slider slider = new Slider(0, 100, 1);
		slider.setId(SEEK_POS_SLIDER_ID);
		slider.valueProperty()
		.addListener((observable) -> {
			if (slider.isValueChanging()) {
				// must check if media is paused before seeking 
				if (mediaPlayer != null && 
						mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED) {
					//convert seconds to millis
					double dur = slider.getValue() * 1000;
					mediaPlayer.seek(Duration.millis(dur));
				}
			}
		}); //addListener()
		Scene scene = VIDEO_PLAYER_STAGE.getScene();
		slider.setTranslateX(10);
		slider.translateYProperty()
		.bind(scene.heightProperty()
				.subtract(50));
		return slider;
	}

	/**
	 * The close button to exit application
	 *
	 * @return Node representing a close button.
	 */
	private Node createCloseButton() {
		Scene scene = VIDEO_PLAYER_STAGE.getScene();
		Group closeButton = new Group();
		closeButton.setId(CLOSE_BUTTON_ID);
		Node closeBackground = new Circle(5, 0, 7);
		closeBackground.setId("close-circle");
		Node closeXmark = new Text(2, 4, "X");
		closeButton.translateXProperty()
		.bind(scene.widthProperty()
				.subtract(15));
		closeButton.setTranslateY(10);
		closeButton.getChildren()
		.addAll(closeBackground, closeXmark);
		//// exit app
		//closeButton.setOnMouseClicked(mouseEvent -> Platform.exit());
		closeButton.setOnMouseClicked(mouseEvent -> {
			this.mediaPlayer.stop();
			this.stage.close();
		});

		return closeButton;
	}

	/**
	 * Sets play button visible and pause button not visible when playVisible is
	 * true otherwise the opposite.
	 *
	 * @param playVisible - value of true the play becomes visible and pause non
	 * visible.
	 */
	private void updatePlayAndPauseButtons(boolean playVisible) {
		Scene scene = VIDEO_PLAYER_STAGE.getScene();
		Node playButton = scene.lookup("#" + PLAY_BUTTON_ID);
		Node pauseButton = scene.lookup("#" + PAUSE_BUTTON_ID);

		playButton.setVisible(playVisible);
		pauseButton.setVisible(!playVisible);
		if (playVisible) {
			// show play button
			playButton.toFront();
			pauseButton.toBack();
		} else {
			// show pause button
			pauseButton.toFront();
			playButton.toBack();

		}
	}

}
