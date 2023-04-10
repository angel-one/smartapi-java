package com.angelbroking.smartapi.http;

import com.angelbroking.smartapi.SmartConnect;
import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import com.angelbroking.smartapi.utils.Constants;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Request handler for all Http requests
 */

public class SmartAPIRequestHandler {

    private static final Logger logger = LoggerFactory.getLogger(SmartAPIRequestHandler.class);
    private static final String USER_AGENT = "javasmartapiconnect/3.0.0";
    private final OkHttpClient client;
    JSONObject apiheader = apiHeaders();

    /**
     * Initialize request handler.
     *
     * @param proxy to be set for making requests.
     */
    public SmartAPIRequestHandler(Proxy proxy) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(10000, TimeUnit.MILLISECONDS);
        if (proxy != null) {
            builder.proxy(proxy);
        }

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        if (SmartConnect.enableLogging) {
            client = builder.addInterceptor(logging).build();
        } else {
            client = builder.build();
        }
    }

    public JSONObject apiHeaders() {

        try {
            JSONObject headers = new JSONObject();

            // Local IP Address
            InetAddress localHost = InetAddress.getLocalHost();
            String clientLocalIP = localHost.getHostAddress();
            headers.put(Constants.HEADER_CLIENT_LOCAL_IP, clientLocalIP);

            // Public IP Address
            String clientPublicIP = getPublicIPAddress();
            headers.put(Constants.HEADER_CLIENT_PUBLIC_IP, clientPublicIP);
            String macAddress = null;
            // MAC Address
            // get all network interfaces of the current system
            Enumeration<NetworkInterface> networkInterface = NetworkInterface.getNetworkInterfaces();
            // iterate over all interfaces
            while (networkInterface.hasMoreElements()) {
                // get an interface
                NetworkInterface network = networkInterface.nextElement();
                // get its hardware or mac address
                byte[] macAddressBytes = network.getHardwareAddress();
                if (macAddressBytes != null) {
                    // initialize a string builder to hold mac address
                    StringBuilder macAddressStr = new StringBuilder();
                    // iterate over the bytes of mac address
                    for (int i = 0; i < macAddressBytes.length; i++) {
                        // convert byte to string in hexadecimal form
                        macAddressStr.append(String.format("%02X%s", macAddressBytes[i], (i < macAddressBytes.length - 1) ? "-" : ""));
                    }

                    macAddress = macAddressStr.toString();
                    if (macAddress != null) {
                        break;
                    }
                }
            }
            headers.put(Constants.MAC_ADDRESS, macAddress);
            String accept = "application/json";
            headers.put(Constants.ACCEPT, accept);
            String userType = "USER";
            headers.put(Constants.USER_TYPE, userType);
            String sourceID = "WEB";
            headers.put(Constants.SOURCE_ID, sourceID);

            logger.debug("Headers {}", headers);

            return headers;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }

    }

    private String getPublicIPAddress() throws MalformedURLException {
        String clientPublicIP = null;
        URL urlName = new URL("http://checkip.amazonaws.com");
        try (BufferedReader sc = new BufferedReader(new InputStreamReader(urlName.openStream()))) {
            clientPublicIP = sc.readLine().trim();
        } catch (IOException e) {
            logger.error("Error reading public IP address: {}", e.getMessage());
        }
        return clientPublicIP;
    }

    /**
     * Sends a POST request to the specified URL with the provided parameters and API key.
     *
     * @param apiKey the API key to use for the request
     * @param url    the URL to send the request to
     * @param params the parameters to include in the request body
     * @return a JSONObject containing the response from the server
     * @throws IOException       if there is an I/O error while sending the request or receiving the response
     * @throws JSONException     if there is an error while parsing the response as a JSON object
     * @throws SmartAPIException if the server returns an error response
     */
    public JSONObject postRequest(String apiKey, String url, JSONObject params) throws IOException, JSONException, SmartAPIException {

        Request request = createPostRequest(apiKey, url, params);
        Response response = client.newCall(request).execute();
        String body = response.body().string();
        logger.debug("Body {}", body);
        return new SmartAPIResponseHandler().handle(response, body);

    }

    /**
     * Makes a POST request.
     *
     * @param url         is the endpoint to which request has to be sent.
     * @param apiKey      is the api key of the Smart API Connect app.
     * @param accessToken is the access token obtained after successful login
     *                    process.
     * @param params      is the map of params which has to be sent in the body.
     * @return JSONObject which is received by Smart API Trade.
     * @throws IOException       is thrown when there is a connection related error.
     * @throws SmartAPIException is thrown for all Smart API Trade related errors.
     * @throws JSONException     is thrown for parsing errors.
     */
    public JSONObject postRequest(String apiKey, String url, JSONObject params, String accessToken) throws IOException, SmartAPIException, JSONException {
        Request request = createPostRequest(apiKey, url, params, accessToken);
        Response response = client.newCall(request).execute();
        String body = response.body().string();
        return new SmartAPIResponseHandler().handle(response, body);
    }

    /**
     * Make a JSON POST request.
     *
     * @param url         is the endpoint to which request has to be sent.
     * @param apiKey      is the api key of the Smart API Connect app.
     * @param accessToken is the access token obtained after successful login
     *                    process.
     * @param jsonArray   is the JSON array of params which has to be sent in the
     *                    body.
     * @throws IOException       is thrown when there is a connection related error.
     * @throws SmartAPIException is thrown for all Smart API Trade related errors.
     * @throws JSONException     is thrown for parsing errors.
     */
    public JSONObject postRequestJSON(String url, JSONArray jsonArray, String apiKey, String accessToken) throws IOException, SmartAPIException, JSONException {
        Request request = createJsonPostRequest(url, jsonArray, apiKey, accessToken);
        Response response = client.newCall(request).execute();
        String body = response.body().string();
        return new SmartAPIResponseHandler().handle(response, body);
    }

    /**
     * Makes a PUT request.
     *
     * @param url         is the endpoint to which request has to be sent.
     * @param apiKey      is the api key of the Smart API Connect app.
     * @param accessToken is the access token obtained after successful login
     *                    process.
     * @param params      is the map of params which has to be sent in the body.
     * @return JSONObject which is received by Smart API Trade.
     * @throws IOException       is thrown when there is a connection related error.
     * @throws SmartAPIException is thrown for all Smart API Trade related errors.
     * @throws JSONException     is thrown for parsing errors.
     */
    public JSONObject putRequest(String url, Map<String, Object> params, String apiKey, String accessToken) throws IOException, SmartAPIException, JSONException {
        Request request = createPutRequest(url, params, apiKey, accessToken);
        Response response = client.newCall(request).execute();
        String body = response.body().string();
        return new SmartAPIResponseHandler().handle(response, body);
    }

    /**
     * Makes a DELETE request.
     *
     * @param url         is the endpoint to which request has to be sent.
     * @param apiKey      is the api key of the Smart API Connect app.
     * @param accessToken is the access token obtained after successful login
     *                    process.
     * @param params      is the map of params which has to be sent in the query
     *                    params.
     * @return JSONObject which is received by Smart API Trade.
     * @throws IOException       is thrown when there is a connection related error.
     * @throws SmartAPIException is thrown for all Smart API Trade related errors.
     * @throws JSONException     is thrown for parsing errors.
     */
    public JSONObject deleteRequest(String url, Map<String, Object> params, String apiKey, String accessToken) throws IOException, SmartAPIException, JSONException {
        Request request = createDeleteRequest(url, params, apiKey, accessToken);
        Response response = client.newCall(request).execute();
        String body = response.body().string();
        return new SmartAPIResponseHandler().handle(response, body);
    }

    /**
     * Makes a GET request to a SmartAPI service endpoint and returns the response as a JSON object.
     *
     * @param apiKey      the API key for the SmartAPI service
     * @param url         the URL of the SmartAPI service endpoint to which the GET request is made
     * @param accessToken the access token for the SmartAPI service
     * @return the response from the SmartAPI service as a JSON object
     * @throws IOException       if there is an I/O error while making the request
     * @throws SmartAPIException if the SmartAPI service returns an error
     * @throws JSONException     if there is an error while processing the JSON response from the SmartAPI service
     */

    public JSONObject getRequest(String apiKey, String url, String accessToken) throws IOException, SmartAPIException, JSONException {
        Request request = createGetRequest(apiKey, url, accessToken);
        Response response = client.newCall(request).execute();
        String body = response.body().string();
        return new SmartAPIResponseHandler().handle(response, body);
    }

    /**
     * Creates a GET request.
     *
     * @param url         is the endpoint to which request has to be done.
     * @param apiKey      is the api key of the Smart API Connect app.
     * @param accessToken is the access token obtained after successful login
     *                    process.
     * @throws IOException
     */
    public Request createGetRequest(String apiKey, String url, String accessToken) {

        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();

        String privateKey = apiKey;

        return new Request.Builder().url(httpBuilder.build()).header(Constants.USER_AGENT, USER_AGENT).header(Constants.AUTHORIZATION, "Bearer " + accessToken).header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON).header(Constants.CLIENT_LOCAL_IP, apiheader.getString(Constants.HEADER_CLIENT_LOCAL_IP)).header(Constants.CLIENT_PUBLIC_IP, apiheader.getString(Constants.HEADER_CLIENT_PUBLIC_IP)).header(Constants.X_MAC_ADDRESS, apiheader.getString(Constants.MAC_ADDRESS)).header(Constants.ACCEPT, apiheader.getString(Constants.ACCEPT)).header(Constants.PRIVATE_KEY, privateKey).header(Constants.X_USER_TYPE, apiheader.getString(Constants.USER_TYPE)).header(Constants.X_SOURCE_ID, apiheader.getString(Constants.SOURCE_ID)).build();
    }

    /**
     * Creates a GET request.
     *
     * @param url         is the endpoint to which request has to be done.
     * @param apiKey      is the api key of the Smart API Connect app.
     * @param accessToken is the access token obtained after successful login
     *                    process.
     * @param commonKey   is the key that has to be sent in query param for quote
     *                    calls.
     * @param values      is the values that has to be sent in query param like 265,
     *                    256265, NSE:INFY.
     */
    public Request createGetRequest(String url, String commonKey, String[] values, String apiKey, String accessToken) {
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
        for (int i = 0; i < values.length; i++) {
            httpBuilder.addQueryParameter(commonKey, values[i]);
        }
        return new Request.Builder().url(httpBuilder.build()).header(Constants.USER_AGENT, USER_AGENT).header(Constants.SMART_API_VERSION, "3").header(Constants.AUTHORIZATION, Constants.TOKEN + apiKey + ":" + accessToken).build();
    }

    /**
     * Creates a POST request with the specified API key, URL and parameters in JSON format.
     *
     * @param apiKey The API key to use for the request.
     * @param url    The URL to send the request to.
     * @param params The JSON object containing the parameters for the request.
     * @return A Request object representing the POST request, with the specified API key, URL and parameters.
     * @throws Exception if there was an error creating the request.
     */
    public Request createPostRequest(String apiKey, String url, JSONObject params) {
        try {

            MediaType jsonMediaType = MediaType.parse(Constants.APPLICATION_JSON_UTF8);
            RequestBody body = RequestBody.create(params.toString(), jsonMediaType);

            String privateKey = apiKey;
            return new Request.Builder().url(url).post(body).header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON).header(Constants.CLIENT_LOCAL_IP, apiheader.getString(Constants.HEADER_CLIENT_LOCAL_IP)).header(Constants.CLIENT_PUBLIC_IP, apiheader.getString(Constants.HEADER_CLIENT_PUBLIC_IP)).header(Constants.X_MAC_ADDRESS, apiheader.getString(Constants.MAC_ADDRESS)).header(Constants.ACCEPT, apiheader.getString(Constants.ACCEPT)).header(Constants.PRIVATE_KEY, privateKey).header(Constants.X_USER_TYPE, apiheader.getString(Constants.USER_TYPE)).header(Constants.X_SOURCE_ID, apiheader.getString(Constants.SOURCE_ID)).build();
        } catch (Exception e) {
            logger.error("exception createPostRequest");
            logger.error(e.getMessage());
            return null;
        }
    }

    /**
     * Creates a POST request.
     *
     * @param url         is the endpoint to which request has to be done.
     * @param apiKey      is the api key of the Smart API Connect app.
     * @param accessToken is the access token obtained after successful login
     *                    process.
     * @param params      is the map of data that has to be sent in the body.
     */
    public Request createPostRequest(String apiKey, String url, JSONObject params, String accessToken) {
        try {

            MediaType jsonMediaType = MediaType.parse(Constants.APPLICATION_JSON_UTF8);
            RequestBody body = RequestBody.create(params.toString(), jsonMediaType);

            String privateKey = apiKey;
            return new Request.Builder().url(url).post(body).header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON).header(Constants.AUTHORIZATION, "Bearer " + accessToken).header(Constants.CLIENT_LOCAL_IP, apiheader.getString(Constants.HEADER_CLIENT_LOCAL_IP)).header(Constants.CLIENT_PUBLIC_IP, apiheader.getString(Constants.HEADER_CLIENT_PUBLIC_IP)).header(Constants.X_MAC_ADDRESS, apiheader.getString(Constants.MAC_ADDRESS)).header(Constants.ACCEPT, apiheader.getString(Constants.ACCEPT)).header(Constants.PRIVATE_KEY, privateKey).header(Constants.X_USER_TYPE, apiheader.getString(Constants.USER_TYPE)).header(Constants.X_SOURCE_ID, apiheader.getString(Constants.SOURCE_ID)).build();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    /**
     * Create a POST request with body type JSON.
     *
     * @param url         is the endpoint to which request has to be done.
     * @param apiKey      is the api key of the Smart API Connect app.
     * @param accessToken is the access token obtained after successful login
     *                    process.
     * @param jsonArray   is the JSONArray of data that has to be sent in the body.
     */
    public Request createJsonPostRequest(String url, JSONArray jsonArray, String apiKey, String accessToken) {
        MediaType jsonMediaType = MediaType.parse(Constants.APPLICATION_JSON_UTF8);
        RequestBody body = RequestBody.create(jsonArray.toString(), jsonMediaType);
        return new Request.Builder().url(url).header(Constants.USER_AGENT, USER_AGENT).header(Constants.SMART_API_VERSION, "3").header(Constants.AUTHORIZATION, Constants.TOKEN + apiKey + ":" + accessToken).post(body).build();
    }

    /**
     * Creates a PUT request.
     *
     * @param url         is the endpoint to which request has to be done.
     * @param apiKey      is the api key of the Smart API Connect app.
     * @param accessToken is the access token obtained after successful login
     *                    process.
     * @param params      is the map of data that has to be sent in the body.
     */
    public Request createPutRequest(String url, Map<String, Object> params, String apiKey, String accessToken) {
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            builder.add(entry.getKey(), entry.getValue().toString());
        }
        RequestBody requestBody = builder.build();
        return new Request.Builder().url(url).put(requestBody).header(Constants.USER_AGENT, USER_AGENT).header(Constants.SMART_API_VERSION, "3").header(Constants.AUTHORIZATION, Constants.TOKEN + apiKey + ":" + accessToken).build();
    }

    /**
     * Creates a DELETE request.
     *
     * @param url         is the endpoint to which request has to be done.
     * @param apiKey      is the api key of the Smart API Connect app.
     * @param accessToken is the access token obtained after successful login
     *                    process.
     * @param params      is the map of data that has to be sent in the query
     *                    params.
     */
    public Request createDeleteRequest(String url, Map<String, Object> params, String apiKey, String accessToken) {
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            httpBuilder.addQueryParameter(entry.getKey(), entry.getValue().toString());
        }

        return new Request.Builder().url(httpBuilder.build()).delete().header(Constants.USER_AGENT, USER_AGENT).header(Constants.SMART_API_VERSION, "3").header(Constants.AUTHORIZATION, Constants.TOKEN + apiKey + ":" + accessToken).build();
    }

}
