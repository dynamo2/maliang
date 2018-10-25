package com.model.data;

import org.bson.types.ObjectId;

import com.model.db.Table;

@Table(name="EZ_Admin")
public class Admin {
	protected ObjectId id;
	
	private String account;
	
	private String password;
	
	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
