package com.angelbroking.smartapi.models;

import com.angelbroking.smartapi.http.SessionExpiryHook;
import com.angelbroking.smartapi.http.exceptions.SmartConnectException;
import lombok.Data;

import java.util.Optional;
@Data
public class SmartConnectParams {

    private static SessionExpiryHook sessionExpiryHook = null;
    private static boolean enableLogging = false;
    private String apiKey;
    private String accessToken;
    private String refreshToken;
    private String userId;

    public SmartConnectParams(String accessToken, String userId) {
        this.userId = userId;
        this.accessToken = accessToken;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
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

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public static SessionExpiryHook getSessionExpiryHook() {
        return sessionExpiryHook;
    }

    public static void setSessionExpiryHook(SessionExpiryHook sessionExpiryHook) {
        SmartConnectParams.sessionExpiryHook = sessionExpiryHook;
    }

    public static boolean isEnableLogging() {
        return enableLogging;
    }

    public static void setEnableLogging(boolean enableLogging) {
        SmartConnectParams.enableLogging = enableLogging;
    }

    public SmartConnectParams() {
    }

    public SmartConnectParams(String apiKey) {
        this.apiKey = apiKey;
    }

    public SmartConnectParams(String apiKey, String accessToken, String refreshToken) {
        this.apiKey = apiKey;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getApiKey() throws SmartConnectException {
        if (apiKey != null) return apiKey;
        else throw new SmartConnectException("The API key is missing.");
    }

    public String getAccessToken() throws SmartConnectException {
        if (accessToken != null) return accessToken;
        else throw new SmartConnectException("The Access Token key is missing.");
    }

    public String getUserId() {
        return Optional.ofNullable(userId).orElseThrow(() -> new SmartConnectException("The user ID is missing."));
    }

    public String getPublicToken() throws SmartConnectException {
        if (refreshToken != null) {
            return refreshToken;
        } else {
            throw new SmartConnectException("The Public Token key is missing.");
        }
    }


}