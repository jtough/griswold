package com.jimtough.griswold.beans;

import org.joda.time.Duration;

import javafx.beans.property.ObjectPropertyBase;

/**
 * Property whose value is an instance of the JodaTime Duration class
 * 
 * @author JTOUGH
 */
public class DurationProperty extends ObjectPropertyBase<Duration> {

	private Object containingObject;
	private String name;
	
	public DurationProperty(
			final Object containingObject,
			final String name) {
		this.containingObject = containingObject;
		if (name == null) {
			this.name = "";
		} else {
			this.name = name;
		}
		this.setValue(new Duration(0));
	}
	
	@Override
	public Object getBean() {
		return this.containingObject;
	}

	@Override
	public String getName() {
		return this.name;
	}

}
