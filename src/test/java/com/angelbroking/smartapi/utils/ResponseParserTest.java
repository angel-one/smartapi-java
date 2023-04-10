package com.angelbroking.smartapi.utils;

import com.angelbroking.smartapi.models.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;

public class ResponseParserTest {

    @Test
    public void testParseResponse() throws JSONException {
        // Prepare a sample JSON response
        JSONObject response = new JSONObject();
        JSONObject userData = new JSONObject();
        userData.put("clientcode", 1234);
        userData.put("name", "John Doe");
        JSONArray productsArray = new JSONArray();
        productsArray.put("Product 1");
        productsArray.put("Product 2");
        userData.put("products", productsArray);
        JSONArray exchangesArray = new JSONArray();
        exchangesArray.put("Exchange 1");
        exchangesArray.put("Exchange 2");
        userData.put("exchanges", exchangesArray);
        response.put(Constants.USER_DATA, userData);

        // Parse the response
        User user = ResponseParser.parseResponse(response);

        // Assert the results
        assertNotNull(user);
        assertEquals("1234", user.getUserId());
        assertEquals("John Doe", user.getUserName());
        assertArrayEquals(new String[]{"Product 1", "Product 2"}, user.getProducts());
        assertArrayEquals(new String[]{"Exchange 1", "Exchange 2"}, user.getExchanges());
    }

    @Test
    public void testParseArray() throws JSONException {
        // Prepare a sample JSON response
        JSONObject response = new JSONObject();
        JSONArray productsArray = new JSONArray();
        productsArray.put("Product 1");
        productsArray.put("Product 2");
        response.put(Constants.USER_PRODUCTS, productsArray);
        JSONArray exchangesArray = new JSONArray();
        exchangesArray.put("Exchange 1");
        exchangesArray.put("Exchange 2");
        response.put(Constants.USER_EXCHANGES, exchangesArray);

        // Prepare a User object to pass to the parseArray method
        User user = new User();

        // Parse the array details
        ResponseParser.parseArray(user, response);

        // Assert the results
        assertArrayEquals(new String[]{"Product 1", "Product 2"}, user.getProducts());
        assertArrayEquals(new String[]{"Exchange 1", "Exchange 2"}, user.getExchanges());
    }

}
