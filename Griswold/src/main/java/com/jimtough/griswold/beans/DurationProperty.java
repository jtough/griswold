package com.jimtough.griswold.beans;

import org.joda.time.Duration;

import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.value.ObservableValue;

/**
 * Property whose value is an instance of the JodaTime Duration class
 * 
 * @author JTOUGH
 */
public class DurationProperty 
		extends ObjectPropertyBase<Duration>
		// NOTE: This 'implements' clause is redundant, but I put it here
		//		 as a reminder to myself. You have to dig about 5 levels
		//		 deep into the inheritance hierarchy to see that this is
		//		 already covered by ObjectPropertyBase<T>
		implements ObservableValue<Duration> {
	
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
