package com.angelbroking.smartapi.models;

import com.google.gson.annotations.SerializedName;

/**
 * A wrapper for profile response.
 */

public class Profile {

	@SerializedName("email")
	public String email;
	@SerializedName("name")
	public String userName;

	@SerializedName("broker")
	public String broker;

	@SerializedName("exchanges")
	public String[] exchanges;

	@SerializedName("products")
	public String[] products;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getBroker() {
		return broker;
	}

	public void setBroker(String broker) {
		this.broker = broker;
	}

	public String[] getExchanges() {
		return exchanges;
	}

	public void setExchanges(String[] exchanges) {
		this.exchanges = exchanges;
	}

	public String[] getProducts() {
		return products;
	}

	public void setProducts(String[] products) {
		this.products = products;
	}

}
