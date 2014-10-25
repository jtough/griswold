package com.jimtough.griswold.workers;

import java.util.Date;

import javax.management.AttributeChangeNotification;
import javax.management.MBeanServerConnection;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoteJMXAppClient {

	private static final Logger logger =
			LoggerFactory.getLogger(RemoteJMXAppClient.class);
	
	/**
	 * Inner class that will handle the notifications.
	 */
	private static class ClientListener implements NotificationListener {
		public void handleNotification(
				Notification notification,
				Object handback) {
			logger.info("Received notification:" +
					" | ClassName: " + notification.getClass().getName() +
					" | Source: " + notification.getSource() +
					" | Type: " + notification.getType() +
					" | Message: " + notification.getMessage());
			if (notification instanceof AttributeChangeNotification) {
				AttributeChangeNotification acn =
					(AttributeChangeNotification) notification;
				logger.info("AttributeName: " + acn.getAttributeName() +
					" | AttributeType: " + acn.getAttributeType() +
					" | NewValue: " + acn.getNewValue() +
					" | OldValue: " + acn.getOldValue());
			}
		}
	}

	/**
	 * Immutable container for various data properties of a single
	 * remote application instance
	 */
	public static final class InstanceRuntimeData {
		private final String hostname;
		private final long memoryUsed;
		private final long memoryMax;
		private final Date startTime;
		public InstanceRuntimeData(
				final String hostname,
				final long memoryUsed,
				final long memoryMax,
				final Date startTime) {
			this.hostname = hostname;
			this.memoryUsed = memoryUsed;
			this.memoryMax = memoryMax;
			this.startTime = startTime;
		}
		public String getHostname() {
			return hostname;
		}
		public long getMemoryUsed() {
			return memoryUsed;
		}
		public long getMemoryMax() {
			return memoryMax;
		}
		public Date getStartTime() {
			return startTime;
		}
	}
	
	public InstanceRuntimeData getInstanceRuntimeData(
			final String hostname, 
			final int port) 
			throws Exception {

		JMXConnector jmxc = null;

		try {
			logger.trace("Create an RMI connector client and " +
					"connect it to the RMI connector server");
			
			//String[] credentials = new String[2];
			//credentials[0] = user;
			//credentials[1] = password;
			//map.put("jmx.remote.credentials", credentials);
			//JMXConnector c = JMXConnectorFactory.newJMXConnector(createConnectionURL(host, port), map);
			//c.connect();
			
			final String urlString = "service:jmx:rmi:///jndi/rmi://" + 
					hostname + ":" + port + "/jmxrmi";
			JMXServiceURL url = new JMXServiceURL(urlString);
					//"service:jmx:rmi:///jndi/rmi://:9999/jmxrmi");
					
			logger.debug("Connecting");
			jmxc = JMXConnectorFactory.connect(url, null);
			logger.debug("Connected");
	
			//// Create listener
			////
			//ClientListener listener = new ClientListener();
			
			// Get an MBeanServerConnection
			//
			logger.debug("Get an MBeanServerConnection");
			MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
	
			Object oMemory = mbsc.getAttribute(
					new ObjectName("java.lang:type=Memory"), "HeapMemoryUsage");
			CompositeData cdMemory = (CompositeData) oMemory;
			//logger.info("committed memory: " + cd.get("committed"));
			long memoryUsed = (long)cdMemory.get("used");
			long memoryMax = (long)cdMemory.get("max");
			logger.debug("used memory: " + memoryUsed);
			logger.debug("max memory: " + memoryMax);

			long startTimeLong = (long) mbsc.getAttribute(
					new ObjectName("java.lang:type=Runtime"), "StartTime");
			Date startTime = new Date(startTimeLong);
			
			InstanceRuntimeData mud = new InstanceRuntimeData(
					hostname, memoryUsed, memoryMax, startTime);
			return mud;
			
			/*
			logger.info("Domains:");
			String domains[] = mbsc.getDomains();
			Arrays.sort(domains);
			for (String domain : domains) {
				logger.info(" --> Domain = " + domain);
			}
	
			logger.info("MBeanServer default domain = " + mbsc.getDefaultDomain());
			
			logger.info("MBean count = " + mbsc.getMBeanCount());
	
			logger.info("Query MBeanServer MBeans:");
			Set<ObjectName> names =
					new TreeSet<ObjectName>(mbsc.queryNames(null, null));
			for (ObjectName name : names) {
				logger.info("ObjectName = " + name);
			}
			 */
			
		} finally {
			if (jmxc != null) {
				logger.debug("Closing connection");
				jmxc.close();
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		RemoteJMXAppClient rjac = new RemoteJMXAppClient();
		rjac.getInstanceRuntimeData("jrc1dscon01", 10998);
	}

}
