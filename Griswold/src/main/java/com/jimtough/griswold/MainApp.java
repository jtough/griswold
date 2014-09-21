package com.jimtough.griswold;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jimtough.griswold.auth.UserAuthenticatorStub;

import javafx.application.Application;
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

	public static final String APPLICATION_NAME = "Project Griswold";
	
	public static final String APP_ALPHA_NAME = "App Alpha";
	public static final String APP_BETA_NAME = "App Beta";
	
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
		mainController = new MainController(
				primaryStage, 
				navController);
		mainController.createMainStageScene();
		authController = new AuthenticationController(
				navController, 
				new UserAuthenticatorStub());
		authController.createAuthenticationDialogScene(primaryStage);
		
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
