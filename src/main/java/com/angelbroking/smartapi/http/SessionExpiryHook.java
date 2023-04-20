package com.angelbroking.smartapi.http;

/**
 * A callback whenever there is a token expiry
 */
public interface SessionExpiryHook {
    void sessionExpired();
}
