package com.angelbroking.smartapi;

import com.angelbroking.smartapi.routes.ApiRoutes;
import com.angelbroking.smartapi.utils.Constants;

import java.util.Map;

import static com.angelbroking.smartapi.utils.Constants.*;

/**
 This class generates end-points for all smart API calls by translating all the routes into a Java Map.
 */

public class Routes {

    public Map<String, String> routes;


    // Initialize all routes,
    @SuppressWarnings("serial")
    public Routes() {
        routes = ApiRoutes.ROUTES;
    }


    public String get(String key) {
        StringBuilder sb = new StringBuilder();
        sb.append(ROOT_URL);
        sb.append(routes.get(key));
        return sb.toString();
    }


    public String getLoginUrl() {
        return LOGIN_URL;
    }

    public String getWsuri() {
        return WSURI;
    }

    public String getSWsuri() {
        return SWSURI;
    }

    public String getSmartStreamWSURI() {
        return SMARTSTREAM_WSURI;
    }
}
