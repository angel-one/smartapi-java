package com.angelbroking.smartapi.utils;

import com.angelbroking.smartapi.dto.SmartConnectAuthDTO;
import com.angelbroking.smartapi.http.exceptions.SmartConnectException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.util.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Properties;

import static com.angelbroking.smartapi.utils.Constants.HEX_FORMAT;
import static com.angelbroking.smartapi.utils.Constants.MAC_ADDRESS_FORMAT;
import static com.angelbroking.smartapi.utils.Constants.URL_PROPERTY_KEY;

@Slf4j
public class Utils {


    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Gson gson = new Gson();

    private Utils() {
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

    public static String createLoginParams(String clientCode, String password, String totp) {
        // Create JSON params object needed to be sent to api.

        return gson.toJson(new SmartConnectAuthDTO(clientCode, password, totp));


    }

    public static String sha256Hex(String str) {
        byte[] a = DigestUtils.sha256(str);
        StringBuilder sb = new StringBuilder(a.length * 2);
        for (byte b : a)
            sb.append(String.format(HEX_FORMAT, b));
        return sb.toString();
    }

    public static String getMacAddress() throws SmartConnectException {
        try {
            Enumeration<NetworkInterface> networkInterface = NetworkInterface.getNetworkInterfaces();
            while (networkInterface.hasMoreElements()) {
                NetworkInterface network = networkInterface.nextElement();
                byte[] macAddressBytes = network.getHardwareAddress();
                if (validateInputNotNullCheck(macAddressBytes)) {
                    StringBuilder macAddressStr = new StringBuilder();
                    for (int i = 0; i < macAddressBytes.length; i++) {
                        macAddressStr.append(String.format(MAC_ADDRESS_FORMAT, macAddressBytes[i], (i < macAddressBytes.length - 1) ? "-" : ""));
                    }
                    String macAddress = macAddressStr.toString();
                    if (validateInputNotNullCheck(macAddress)) {
                        return macAddress;
                    }
                }
            }
            throw new SmartConnectException("MAC address not found");
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new SmartConnectException("Failed to retrieve MAC address", e);
        }
    }
    public static String getResponseBody(Response response) throws IOException {
        String body = "";
        if (response.isSuccessful()) {
            ResponseBody responseBody = response.body();
            if (validateInputNotNullCheck(responseBody)) {
                body = responseBody.string();
            }
        }
        return body;
    }

    public static <T> boolean validateInputNullCheck(T input) {
        return input == null;
    }

    public static <T> boolean validateInputNotNullCheck(T input) {
        return input != null;
    }

    public static String getPublicIPAddress() throws IOException {
        String clientPublicIP;
        Properties properties = new Properties();
        try (InputStream input = Utils.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (validateInputNullCheck(input)) {
                throw new IOException("config.properties not found on the classpath");
            }
            properties.load(input);
        } catch (IOException e) {
            log.error("Error loading configuration file: {}", e.getMessage());
            throw new IOException("Failed to load configuration file");
        }
        URL urlName = new URL(properties.getProperty(URL_PROPERTY_KEY));
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlName.openStream()))) {
            clientPublicIP = bufferedReader.readLine().trim();
        } catch (IOException e) {
            log.error("Error reading public IP address: {}", e.getMessage());
            throw new IOException("Failed to get public ip address");
        }
        return clientPublicIP;
    }
}

