package com.jimtough.griswold.workers;

import java.util.Random;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jimtough.griswold.beans.AppBetaStatus;
import com.jimtough.griswold.beans.GenericStatusCode;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;

/**
 * This class will update the list of 'App Beta' status objects from a
 * background thread
 * 
 * @author JTOUGH
 */
public class AppBetaStatusUpdater 
		extends ScheduledService<Void> {

	private static final Logger logger =
			LoggerFactory.getLogger(AppBetaStatusUpdater.class);

	private final ObservableList<AppBetaStatus> ol;
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
	
	public AppBetaStatusUpdater(final ObservableList<AppBetaStatus> ol) {
		if (ol == null) {
			throw new IllegalArgumentException("ol cannot be null");
		}
		this.ol = ol;
	}

	private void getUpdatedStatusData() {
		// does nothing yet
	}

	private void randomlyChangeGenericStatusCode(AppBetaStatus abs) {
		int x = random.nextInt(1000);
		GenericStatusCode curStatusCode = abs.getStatusCode();
		
		if (curStatusCode.equals(GenericStatusCode.NORMAL)) {
			// 0.1% chance of being changed to a bad status code
			if (x == 0) {
				abs.setStatusCode(GenericStatusCode.OFFLINE);
			} else if (x == 1) {
				abs.setStatusCode(GenericStatusCode.WARNING);
			} else if (x == 2) {
				abs.setStatusCode(GenericStatusCode.ERROR);
			}
		} else {
			// 10% chance of switching back to a normal status code
			if (x < 100) {
				abs.setStatusCode(GenericStatusCode.NORMAL);
			}
		}
	}
	
	private void updateStatusObjectList() {
		getUpdatedStatusData(); // this is where we would get real data first...
		for (AppBetaStatus abs : ol) {
			abs.setLastUpdatedDateTime(DateTime.now());
			randomlyChangeGenericStatusCode(abs);
		}
	}
	
	@Override
	protected Task<Void> createTask() {
		return new UpdateTask();
	}
	
}
