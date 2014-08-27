package com.sdg.models;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.R.string;

public class User {

	public String getName() {
		return name;
	}
	public void setName(String string) {
		this.name = string;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public ArrayList<String> getPrevileges() {
		return previleges;
	}
	public void setPrevileges(ArrayList<String> previleges) {
		this.previleges = previleges;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	private String id;
	private String name;
	private String username;
	private String password;
	private String description;
	private ArrayList<String> previleges;
}
