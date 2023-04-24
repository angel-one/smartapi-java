package com.angelbroking.smartapi.routes;

import java.util.Map;

import static com.angelbroking.smartapi.utils.Constants.LOGIN_URL;
import static com.angelbroking.smartapi.utils.Constants.ROOT_URL;
import static com.angelbroking.smartapi.utils.Constants.SMARTSTREAM_WSURI;
import static com.angelbroking.smartapi.utils.Constants.SWSURI;
import static com.angelbroking.smartapi.utils.Constants.WSURI;


/**
 * This class generates end-points for all smart API calls by translating all the routes into a Java Map.
 */

public class Routes {

    public final Map<String, String> apiRoutes;


    // Initialize all routes,
    public Routes() {
        apiRoutes = ApiRoutes.ROUTES;
    }


    public String get(String key) {
        StringBuilder sb = new StringBuilder();
        sb.append(ROOT_URL);
        sb.append(apiRoutes.get(key));
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
