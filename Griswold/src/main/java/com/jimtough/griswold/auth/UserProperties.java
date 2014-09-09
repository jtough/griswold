package com.jimtough.griswold.auth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Container for the properties associated with a particular user
 * 
 * @author JTOUGH
 */
public class UserProperties {

	public static enum UserPropertyKey {
		LDAP_DN("distinguishedName"),
		LDAP_CN("cn"),
		SURNAME("sn"),
		GIVEN_NAME("givenName"),
		COUNTRY_ABBREV("c"),
		CITY_NAME("l"),
		PROV_OR_STATE_ABBREV("st"),
		JOB_TITLE("title"),
		DISPLAY_NAME("displayName"),
		COUNTRY("co"),
		MAIL_NICKNAME("mailNickname"),
		NAME("name"),
		USER_PRINCIPAL_NAME("userPrincipalName"),
		EMAIL("mail"),
		DEPARTMENT("department"),
		MOBILE_NUMBER("mobile");
		
		/**
		 * Active Directory LDAP attribute name for this property value
		 * (may not be the same on every AD server, of course)
		 */
		public final String adKey;
	
		private UserPropertyKey(final String adKey) {
			this.adKey = adKey;
		}
	}

	public static final String AD_KEY_MANAGER = "manager";
	public static final String AD_KEY_EMPLOYEES = "directReports";
	
	private Map<UserPropertyKey,List<String>> userPropertyMap =
			new HashMap<UserPropertyKey,List<String>>();
	private List<UserProperties> employeeList = null;
	private UserProperties manager = null;
	
	public UserProperties() {}

	/**
	 * Associate a single property value with a key
	 * @param key Non-null
	 * @param value
	 */
	public void setUserPropertyValue(UserPropertyKey key, String value) {
		if (key == null) {
			throw new IllegalArgumentException("key cannot be null");
		}
		if (value == null) {
			this.userPropertyMap.remove(key);
		} else {
			List<String> singlePropertyList = new ArrayList<String>();
			singlePropertyList.add(value);
			this.userPropertyMap.put(key, singlePropertyList);
		}
	}

	/**
	 * Associate a list of property values with a key
	 * @param key Non-null
	 * @param value
	 */
	public void setUserPropertyValues(UserPropertyKey key, List<String> value) {
		if (key == null) {
			throw new IllegalArgumentException("key cannot be null");
		}
		if (value == null) {
			this.userPropertyMap.remove(key);
		} else {
			this.userPropertyMap.put(key, value);
		}
	}

	/**
	 * Get the (first) property value associated with this key
	 * @param key Non-null
	 * @return String containing the property for this key;
	 *  returns null if there is no property associated with the key,
	 *  and returns the first value found if there is a list of values
	 */
	public String getUserPropertyValue(UserPropertyKey key) {
		if (key == null) {
			throw new IllegalArgumentException("key cannot be null");
		}
		List<String> propertyList = this.userPropertyMap.get(key);
		if (propertyList == null) {
			return null;
		} else if (propertyList.isEmpty()) {
			return null;
		}
		return propertyList.get(0);
	}

	/**
	 * get the list of property values associated with this key
	 * @param key Non-null
	 * @return {@code List<String>} containing the properties for this key,
	 *  or null if there are no properties associated with the key
	 */
	public List<String> getUserPropertyValues(UserPropertyKey key) {
		if (key == null) {
			throw new IllegalArgumentException("key cannot be null");
		}
		return this.userPropertyMap.get(key);
	}
	
	public void setManager(UserProperties manager) {
		this.manager = manager;
	}
	
	public UserProperties getManager() {
		return this.manager;
	}
	
	public void setEmployees(List<UserProperties> employeeList) {
		this.employeeList = employeeList;
	}
	
	public List<UserProperties> getEmployees() {
		return this.employeeList;
	}
	
}
