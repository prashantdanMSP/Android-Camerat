package com.camrate.pojo;

public class PojoFacebookFriends {
	private String id;
	private String name;
	private String url;
	boolean isitemChecked;

	public boolean isIsitemChecked() {
		return isitemChecked;
	}

	public void setIsitemChecked(boolean isitemChecked) {
		this.isitemChecked = isitemChecked;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
