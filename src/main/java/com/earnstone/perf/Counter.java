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

/**
 * The base class implementation for Java performance counters. All the
 * underlying performance counters are inherited from this class. Every class
 * inherited from Counter should be considered thread-safe.
 * 
 * @author Corey Hulen
 */
public class Counter implements Comparable<Counter> {
	
	
	private String category;
	private String group;
	private String name;
	private String description;
	private double value;
	private String displayValue;

	public Counter() {
	}

	@Override
	public String toString() {
		return getDisplayValue();
	}

	public int compareTo(Counter base) {

		int catComp = getCategory().compareTo(base.getCategory());

		if (catComp == 0) {
			int groupComp = getGroup().compareTo(base.getGroup());

			if (groupComp == 0)
				return getName().compareTo(base.getName());
			else
				return groupComp;
		}
		else
			return catComp;
	}		

	public void setCategory(String category) {
		this.category = category;
	}

	public String getCategory() {
		return category;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getGroup() {
		return group;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public double getValue() {
		return value;
	}

	public void setDisplayValue(String value) {
		this.displayValue = value;
	}

	public String getDisplayValue() {
		return displayValue;
	}	
}
