package com.angelbroking.smartapi.utils;

import java.util.Arrays;

public class Utils {
    private Utils() {

    }

    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static boolean isNotEmpty(final CharSequence cs) {
        return !isEmpty(cs);
    }

    public static boolean areCharArraysEqual(char[] array1, char[] array2) {
        if (array1 == null && array2 == null) {
            return true;
        }

        if (array1 != null && array2 != null) {
            return Arrays.equals(array1, array2);
        }

        return false;
    }


    public static boolean areByteArraysEqual(byte[] array1, byte[] array2) {
        if (array1 == null && array2 == null) {
            return true;
        }

        if (array1 != null && array2 != null) {
            return Arrays.equals(array1, array2);
        }

        return false;
    }


}
