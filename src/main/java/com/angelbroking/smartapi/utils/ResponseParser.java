package com.angelbroking.smartapi.utils;

import com.angelbroking.smartapi.http.exceptions.InvalidParamsException;
import com.angelbroking.smartapi.http.response.HttpResponse;
import com.angelbroking.smartapi.models.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.angelbroking.smartapi.utils.Constants.USER_DATA;
import static com.angelbroking.smartapi.utils.Constants.USER_EXCHANGES;
import static com.angelbroking.smartapi.utils.Constants.USER_PRODUCTS;

public class ResponseParser {

//    Added a private constructor to hide the implicit public one.


    private ResponseParser() {
    }

    /**
     * Parses user details response from server.
     *
     * @param httpResponse is the response from server.
     * @return User is the parsed data.
     * @throws JSONException is thrown when there is error while parsing response.
     */
    public static User parseResponse(HttpResponse httpResponse) throws JSONException {
        JSONObject  response = new JSONObject(httpResponse.getBody().toString());
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (jsonElement, type, jsonDeserializationContext) -> {
            if (jsonElement == null || jsonElement.getAsString().isEmpty()) {
                // return a default date value
                return new Date(0L); // or any other default date value
            }
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                return format.parse(jsonElement.getAsString());
            } catch (ParseException e) {
                throw new InvalidParamsException("Failed to parse response due to invalid date format");
            }
        });

        Gson gson = gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        User user = gson.fromJson(String.valueOf(response.get(USER_DATA)), User.class);
        return parseArray(user, response.getJSONObject(USER_DATA));
    }

    /**
     * Parses array details of product, exchange and order_type from json response.
     *
     * @param response is the json response from server.
     * @param user     is the object to which data is copied to from json response.
     * @return User is the pojo of parsed data.
     */
    public static User parseArray(User user, JSONObject response) throws JSONException {
        JSONArray productArray = response.getJSONArray(USER_PRODUCTS);
        String[] products = new String[productArray.length()];
        for (int i = 0; i < productArray.length(); i++) {
            products[i] = productArray.getString(i);
        }
        user.setProducts(products);
        JSONArray exchangesArray = response.getJSONArray(USER_EXCHANGES);
        String[] exchanges = new String[exchangesArray.length()];
        for (int j = 0; j < exchangesArray.length(); j++) {
            exchanges[j] = exchangesArray.getString(j);
        }
        user.setExchanges(exchanges);

        return user;
    }
}
