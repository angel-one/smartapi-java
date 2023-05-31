package com.angelbroking.smartapi.models;

import com.angelbroking.smartapi.http.SessionExpiryHook;
import com.angelbroking.smartapi.http.exceptions.SmartConnectException;
import lombok.Data;

import java.util.Optional;

import static com.angelbroking.smartapi.utils.Constants.ENABLE_LOGGING;
import static com.angelbroking.smartapi.utils.Utils.validateInputNotNullCheck;

@Data
public class SmartConnectParams {

    private static SessionExpiryHook sessionExpiryHook = null;
    private String apiKey;
    private String accessToken;
    private String refreshToken;
    private String userId;

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

    public static boolean isEnableLogging() {
        return ENABLE_LOGGING;
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
        if (validateInputNotNullCheck(apiKey)) return apiKey;
        else throw new SmartConnectException("The API key is missing.");
    }

    public String getAccessToken() throws SmartConnectException {
        if (validateInputNotNullCheck(accessToken)) return accessToken;
        else throw new SmartConnectException("The Access Token key is missing.");
    }

    public String getUserId() {
        return Optional.ofNullable(userId).orElseThrow(() -> new SmartConnectException("The user ID is missing."));
    }

    public static void setSessionExpiryHook(SessionExpiryHook hook) {
        sessionExpiryHook = hook;
    }

    public static SessionExpiryHook getSessionExpiryHook() {
        return sessionExpiryHook;
    }
}