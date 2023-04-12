package com.angelbroking.smartapi.utils;

import org.apache.http.util.TextUtils;

import java.util.Arrays;

public class Utils {

    public static boolean isEmpty(final CharSequence cs) {
        return TextUtils.isEmpty(cs);
    }


    public static boolean isNotEmpty(final CharSequence cs) {
        return !isEmpty(cs);
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
