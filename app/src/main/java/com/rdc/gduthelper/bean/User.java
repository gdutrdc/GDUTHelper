package com.rdc.gduthelper.bean;

/**
 * Created by seasonyuu on 16-8-20.
 */

public class User {
	private String xh;
	private String password;

	public User(String xh, String password) {
		this.xh = xh;
		this.password = password;
	}

	public String getXh() {
		return xh;
	}

	public void setXh(String xh) {
		this.xh = xh;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
