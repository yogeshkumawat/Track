package com.yogesh.tracker;

public class Boy {

	protected String name;
	protected double mLatitude,mLongitude;
	protected long duration;
	
	public Boy(String name, double latitude, double longitude, long duration) {
		this.name = name;
		mLatitude = latitude;
		mLongitude = longitude;
		this.duration = duration;
	}
	
	public Boy() {
		
	}
	
}
