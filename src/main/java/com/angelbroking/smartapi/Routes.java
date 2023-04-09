package com.angelbroking.smartapi;

import com.angelbroking.smartapi.routes.ApiRoutes;
import com.angelbroking.smartapi.utils.Constants;

import java.util.Map;

/**
 * Generates end-points for all smart api calls.
 * <p>
 * Here all the routes are translated into a Java Map.
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
        sb.append(Constants.ROOT_URL);
        sb.append(routes.get(key));
        return sb.toString();
    }


    public String getLoginUrl() {
        return Constants.LOGIN_URL;
    }

    public String getWsuri() {
        return Constants.WSURI;
    }

    public String getSWsuri() {
        return Constants.SWSURI;
    }

    public String getSmartStreamWSURI() {
        return Constants.SMARTSTREAM_WSURI;
    }
}
