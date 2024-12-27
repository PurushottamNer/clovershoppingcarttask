package com.clover.enums;

public enum MetroCities {
	MUMBAI, DELHI, BANGALORE, KOLKATA, CHENNAI, HYDERABAD, PUNE, AHMEDABAD, CHANDIGARH, JAIPUR;

	public static boolean isMetroCity(String city) {
		if (city == null) {
			return false;
		}
		try {
			return valueOf(city.toUpperCase()) != null;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}
}
