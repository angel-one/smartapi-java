package com.angelbroking.smartapi.models;

import com.angelbroking.smartapi.Routes;
import com.angelbroking.smartapi.http.SessionExpiryHook;
import com.angelbroking.smartapi.http.exceptions.SmartConnectException;

import java.net.Proxy;
import java.util.Optional;

public class SmartConnectParams {
    private Routes routes = new Routes();
    private Proxy proxy = Proxy.NO_PROXY;
    private static SessionExpiryHook sessionExpiryHook = null;
    private static boolean enableLogging = false;
    private String apiKey;
    private String accessToken;
    private String refreshToken;
    private String userId;

    public Routes getRoutes() {
        return routes;
    }

    public void setRoutes(Routes routes) {
        this.routes = routes;
    }

    public Proxy getProxy() {
        return proxy;
    }

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }

    public static SessionExpiryHook getSessionExpiryHook() {
        return sessionExpiryHook;
    }

    public void setSessionExpiryHook(SessionExpiryHook sessionExpiryHook) {
        this.sessionExpiryHook = sessionExpiryHook;
    }

    public static boolean isEnableLogging() {
        return enableLogging;
    }

    public void setEnableLogging(boolean enableLogging) {
        this.enableLogging = enableLogging;
    }



    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        if (refreshToken != null) {
            return refreshToken;
        } else {
            throw new SmartConnectException("The Public Token key is missing.");
        }
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getApiKey() throws SmartConnectException {
        if (apiKey != null) return apiKey;
        else throw new SmartConnectException("The API key is missing.");
    }


    /**
     * Returns accessToken.
     *
     * @return String access_token is returned.
     * @throws SmartConnectException if accessToken is null.
     */
    public String getAccessToken() throws SmartConnectException {
        if (accessToken != null) return accessToken;
        else throw new SmartConnectException("The Access Token key is missing.");
    }


    /**
     * Returns userId.
     *
     * @return String userId is returned.
     * @throws SmartConnectException if userId is null.
     */
    public String getUserId() {
        return Optional.ofNullable(userId).orElseThrow(() -> new SmartConnectException("The user ID is missing."));

    }



    /**
     * Retrieves login url
     *
     * @return String loginUrl is returned.
     */
    public String getLoginURL() throws SmartConnectException {
        String baseUrl = routes.getLoginUrl();
        if (baseUrl != null) {
            return baseUrl;
        } else {
            throw new SmartConnectException("The Login URL key is missing.");
        }
    }


}

