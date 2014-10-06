package com.jimtough.griswold.workers;

import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.collections.ObservableList;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.MutableDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jimtough.griswold.beans.AppBetaStatus;
import com.jimtough.griswold.beans.GenericStatusCode;
import com.jimtough.griswold.workers.RemoteJMXAppClient.InstanceRuntimeData;

public class AppBetaStatusUpdaterRunnable 
		implements Runnable {

	private static final Logger logger =
			LoggerFactory.getLogger(AppBetaStatusUpdaterRunnable.class);
	
	private final ObservableList<AppBetaStatus> ol;

	public AppBetaStatusUpdaterRunnable(
			final ObservableList<AppBetaStatus> ol) {
		this.ol = ol;
	}
	
	@Override
	public void run() {
		//-------------------------------------------------------
		// For each AppBetaStatus object, connect via JMX and
		// get its memory usage data
		RemoteJMXAppClient jmxClient = new RemoteJMXAppClient();
		while (true) {
			if (Thread.currentThread().isInterrupted()) {
				logger.warn("This thread was interrupted - exiting");
				return;
			}
			for (AppBetaStatus abs : ol) {
				try {
					//InstanceRuntimeData mud = jmxClient.getInstanceRuntimeData(
					//		abs.getHostname(), 9999);
					// stubbed out
					MutableDateTime fakeStartTime = MutableDateTime.now();
					fakeStartTime.addDays(-1);
					InstanceRuntimeData mud = new InstanceRuntimeData(
							abs.getHostname(), 
							25000000, 
							100000000, 
							fakeStartTime.toDate());
					TimeUnit.MILLISECONDS.sleep(1250);
					double memoryUsedPercent =
							(double)mud.getMemoryUsed() / (double)mud.getMemoryMax();
					Platform.runLater(() -> {
						abs.setLastUpdatedDateTime(DateTime.now());
						abs.setMemoryUsedPercent(memoryUsedPercent);
						abs.setStatusCode(GenericStatusCode.NORMAL);
						Duration d = new Duration(
								new DateTime(mud.getStartTime()),
								DateTime.now());
						abs.setUptime(d);
					});
				} catch (Exception e) {
					Platform.runLater(() -> {
						abs.setLastUpdatedDateTime(DateTime.now());
						abs.setMemoryUsedPercent(-1.0);
						abs.setStatusCode(GenericStatusCode.OFFLINE);
						abs.setUptime(new Duration(0));
					});
				}
				if (Thread.currentThread().isInterrupted()) {
					logger.warn("This thread was interrupted - exiting");
					return;
				}
				try {
					// Sleep between each instance so this thread does not
					// overload the UI with too many updates
					TimeUnit.MILLISECONDS.sleep(250);
				} catch (InterruptedException e) {
					logger.warn("This thread was interrupted - exiting");
					return;
				}
			}
		}
		//-------------------------------------------------------
	}
	
}
