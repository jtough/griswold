package com.jimtough.griswold;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jimtough.griswold.beans.AppBetaStatus;

public class AppBetaDetailsController {

	private static final Logger logger =
			LoggerFactory.getLogger(AppBetaDetailsController.class);

	private static final int SCENE_WIDTH = 480;
	private static final int SCENE_HEIGHT = 300;
	
	private final Stage parent;
	private final Stage stage;
	private final Font labelFont;
	
	public AppBetaDetailsController(final Stage parent) {
		this.parent = parent;
		this.stage = new Stage();
		this.labelFont = new Font("Tahoma", 24);
	}

	public void onClose() {
		logger.info("onClose() | INVOKED");
		removeEffectFromParent();
		this.stage.close();
	}

	private void addEffectToParent() {
		parent.getScene().getRoot().setEffect(new GaussianBlur(5.5));
	}
	
	private void removeEffectFromParent() {
		parent.getScene().getRoot().setEffect(null);
	}
	
	public void createAppBetaStatusDetailsModalDialogScene(
			final AppBetaStatus abs) {

		try {
			addEffectToParent();
			
			// create a transparent stage
			stage.initStyle(StageStyle.TRANSPARENT);
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.initOwner(parent);
			
			Group root = new Group();
			Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT, Color.rgb(0, 0, 0, 0));
			
			Tooltip t = new Tooltip("Press ESC to close this dialog");
			Tooltip.install(root, t);
			
			stage.setScene(scene);
			scene.addEventHandler(KeyEvent.KEY_RELEASED,
					new EventHandler<KeyEvent>() {
						@Override
						public void handle(KeyEvent event) {
							if (event.getCode().equals(KeyCode.ESCAPE)) {
								logger.info("Escape key pressed - closing dialog");
								onClose();
							}
						}
					});
	
			StackPane stackPane = new StackPane();
			root.getChildren().addAll(stackPane);
			stackPane.prefWidthProperty().bind(scene.widthProperty());
			stackPane.prefHeightProperty().bind(scene.heightProperty());
			stackPane.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, new CornerRadii(15.0), null)));
			stackPane.setOpacity(0.75);
			
			GridPane gridpane = new GridPane();
			gridpane.maxWidthProperty().bind(stackPane.widthProperty().subtract(60));
			gridpane.maxHeightProperty().bind(stackPane.heightProperty().subtract(60));
			gridpane.setBorder(new Border(new BorderStroke(
					Color.GREEN,
					BorderStrokeStyle.SOLID, 
					new CornerRadii(15.0),
					null)));
			gridpane.setPadding(new Insets(2));
			gridpane.setHgap(5);
			gridpane.setVgap(3);
			gridpane.setCenterShape(true);
			//ColumnConstraints col1Size = new ColumnConstraints(100);
			//ColumnConstraints col2Size = new ColumnConstraints(200);
			//gridpane.getColumnConstraints().addAll(col1Size, col2Size);
			gridpane.setGridLinesVisible(false);
			
			Text hostnameLabel = new Text("Hostname");
			hostnameLabel.setFont(labelFont);
			gridpane.add(hostnameLabel, 0, 0);
			TextField hostnameValue = new TextField();
			hostnameValue.textProperty().bindBidirectional(abs.hostnameProperty());
			gridpane.add(hostnameValue, 1, 0);
			GridPane.setHalignment(hostnameLabel, HPos.RIGHT);
			
			Text statusLabel = new Text("Status");
			statusLabel.setFont(labelFont);
			gridpane.add(statusLabel, 0, 1);
			Text statusValue = new Text();
			statusValue.textProperty().bind(abs.statusStringProperty());
			gridpane.add(statusValue, 1, 1);
			GridPane.setHalignment(statusLabel, HPos.RIGHT);

			NumberFormat nf = DecimalFormat.getInstance();
			nf.setMinimumIntegerDigits(1);
			nf.setMinimumFractionDigits(1);
			nf.setMaximumFractionDigits(1);
			Text memoryUsedLabel = new Text("Memory Used");
			memoryUsedLabel.setFont(labelFont);
			gridpane.add(memoryUsedLabel, 0, 2);
			Text memoryUsedValueText = new Text();
			memoryUsedValueText.textProperty().bind(
					abs.memoryUsedPercentProperty().multiply(100).asString("%1.2f %%"));
			gridpane.add(memoryUsedValueText, 1, 2);
			GridPane.setHalignment(memoryUsedLabel, HPos.RIGHT);
			
			Text suppressAlertsLabel = new Text("Suppress Alerts");
			CheckBox suppressAlertsCheckbox = new CheckBox();
			suppressAlertsCheckbox.selectedProperty()
					.bindBidirectional(abs.suppressAlertsProperty());
			suppressAlertsLabel.setFont(labelFont);
			gridpane.add(suppressAlertsLabel, 0, 3);
			gridpane.add(suppressAlertsCheckbox, 1, 3);
			GridPane.setHalignment(suppressAlertsLabel, HPos.RIGHT);
			
			Text lastUpdatedLabel = new Text("Last Updated");
			Text lastUpdatedText = new Text();
			lastUpdatedText.textProperty().bind(abs.lastUpdatedStringProperty());
			lastUpdatedLabel.setFont(labelFont);
			gridpane.add(lastUpdatedLabel, 0, 4);
			gridpane.add(lastUpdatedText, 1, 4);
			GridPane.setHalignment(lastUpdatedLabel, HPos.RIGHT);
			
			Text notesLabel = new Text("Notes");
			TextArea notesText = new TextArea();
			notesText.textProperty().bindBidirectional(abs.notesProperty());
			notesText.wrapTextProperty().set(true);
			notesLabel.setFont(labelFont);
			gridpane.add(notesLabel, 0, 5);
			gridpane.add(notesText, 1, 5);
			GridPane.setHalignment(notesLabel, HPos.RIGHT);

			stackPane.getChildren().add(gridpane);
			stage.show();
			notesText.requestFocus();
		} catch (Exception e) {
			removeEffectFromParent();
		}
	}
	
}
