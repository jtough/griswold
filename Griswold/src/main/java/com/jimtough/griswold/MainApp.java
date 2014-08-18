package com.jimtough.griswold;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
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
            System.out.println("hbox width  " + hbox.getBoundsInParent().getWidth());
            System.out.println("hbox height " + hbox.getBoundsInParent().getHeight());
        });
        primaryStage.setTitle("HBox Example");
        primaryStage.setScene(scene);
        primaryStage.show();
       
        
        
        // Now I create a second window. If you close the 'primary' stage
        // above, this 'secondary' stage still lives on. Not sure yet how
        // to make this behave like a modal dialog.
        
        Stage secondaryStage = new Stage();
        Group secondaryRoot = new Group();
        
        // rounded rectangular background 
        Rectangle background = new Rectangle(320, 112);
        background.setX(0);
        background.setY(0);
        background.setArcHeight(15);
        background.setArcWidth(15);
        background.setFill(Color.rgb(0, 0, 0, .55));
        background.setStrokeWidth(1.5);
        background.setStroke(Color.AQUA);
        
        secondaryRoot.getChildren().add(background);
        
        Scene secondaryScene = new Scene(secondaryRoot, 300, 250);
        secondaryScene.setFill(new Color(0, 0.25, 0.25, 0.5));
        secondaryStage.setTitle("Secondary Stage");
        secondaryStage.setScene(secondaryScene);
        secondaryStage.show();
        
        
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
        launch(args);
    }

}
