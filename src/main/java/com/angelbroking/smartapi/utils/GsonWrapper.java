package com.angelbroking.smartapi.utils;

import com.google.gson.Gson;

public class GsonWrapper {

    private final Gson gson;

    public GsonWrapper() {
        this.gson = new Gson();
    }

    public String toJson(Object object) {
        return gson.toJson(object);
    }

    // Add other delegate methods as needed
}

