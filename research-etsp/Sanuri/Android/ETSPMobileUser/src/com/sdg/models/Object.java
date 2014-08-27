package com.sdg.models;


public class Object {
	private String objectID;
	private Integer trackerIMEI;
	private String objectName;
	private String profile;
	private String description;
	private String category;
	public String getObjectName() {
		return objectName;
	}
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}
	public String getProfile() {
		return profile;
	}
	public void setProfile(String profile) {
		this.profile = profile;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getObjectID() {
		return objectID;
	}
	public void setObjectID(String objectID) {
		this.objectID = objectID;
	}
	public Integer getTrackerIMEI() {
		return trackerIMEI;
	}
	public void setTrackerIMEI(Integer trackerIMEI) {
		this.trackerIMEI = trackerIMEI;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
}
