package com.angelbroking.smartapi.utils;

import com.angelbroking.smartapi.http.exceptions.InvalidParamsException;
import com.angelbroking.smartapi.models.User;
import com.google.gson.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ResponseParser {


    /**
     * Parses user details response from server.
     *
     * @param response is the json response from server.
     * @return User is the parsed data.
     * @throws JSONException is thrown when there is error while parsing response.
     */
    public static User parseResponse(JSONObject response) throws JSONException {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {

            @Override
            public Date deserialize(JsonElement jsonElement, Type type,
                                    JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                try {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    return format.parse(jsonElement.getAsString());
                } catch (ParseException e) {
                    throw new InvalidParamsException("Failed to parse response due to invalid date format");
                }
            }
        });
        Gson gson = gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        User user = gson.fromJson(String.valueOf(response.get(Constants.USER_DATA)), User.class);
        user = parseArray(user, response.getJSONObject(Constants.USER_DATA));
        return user;
    }

    /**
     * Parses array details of product, exchange and order_type from json response.
     *
     * @param response is the json response from server.
     * @param user     is the object to which data is copied to from json response.
     * @return User is the pojo of parsed data.
     */
    public static User parseArray(User user, JSONObject response) throws JSONException {
        JSONArray productArray = response.getJSONArray(Constants.USER_PRODUCTS);
        String[] products = new String[productArray.length()];
        for (int i = 0; i < productArray.length(); i++) {
            products[i] = productArray.getString(i);
        }
        user.setProducts(products);
        JSONArray exchangesArray = response.getJSONArray(Constants.USER_EXCHANGES);
        String[] exchanges = new String[exchangesArray.length()];
        for (int j = 0; j < exchangesArray.length(); j++) {
            exchanges[j] = exchangesArray.getString(j);
        }
        user.setExchanges(exchanges);

        return user;
    }
}
