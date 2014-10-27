package com.jimtough.griswold.workers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
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

	private static final int NUMBER_OF_JMX_THREADS = 10;

	private static final class SimpleThreadFactory implements ThreadFactory {
		@Override
		public Thread newThread(Runnable r) {
			Thread t = new Thread(r);
			t.setDaemon(true);
			return t;
		}
	}
	
	private final ObservableList<AppBetaStatus> ol;

	public AppBetaStatusUpdaterRunnable(
			final ObservableList<AppBetaStatus> ol) {
		this.ol = ol;
	}
	
	void processItem(final AppBetaStatus abs) throws InterruptedException {
		try {
			//-------------------------------------------------------
			// Connect via JMX and retrieve runtime information about
			// the remote application, such as its memory usage data
			RemoteJMXAppClient jmxClient = new RemoteJMXAppClient();
			
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
			throw new InterruptedException("Thread flagged as interrupted");
		}
	}
	
	@Override
	public void run() {
		while (true) {
			if (Thread.currentThread().isInterrupted()) {
				logger.warn("This thread was interrupted - exiting");
				return;
			}
			try {
				// Create an ExecutorService with a fixed size thread pool
				ExecutorService executor = Executors.newFixedThreadPool(
						NUMBER_OF_JMX_THREADS, new SimpleThreadFactory());
				// Submit each object in the list as a runnable job for the
				// thread pool to process
				for (AppBetaStatus abs : ol) {
					executor.submit(() -> {
						try {
							processItem(abs);
						} catch (InterruptedException ie) {
							logger.info("JMX polling job has been interrupted");
						}
					});
				}
			} catch (Exception e) {
				logger.error("Caught exception" + 
						" - ignoring, but iterating from start of list", e);
			}
			try {
				// Sleep between each polling cycle so this thread does not
				// overload the application
				logger.info("Sleep for awhile before next polling cycle...");
				TimeUnit.SECONDS.sleep(5);
				logger.info("...wakey! wakey!");
			} catch (InterruptedException ie) {
				logger.warn("Caught InterruptedException - exiting", ie);
				return;
			} finally {
				
			}
		}
		//-------------------------------------------------------
	}
	
}
