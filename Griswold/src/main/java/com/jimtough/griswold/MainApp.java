package com.jimtough.griswold;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Contains the JavaFX start() method and is the entry point to the JavaFX
 * application; runs on the main application thread
 * 
 * @author JTOUGH
 */
public class MainApp extends Application {

	private static final Logger logger =
			LoggerFactory.getLogger(MainApp.class);

	private NavigationController navController;
	private MainController mainController;
	private AuthenticationController authController;

	@Override
	public void init() {
		// NOTE: Not allowed to create a Stage or Scene in the init() method
		logger.info("init() | INVOKED");
	}
	
	@Override
	public void stop() {
		logger.info("stop() | INVOKED");
	}
	
	@Override
	public void start(final Stage primaryStage) throws Exception {
		
		logger.info("start() | ENTER");
		
		//-------------------------------------------------
		// STUFF GENERATED IN SAMPLE PROJECT BY NETBEANS
		//Parent root = FXMLLoader.load(getClass().getResource("/fxml/Scene.fxml"));
		//
		//Scene scene = new Scene(root);
		//scene.getStylesheets().add("/styles/Styles.css");
		//
		//stage.setTitle("JavaFX and Maven");
		//stage.setScene(scene);
		//stage.show();
		//-------------------------------------------------

		primaryStage.hide();
		
		navController = new NavigationController(primaryStage);
		mainController = new MainController(primaryStage, navController);
		mainController.createMainStageScene();
		authController = new AuthenticationController(navController);
		authController.createAuthenticationDialogScene(primaryStage);
		
//		// Now I create a second window. If you close the 'primary' stage
//		// above, this 'secondary' stage still lives on. Not sure yet how
//		// to make this behave like a modal dialog.
//		
//		Stage secondaryStage = new Stage();
//		Group secondaryRoot = new Group();
//		
//		// rounded rectangular background 
//		Rectangle background = new Rectangle(320, 112);
//		background.setX(0);
//		background.setY(0);
//		background.setArcHeight(15);
//		background.setArcWidth(15);
//		background.setFill(Color.rgb(0, 0, 0, .55));
//		background.setStrokeWidth(1.5);
//		background.setStroke(Color.AQUA);
//		
//		secondaryRoot.getChildren().add(background);
//		
//		Scene secondaryScene = new Scene(secondaryRoot, 300, 250);
//		secondaryScene.setFill(new Color(0, 0.25, 0.25, 0.5));
//		secondaryStage.setTitle("Secondary Stage");
//		secondaryStage.setScene(secondaryScene);
//		secondaryStage.show();
		
		logger.info("start() | EXIT");
	}

	/**
	 * The main() method is ignored in correctly deployed JavaFX application.
	 * main() serves only as fallback in case the application can not be
	 * launched through deployment artifacts, e.g., in IDEs with limited FX
	 * support. NetBeans ignores main().
	 *
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		logger.info("main() | ENTER");
		launch(args);
		logger.info("main() | EXIT");
	}

}
