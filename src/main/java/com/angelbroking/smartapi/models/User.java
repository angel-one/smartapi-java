package com.angelbroking.smartapi.models;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;

/**
 * A wrapper for user and session details.
 */
public class User {

	@SerializedName("name")
	public String userName;

	@SerializedName("clientcode")
	public String userId;

	@SerializedName("mobileno")
	public String mobileNo;

	@SerializedName("broker")
	public String brokerName;

	@SerializedName("email")
	public String email;

	@SerializedName("lastlogintime")
	public Date lastLoginTime;

	@SerializedName("accessToken")
	public String accessToken;

	@SerializedName("refreshToken")
	public String refreshToken;

	public String[] products;
	public String[] exchanges;

	public String[] orderTypes; // Add this field
	@SerializedName("feedToken")
	public String feedToken;

	/**
	 * Parses user details response from server.
	 * 
	 * @param response is the json response from server.
	 * @throws JSONException is thrown when there is error while parsing response.
	 * @return User is the parsed data.
	 */
	public User parseResponse(JSONObject response) throws JSONException {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {

			@Override
			public Date deserialize(JsonElement jsonElement, Type type,
					JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
				try {
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					return format.parse(jsonElement.getAsString());
				} catch (ParseException e) {
					return null;
				}
			}
		});
		Gson gson = gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		User user = gson.fromJson(String.valueOf(response.get("data")), User.class);
		user = parseArray(user, response.getJSONObject("data"));
		return user;
	}

	/**
	 * Parses array details of product, exchange and order_type from json response.
	 * 
	 * @param response is the json response from server.
	 * @param user     is the object to which data is copied to from json response.
	 * @return User is the pojo of parsed data.
	 */
	public User parseArray(User user, JSONObject response) throws JSONException {
		JSONArray productArray = response.getJSONArray("products");
		user.products = new String[productArray.length()];
		for (int i = 0; i < productArray.length(); i++) {
			user.products[i] = productArray.getString(i);
		}

		JSONArray exchangesArray = response.getJSONArray("exchanges");
		user.exchanges = new String[exchangesArray.length()];
		for (int j = 0; j < exchangesArray.length(); j++) {
			user.exchanges[j] = exchangesArray.getString(j);
		}

		JSONArray orderTypeArray = response.optJSONArray("order_types"); // Use optJSONArray for safety
		if (orderTypeArray != null) {
			user.orderTypes = new String[orderTypeArray.length()];
			for (int k = 0; k < orderTypeArray.length(); k++) {
				user.orderTypes[k] = orderTypeArray.getString(k);
			}
		} else {
			user.orderTypes = new String[0]; // Initialize to empty array if not present
		}

		return user;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getBrokerName() {
		return brokerName;
	}

	public void setBrokerName(String brokerName) {
		this.brokerName = brokerName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String[] getProducts() {
		return products;
	}

	public void setProducts(String[] products) {
		this.products = products;
	}

	public String[] getExchanges() {
		return exchanges;
	}

	public void setExchanges(String[] exchanges) {
		this.exchanges = exchanges;
	}

	public String getFeedToken() {
		return feedToken;
	}

	public void setFeedToken(String feedToken) {
		this.feedToken = feedToken;
	}

	// Add the getOrderTypes method
	public List<String> getOrderTypes() {
		if (this.orderTypes != null) {
			return Arrays.asList(this.orderTypes);
		}
		return Arrays.asList(); // Return empty list if not populated
	}

	@Override
	public String toString() {
		return "User [userName=" + userName + ", userId=" + userId + ", mobileNo=" + mobileNo + ", brokerName="
				+ brokerName + ", email=" + email + ", lastLoginTime=" + lastLoginTime + ", accessToken=" + accessToken
				+ ", refreshToken=" + refreshToken + ", products=" + Arrays.toString(products) + ", exchanges="
				+ Arrays.toString(exchanges) + ", feedToken=" + feedToken + "]";
	}

}
