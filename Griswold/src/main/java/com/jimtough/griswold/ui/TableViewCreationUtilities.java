package com.jimtough.griswold.ui;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import javafx.util.Callback;

import com.jimtough.griswold.AppBetaDetailsController;
import com.jimtough.griswold.GenericAppStatusTableCell;
import com.jimtough.griswold.beans.AppAlphaStatus;
import com.jimtough.griswold.beans.AppBetaStatus;
import com.jimtough.griswold.beans.GenericStatusCode;

/**
 * Methods to aid in creating the TableView objects for this application
 * @author JTOUGH
 */
public class TableViewCreationUtilities {

	private static final Logger logger =
			LoggerFactory.getLogger(TableViewCreationUtilities.class);

	/**
	 * Create a TableView node for displaying the list of AppAlphaStatus objects
	 * @param observableList Non-null {@code ObservableList<AppAlphaStatus>}
	 * @return Non-null {@code TableView<AppAlphaStatus>}
	 */
	public TableView<AppAlphaStatus> createAppAlphaStatusTableView(
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
	
	/**
	 * Create a TableView node for displaying the list of AppBetaStatus objects
	 * @param observableList Non-null {@code ObservableList<AppBetaStatus>}
	 * @return Non-null {@code TableView<AppBetaStatus>}
	 */
	public TableView<AppBetaStatus> createAppBetaStatusTableView(
			final ObservableList<AppBetaStatus> observableList,
			final Stage primaryStage) {
		if (observableList == null) {
			throw new IllegalArgumentException("observableList cannot be null");
		}
		
		// create a SortedList based on the provided ObservableList
		SortedList<AppBetaStatus> sortedList = 
				new SortedList<AppBetaStatus>(observableList);
		
		TableView<AppBetaStatus> tv = new TableView<AppBetaStatus>(sortedList);

		// bind the sortedList comparator to the TableView comparator
		sortedList.comparatorProperty().bind(tv.comparatorProperty());
		
		tv.setOnMouseClicked(event -> {
			if (event.getButton().equals(MouseButton.PRIMARY)) {
				if (event.getClickCount() > 1) {
					logger.info("Mouse click count: " + event.getClickCount());
					AppBetaStatus status =
							tv.getSelectionModel().getSelectedItem();
					logger.info("Double-clicked on: " + status.getHostname());
					AppBetaDetailsController abdc =
							new AppBetaDetailsController(primaryStage);
					abdc.createAppBetaStatusDetailsModalDialogScene(status);
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
				logger.debug("called for hostname - " + p.getValue().hostnameProperty().get());
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
		
		TableColumn<AppBetaStatus,Double> memoryUseCol = 
				new TableColumn<AppBetaStatus,Double>("Memory Use");
		memoryUseCol.setCellValueFactory(
				new PropertyValueFactory<AppBetaStatus, Double>("memoryUsedPercent"));
		memoryUseCol.setCellFactory(ProgressBarTableCell.<AppBetaStatus> forTableColumn());

		TableColumn<AppBetaStatus, Boolean> suppressAlertsCol = 
				new TableColumn<>("Suppress");
		suppressAlertsCol.setEditable(false);
		suppressAlertsCol.setCellValueFactory(
				new PropertyValueFactory<AppBetaStatus,Boolean>("suppressAlerts"));
		suppressAlertsCol.setCellFactory(CheckBoxTableCell.forTableColumn(suppressAlertsCol));
		
		TableColumn<AppBetaStatus, String> lastUpdatedStringCol = new TableColumn<>("Last Updated");
		lastUpdatedStringCol.setEditable(false);
		lastUpdatedStringCol.setCellValueFactory(new PropertyValueFactory<AppBetaStatus,String>("lastUpdatedString"));

		TableColumn<AppBetaStatus, String> uptimeCol = new TableColumn<>("Uptime");
		uptimeCol.setEditable(false);
		uptimeCol.setCellValueFactory(new PropertyValueFactory<AppBetaStatus,String>("uptimeString"));
		
		List<TableColumn<AppBetaStatus,? extends Object>> tableColumnList = new ArrayList<>();
		tableColumnList.add(hostnameCol);
		tableColumnList.add(statusStringCol);
		tableColumnList.add(memoryUseCol);
		tableColumnList.add(suppressAlertsCol);
		//tableColumnList.add(lastUpdatedStringCol);
		tableColumnList.add(uptimeCol);
		
		tv.getColumns().setAll(tableColumnList);
		
		// This is needed to add Context Menu support.
		// Loosely based on example from here: 
		// 		https://gist.github.com/james-d/7758918
		tv.setRowFactory(new Callback<TableView<AppBetaStatus>, TableRow<AppBetaStatus>>() {
			@Override  
			public TableRow<AppBetaStatus> call(TableView<AppBetaStatus> tableView) {
				final TableRow<AppBetaStatus> row = new TableRow<>();
				final ContextMenu contextMenu = new ContextMenu();
				
				Menu changeStatusMenu = new Menu("Manually Change Status");
				MenuItem offlineMenuItem = new MenuItem("Offline");
				MenuItem errorMenuItem = new MenuItem("Error");
				MenuItem warningMenuItem = new MenuItem("Warning");
				MenuItem unknownMenuItem = new MenuItem("Unknown");
				MenuItem normalMenuItem = new MenuItem("Normal");
				
				changeStatusMenu.getItems().addAll(
						offlineMenuItem,
						errorMenuItem,
						warningMenuItem,
						unknownMenuItem,
						normalMenuItem);

				offlineMenuItem.setOnAction((actionEvent) -> {
					AppBetaStatus item = row.getItem();
					item.setStatusCode(GenericStatusCode.OFFLINE);
					logger.info("Manually set item " + item.getHostname() +
							" to " + GenericStatusCode.OFFLINE);
				});

				errorMenuItem.setOnAction((actionEvent) -> {
					AppBetaStatus item = row.getItem();
					item.setStatusCode(GenericStatusCode.ERROR);
					logger.info("Manually set item " + item.getHostname() +
							" to " + GenericStatusCode.ERROR);
				});

				warningMenuItem.setOnAction((actionEvent) -> {
					AppBetaStatus item = row.getItem();
					item.setStatusCode(GenericStatusCode.WARNING);
					logger.info("Manually set item " + item.getHostname() +
							" to " + GenericStatusCode.WARNING);
				});

				unknownMenuItem.setOnAction((actionEvent) -> {
					AppBetaStatus item = row.getItem();
					item.setStatusCode(GenericStatusCode.UNKNOWN);
					logger.info("Manually set item " + item.getHostname() +
							" to " + GenericStatusCode.UNKNOWN);
				});

				normalMenuItem.setOnAction((actionEvent) -> {
					AppBetaStatus item = row.getItem();
					item.setStatusCode(GenericStatusCode.NORMAL);
					logger.info("Manually set item " + item.getHostname() +
							" to " + GenericStatusCode.NORMAL);
				});
				
				//final MenuItem removeMenuItem = new MenuItem("Remove (not functional yet)");
				//removeMenuItem.setOnAction(new EventHandler<ActionEvent>() {
				//	@Override
				//	public void handle(ActionEvent event) {
				//		tv.getItems().remove(row.getItem());
				//	}
				//});
				
				MenuItem suppressAlertsMenuItem = 
						new MenuItem("Suppress Alerts");
				suppressAlertsMenuItem.setOnAction((actionEvent) -> {
					AppBetaStatus item = row.getItem();
					item.setSuppressAlerts(true);
					logger.info("Suppressing alerts for " + item.getHostname());
				});
				
				contextMenu.getItems().addAll(
						//removeMenuItem,
						changeStatusMenu,
						suppressAlertsMenuItem);
				
				// Set context menu on row, but use a binding to make it only show for non-empty rows:
				row.contextMenuProperty().bind(
						Bindings.when(row.emptyProperty())
						.then((ContextMenu)null)
						.otherwise(contextMenu)
						);
				return row;
			}
		});
		
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
	
}
