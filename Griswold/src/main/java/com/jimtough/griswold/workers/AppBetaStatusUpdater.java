package com.jimtough.griswold.workers;

import java.util.Random;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jimtough.griswold.MainApp;
import com.jimtough.griswold.beans.AppBetaStatus;
import com.jimtough.griswold.beans.GenericStatusCode;
import com.jimtough.griswold.notification.NotificationCategory;
import com.jimtough.griswold.notification.NotificationIcon;
import com.jimtough.griswold.notification.NotificationImportance;
import com.jimtough.griswold.notification.NotificationMessage;
import com.jimtough.griswold.notification.NotificationMessageSource;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.SortType;
import javafx.scene.control.TableView;

/**
 * This class will update the list of 'App Beta' status objects from a
 * background thread
 * 
 * @author JTOUGH
 */
public class AppBetaStatusUpdater 
		extends ScheduledService<Void>
		implements NotificationMessageSource {

	private static final Logger logger =
			LoggerFactory.getLogger(AppBetaStatusUpdater.class);

	private final ObservableList<AppBetaStatus> ol;
	private final TableView<AppBetaStatus> tv;
	private final Random random = new Random();

	private class UpdateTask extends Task<Void> {
		@Override
		protected Void call() throws Exception {
			Platform.runLater(() -> {
				try {
					logger.trace("Invoking updateStatusObjectList()...");
					updateStatusObjectList();
					logger.trace("...updateStatusObjectList() complete");
				} catch (RuntimeException re) {
					logger.error("Exception when updating status data", re);
				} finally {}
			});
			return null;
		}
	}
	
	public AppBetaStatusUpdater(
			final ObservableList<AppBetaStatus> ol,
			final TableView<AppBetaStatus> tv) {
		if (ol == null) {
			throw new IllegalArgumentException("ol cannot be null");
		}
		if (tv == null) {
			throw new IllegalArgumentException("tv cannot be null");
		}
		this.ol = ol;
		this.tv = tv;
	}

	private void getUpdatedStatusData() {
		// does nothing yet
	}

	private void randomlyChangeGenericStatusCode(AppBetaStatus abs) {
		int x = random.nextInt(10000);
		GenericStatusCode curStatusCode = abs.getStatusCode();
		
		if (curStatusCode.equals(GenericStatusCode.NORMAL)) {
			// 3 in 10000 chance of being changed to a bad status code
			if (x == 0) {
				abs.setStatusCode(GenericStatusCode.OFFLINE);
			} else if (x == 1) {
				abs.setStatusCode(GenericStatusCode.WARNING);
			} else if (x == 2) {
				abs.setStatusCode(GenericStatusCode.ERROR);
			}
		} else {
			// 5% chance of switching back to a normal status code
			if (x < 500) {
				abs.setStatusCode(GenericStatusCode.NORMAL);
			}
		}
	}
	
	private void updateStatusObjectList() {
		getUpdatedStatusData(); // this is where we would get real data first...

		// Jim Tough - 2014-09-14
		// TODO The 'force re-sort' trick only works for a single-column sort.
		// Will require more effort to make this work for multi-column sort.
		// http://stackoverflow.com/questions/11096353/javafx-re-sorting-a-column-in-a-tableview
		
		TableColumn<AppBetaStatus,?> sortColumn = null;
		SortType sortType = null;
		if (tv.getSortOrder().size() == 1) {
			sortColumn = (TableColumn<AppBetaStatus,?>)tv.getSortOrder().get(0);
			sortType = sortColumn.getSortType();
		}		
		
		for (AppBetaStatus abs : ol) {
			abs.setLastUpdatedDateTime(DateTime.now());
			randomlyChangeGenericStatusCode(abs);
		}
		
		if (sortColumn != null && sortType != null) {
			tv.getSortOrder().clear();
			tv.getSortOrder().add(sortColumn);
			// these two calls trigger a re-sort
			sortColumn.setSortType(sortType);
			sortColumn.setSortable(true);
		}
	}
	
	@Override
	protected Task<Void> createTask() {
		return new UpdateTask();
	}

	@Override
	public synchronized NotificationMessage peek() {
		GenericStatusCode worstStatus = GenericStatusCode.NORMAL;
		int abnormalStatusCount = 0;
		for (AppBetaStatus abs : ol) {
			if (!abs.getStatusCode().equals(GenericStatusCode.NORMAL)) {
				abnormalStatusCount++;
				if (worstStatus.ordinal() < abs.getStatusCode().ordinal()) {
					worstStatus = abs.getStatusCode();
				}
			}
		}
		if (worstStatus == null) {
			return null;
		} else if (worstStatus.equals(GenericStatusCode.NORMAL)) {
			final String messageText = "All instances of " +
					MainApp.APP_BETA_NAME + " appear to be operating normally";
			return new NotificationMessage(
					messageText, 
					NotificationImportance.TRIVIAL, 
					NotificationCategory.INFO_POSITIVE, 
					NotificationIcon.CHECKMARK);
		} else {
			NotificationImportance importance;
			NotificationCategory category;
			NotificationIcon icon;
			final String messageText = abnormalStatusCount + 
					" instances of " + MainApp.APP_BETA_NAME + 
					" are in an abnormal state. " +
					"Most severe status is " + worstStatus.toString() + ".";
			if (worstStatus.ordinal() <= GenericStatusCode.WARNING.ordinal()) {
				importance = NotificationImportance.NORMAL;
				category = NotificationCategory.WARNING;
				icon = NotificationIcon.WARNING;
			} else if (worstStatus.equals(GenericStatusCode.ERROR)) {
				importance = NotificationImportance.HIGH;
				category = NotificationCategory.ERROR;
				icon = NotificationIcon.ERROR;
			} else {
				importance = NotificationImportance.CRITICAL;
				category = NotificationCategory.ERROR;
				icon = NotificationIcon.SKULL;
			}
			return new NotificationMessage(
					messageText, importance, category, icon);
		}
	}

	@Override
	public NotificationMessage take() {
		return peek();
	}
	
}
