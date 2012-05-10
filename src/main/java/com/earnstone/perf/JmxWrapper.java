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

import java.util.List;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.ReflectionException;

public class JmxWrapper implements DynamicMBean {

	private String category;
	private String group;

	public JmxWrapper(String category, String group) {
		this.category = category;
		this.group = group;
	}

	public Object getAttribute(String name) throws AttributeNotFoundException, MBeanException, ReflectionException {

		Counter counter = Registry.getCounter(category, group, name);

		if (counter != null)
			return new Double(counter.getValue());
		else
			return null;
	}

	public AttributeList getAttributes(String[] names) {

		AttributeList list = new AttributeList();
		Object value = null;

		for (String name : names) {

			Counter counter = Registry.getCounter(category, group, name);

			if (counter != null)
				value = new Double(counter.getValue());

			list.add(new Attribute(name, value));
		}

		return list;
	}

	public MBeanInfo getMBeanInfo() {

		List<Counter> counters = Registry.listCounters(category, group);
		MBeanAttributeInfo[] attrs = new MBeanAttributeInfo[counters.size()];

		for (int i = 0; i < counters.size(); i++) {
			attrs[i] = new MBeanAttributeInfo(counters.get(i).getName(), Double.class.getName(), counters.get(i).getDescription(), true, false, false);
		}

		String name = group == null || group.length() == 0 ? "com.earnstone.perf:type=" + RemoveSpecialCharacters(category) : "com.earnstone.perf:type=" + RemoveSpecialCharacters(category)
				+ ",group=" + RemoveSpecialCharacters(group);

		return new MBeanInfo(name, category, attrs, null, null, null);
	}

	public Object invoke(String arg0, Object[] arg1, String[] arg2) throws MBeanException, ReflectionException {
		return null;
	}

	public void setAttribute(Attribute attr) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
	}

	public AttributeList setAttributes(AttributeList attrList) {
		return new AttributeList();
	}

	public String getObjectTypeName() {

		if (group == null || group.length() == 0)
			return "com.earnstone.perf:type=" + RemoveSpecialCharacters(category);
		else
			return "com.earnstone.perf:type=" + RemoveSpecialCharacters(category) + ",group=" + RemoveSpecialCharacters(group);
	}

	private static String RemoveSpecialCharacters(String str) {
		char[] buffer = new char[str.length()];
		int idx = 0;

		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);

			if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c == '.') || (c == '_')) {
				buffer[idx] = c;
				idx++;
			}
		}

		return new String(buffer, 0, idx);
	}
}
