package com.angelbroking.smartapi.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.util.TextUtils;
import org.json.JSONObject;

import java.util.Arrays;

import static com.angelbroking.smartapi.utils.Constants.*;

public class Utils {

    // Private constructor to prevent instantiation from outside the class
    private Utils() {
        // This constructor is intentionally left blank
    }

    /**
     * Checks whether the given CharSequence is empty (null or zero-length).
     *
     * @param cs the CharSequence to check
     * @return true if the given CharSequence is empty, false otherwise
     */
    public static boolean isEmpty(final CharSequence cs) {
        return TextUtils.isEmpty(cs);
    }

    /**
     * Checks whether the given CharSequence is not empty (not null and not zero-length).
     *
     * @param charSequence the CharSequence to check
     * @return true if the given CharSequence is not empty, false otherwise
     */
    public static boolean isNotEmpty(final CharSequence charSequence) {
        return !isEmpty(charSequence);
    }

    /**
     * Checks whether two char arrays are equal.
     *
     * @param firstArray  the first byte array
     * @param secondArray the second byte array
     * @return true if the two byte arrays are equal, false otherwise
     */
    public static boolean areCharArraysEqual(char[] firstArray, char[] secondArray) {
        return Arrays.equals(firstArray, secondArray);
    }

    /**
     * Checks whether two byte arrays are equal.
     *
     * @param firstArray  the first byte array
     * @param secondArray the second byte array
     * @return true if the two byte arrays are equal, false otherwise
     */
    public static boolean areByteArraysEqual(byte[] firstArray, byte[] secondArray) {
        return Arrays.equals(firstArray, secondArray);
    }

    public static JSONObject createLoginParams(String clientCode, String password, String totp) {
        // Create JSON params object needed to be sent to api.

        JSONObject params = new JSONObject();
        params.put(SMART_CONNECT_CLIENT_CODE, clientCode);
        params.put(SMART_CONNECT_PASSWORD, password);
        params.put(SMART_CONNECT_TOTP, totp);
        return params;
    }

    public static String sha256Hex(String str) {
        byte[] a = DigestUtils.sha256(str);
        StringBuilder sb = new StringBuilder(a.length * 2);
        for (byte b : a)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
