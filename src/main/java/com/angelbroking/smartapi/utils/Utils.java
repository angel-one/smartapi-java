package com.angelbroking.smartapi.utils;

import org.apache.http.util.TextUtils;

import java.util.Arrays;

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
}
