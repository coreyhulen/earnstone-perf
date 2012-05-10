/*
 * ePerf: Earnstone Performance Counters.
 * 
 * Copyright 2011 Corey Hulen, Earnstone Corporation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package com.earnstone.perf;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.sun.jdmk.comm.HtmlAdaptorServer;

/**
 * A central thread-safe repository for counters. Although it is not required to
 * register your performance counters in the registry it is recommended. Some
 * other features like exposing performance counters via JMX may depend on the
 * performance registry.
 * 
 * @author Corey Hulen
 * 
 */
public class Registry {

	private static boolean shouldRegisterJmx;
	private static HashMap<String, String> alreadSeen;
	private static LinkedHashMap<String, Counter> perfs;

	private static String KeyFormat = "%s::%s::%s";

	static {
		synchronized (Registry.class) {
			if (perfs == null) {
				perfs = new LinkedHashMap<String, Counter>();
				alreadSeen = new HashMap<String, String>();
			}
		}
	}

	/**
	 * Registers a performance counter in the system. If a counter with the
	 * supplied category, group, and name already exist then the old one will be
	 * returned. If either category or name are null or the empty string or
	 * group is null then an IllegalArgumentException exception is thrown.
	 * 
	 * @param perf
	 *            The performance counter to register.
	 */
	public static Counter register(Counter perf) {

		if (perf.getCategory() == null || perf.getCategory().length() == 0)
			throw new IllegalArgumentException("The performance counter cannot have an empty category");

		if (perf.getName() == null || perf.getName().length() == 0)
			throw new IllegalArgumentException("The performance counter cannot have an empty name");

		synchronized (perfs) {
			String key = String.format(KeyFormat, perf.getCategory(), perf.getGroup(), perf.getName());

			Counter previous = perfs.put(key, perf);
			if (shouldRegisterJmx)
				registerCounterAsJmx(perf);

			return previous;
		}
	}

	/**
	 * A list of all the categories from performance counters registered.
	 */
	public static List<String> listCategories() {
		HashMap<String, String> map = new HashMap<String, String>();

		synchronized (perfs) {
			for (Counter perf : perfs.values()) {
				map.put(perf.getCategory(), perf.getCategory());
			}
		}

		ArrayList<String> list = new ArrayList<String>();
		list.addAll(map.values());
		Collections.sort(list);
		return list;
	}

	/**
	 * A list of all the groups for the supplied category from performance
	 * counters registered.
	 */
	public static List<String> listGroupsForCategories(String category) {
		HashMap<String, String> map = new HashMap<String, String>();

		synchronized (perfs) {
			for (Counter perf : perfs.values()) {
				if (perf.getCategory().equals(category))
					map.put(perf.getGroup(), perf.getGroup());
			}
		}

		ArrayList<String> list = new ArrayList<String>();
		list.addAll(map.values());
		Collections.sort(list);
		return list;
	}

	/**
	 * Returns a newly created list with the performance counters for the
	 * supplied category and group.
	 * 
	 * @param category
	 *            The category to search for.
	 * @param group
	 *            The group to search for.
	 * @return A newly created list of the performance counters. Can be a size
	 *         of zero.
	 */
	public static List<Counter> listCounters(String category, String group) {
		ArrayList<Counter> catPerfs = new ArrayList<Counter>();

		synchronized (perfs) {
			for (Counter perf : perfs.values()) {
				if (category == null || perf.getCategory().equals(category)) {
					if (group == null || perf.getGroup().equals(group))
						catPerfs.add(perf);
				}
			}
		}

		Collections.sort(catPerfs);

		return catPerfs;
	}

	/**
	 * Convenience methods to pretty print a performance counter list. Assumes
	 * it will be called with the results from <code>listAllCounters</code> or
	 * <code>listCounters</code>, which returns a copy and therefore doesn't
	 * need any thread safety.
	 * 
	 * @param list
	 *            The list to print
	 * @return The printed list.
	 */
	public static String printPerfs(List<Counter> list) {

		if (list == null || list.size() == 0)
			return "";

		StringBuilder builder = new StringBuilder(1024);

		int maxLength = 0;
		for (Counter base : list) {
			maxLength = Math.max(base.getName().length(), maxLength);
		}

		String lastCat = null;
		String lastGroup = null;
		for (Counter base : list) {
			if (!base.getCategory().equals(lastCat)) {
				builder.append(base.getCategory()).append('\n');
				lastCat = base.getCategory();
				lastGroup = null;
			}

			if (!base.getGroup().equals(lastGroup)) {
				builder.append("   ").append(base.getGroup()).append('\n');
				lastGroup = base.getGroup();
			}

			builder.append("         ").append(base.getName());

			for (int i = 0; i < (maxLength - base.getName().length()); i++) {
				builder.append(' ');
			}

			builder.append("  ").append(base.getDisplayValue()).append('\n');
		}

		return builder.toString();
	}

	/**
	 * Find the performance counter in the registry.
	 * 
	 * @param category
	 *            The category of the performance counter
	 * @param name
	 *            The name of the performance counter
	 * @return The corresponding performance counter. May return null.
	 */
	public static Counter getCounter(String category, String group, String name) {

		synchronized (perfs) {
			return perfs.get(String.format(KeyFormat, category, group, name));
		}
	}

	private static void registerCounterAsJmx(Counter counter) {

		boolean seenInThisContext = alreadSeen.containsKey(counter.getCategory() + "::" + counter.getGroup());

		try {
			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
			JmxWrapper jmxWrapper = new JmxWrapper(counter.getCategory(), counter.getGroup());
			ObjectName objectName = new ObjectName(jmxWrapper.getObjectTypeName());

			if (seenInThisContext == false && mbs.isRegistered(objectName)) {
				// This is a funky case when running inside a servlet container
				// like
				// Tomcat who unregisters the war file and it's classes. So the
				// MBeanServer still sees a ref to the class, but it was
				// unloaded
				// By the Tomcat class loader. So we need to unregister the
				// old jmx bean and re-register with a new one.
				mbs.unregisterMBean(objectName);
			}

			if (!mbs.isRegistered(objectName)) {
				mbs.registerMBean(jmxWrapper, objectName);
				alreadSeen.put(counter.getCategory() + "::" + counter.getGroup(), null);
			}
		}
		catch (Exception e) {
			throw new IllegalArgumentException("The performance counter cannot be registered as a Jmx Bean InnerException=" + e.toString());
		}
	}

	public static void registerJmxHtmlAdapter(int port) {

		if (shouldRegisterJmx) {
			try {
				String name = "com.earnstone.perf:type=HtmlAdapter";
				boolean seenInThisContext = alreadSeen.containsKey(name);

				MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
				ObjectName adapterName = new ObjectName(name);
				
				if (seenInThisContext == false && mbs.isRegistered(adapterName)) {					
					mbs.unregisterMBean(adapterName);
				}
				
				if (!mbs.isRegistered(adapterName)) {
					HtmlAdaptorServer adapter = new HtmlAdaptorServer();
					adapter.setPort(port);
					mbs.registerMBean(adapter, adapterName);
					adapter.start();
					alreadSeen.put(name, null);
				}
			}
			catch (Exception e) {
				throw new IllegalArgumentException("The Jmx Html adaper cannot be registered as a Jmx Bean InnerException=" + e.toString());
			}
		}
	}

	public static void setShouldRegisterJmx(boolean shouldRegisterJmx) {
		Registry.shouldRegisterJmx = shouldRegisterJmx;
	}

	public static boolean isShouldRegisterJmx() {
		return shouldRegisterJmx;
	}
}
