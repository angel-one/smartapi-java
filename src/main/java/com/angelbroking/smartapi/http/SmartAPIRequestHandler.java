package com.angelbroking.smartapi.http;

import com.angelbroking.smartapi.http.exceptions.APIRequestCreationException;
import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import com.angelbroking.smartapi.http.exceptions.SmartConnectException;
import com.angelbroking.smartapi.http.response.HttpResponse;
import com.angelbroking.smartapi.models.ApiHeaders;
import com.angelbroking.smartapi.models.SmartConnectParams;
import com.angelbroking.smartapi.utils.Constants;
import com.angelbroking.smartapi.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Proxy;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.angelbroking.smartapi.utils.Constants.ACCEPT;
import static com.angelbroking.smartapi.utils.Constants.API_REQUEST_FAILED_MSG;
import static com.angelbroking.smartapi.utils.Constants.APPLICATION_JSON;
import static com.angelbroking.smartapi.utils.Constants.APPLICATION_JSON_UTF8;
import static com.angelbroking.smartapi.utils.Constants.AUTHORIZATION;
import static com.angelbroking.smartapi.utils.Constants.CLIENT_LOCAL_IP;
import static com.angelbroking.smartapi.utils.Constants.CLIENT_PUBLIC_IP;
import static com.angelbroking.smartapi.utils.Constants.CONTENT_TYPE;
import static com.angelbroking.smartapi.utils.Constants.INVALID_URL;
import static com.angelbroking.smartapi.utils.Constants.NULL_URL_MESSAGE;
import static com.angelbroking.smartapi.utils.Constants.PRIVATE_KEY;
import static com.angelbroking.smartapi.utils.Constants.SMARTAPIREQUESTHANDLER_USER_AGENT;
import static com.angelbroking.smartapi.utils.Constants.SMART_API_VERSION;
import static com.angelbroking.smartapi.utils.Constants.TOKEN;
import static com.angelbroking.smartapi.utils.Constants.X_MAC_ADDRESS;
import static com.angelbroking.smartapi.utils.Constants.X_SOURCE_ID;
import static com.angelbroking.smartapi.utils.Constants.X_USER_TYPE;
import static com.angelbroking.smartapi.utils.Utils.getPublicIPAddress;
import static com.angelbroking.smartapi.utils.Utils.getResponseBody;
import static com.angelbroking.smartapi.utils.Utils.validateInputNotNullCheck;
import static com.angelbroking.smartapi.utils.Utils.validateInputNullCheck;

/**
 * Request handler for all Http requests
 */
@Slf4j
public class SmartAPIRequestHandler {

    private final OkHttpClient client;
    ApiHeaders apiheader = apiHeaders();

    public SmartAPIRequestHandler(Proxy proxy, long timeOutInMillis) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(timeOutInMillis, TimeUnit.MILLISECONDS);
        if (validateInputNotNullCheck(proxy)) {
            builder.proxy(proxy);
        }

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        if (SmartConnectParams.isEnableLogging()) {
            client = builder.addInterceptor(logging).build();
        } else {
            client = builder.build();
        }
    }

    public ApiHeaders apiHeaders() {
        try {

            ApiHeaders headers = new ApiHeaders();
            InetAddress localHost = InetAddress.getLocalHost();
            headers.setHeaderClientLocalIP(localHost.getHostAddress());
            headers.setHeaderClientPublicIP(getPublicIPAddress());
            headers.setMacAddress(Utils.getMacAddress());
            headers.setAccept("application/json");
            headers.setUserType("USER");
            headers.setSourceID("WEB");
            log.info("Headers {}", headers);
            return headers;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new SmartConnectException("Failed to generate API headers");
        }
    }



    /**
     * Sends a POST request to the specified URL with the provided parameters and API key.
     *
     * @param apiKey the API key to use for the request
     * @param url    the URL to send the request to
     * @param params the parameters to include in the request body
     * @return a HttpResponse containing the response from the server
     * @throws IOException       if there is an I/O error while sending the request or receiving the response
     * @throws JSONException     if there is an error while parsing the response as a JSON object
     * @throws SmartAPIException if the server returns an error response
     */
    public HttpResponse postRequest(String apiKey, String url, String params) throws IOException, JSONException, SmartAPIException {

        Request request = createPostRequest(apiKey, url, params);
        Response response = client.newCall(request).execute();
       String body = getResponseBody(response);
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
     * @return HttpResponse which is received by Smart API Trade.
     * @throws IOException       is thrown when there is a connection related error.
     * @throws SmartAPIException is thrown for all Smart API Trade related errors.
     * @throws JSONException     is thrown for parsing errors.
     */
    public HttpResponse postRequest(String apiKey, String url, String params, String accessToken) throws IOException, SmartAPIException, JSONException {
        Request request = createPostRequest(apiKey, url, params, accessToken);
        Response response = client.newCall(request).execute();
       String body = getResponseBody(response);
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
     * @throws JSONException     is thrown for parsing errors.
     */
    public HttpResponse postRequestJSON(String url, JSONArray jsonArray, String apiKey, String accessToken) throws IOException, JSONException, SmartAPIException {
        Request request = createJsonPostRequest(url, jsonArray, apiKey, accessToken);
        Response response = client.newCall(request).execute();
       String body = getResponseBody(response);
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
     * @return HttpResponse which is received by Smart API Trade.
     * @throws IOException       is thrown when there is a connection related error.
     * @throws JSONException     is thrown for parsing errors.
     */
    public HttpResponse putRequest(String url, Map<String, Object> params, String apiKey, String accessToken) throws IOException, JSONException, SmartAPIException {
        Request request = createPutRequest(url, params, apiKey, accessToken);
        Response response = client.newCall(request).execute();
       String body = getResponseBody(response);
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
     * @return HttpResponse which is received by Smart API Trade.
     * @throws IOException       is thrown when there is a connection related error.
     * @throws JSONException     is thrown for parsing errors.
     */
    public HttpResponse deleteRequest(String url, Map<String, Object> params, String apiKey, String accessToken) throws IOException, JSONException, SmartAPIException {
        Request request = createDeleteRequest(url, params, apiKey, accessToken);
        Response response = client.newCall(request).execute();
       String body = getResponseBody(response);
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

    public HttpResponse getRequest(String apiKey, String url, String accessToken) throws IOException, SmartAPIException, JSONException {
        Request request = createGetRequest(apiKey, url, accessToken);
        Response response = client.newCall(request).execute();
       String body = getResponseBody(response);
        return new SmartAPIResponseHandler().handle(response, body);
    }

    /**
     * Creates a GET request.
     *
     * @param url         is the endpoint to which request has to be done.
     * @param privateKey  is the api key of the Smart API Connect app.
     * @param accessToken is the access token obtained after successful login
     */
    public Request createGetRequest(String privateKey, String url, String accessToken) {

        if (validateInputNullCheck(url)) {
            throw new IllegalArgumentException(NULL_URL_MESSAGE);
        }
        HttpUrl httpUrl = HttpUrl.parse(url);
        if (validateInputNullCheck(httpUrl)) {
            throw new IllegalArgumentException(String.format("Invalid URL: %s", url));

        }
        HttpUrl.Builder httpBuilder = httpUrl.newBuilder();

        StringBuilder authHeader = new StringBuilder();
        authHeader.append("Bearer ");
        authHeader.append(accessToken);
        return new Request.Builder().url(httpBuilder.build()).header(
                SMARTAPIREQUESTHANDLER_USER_AGENT, SMARTAPIREQUESTHANDLER_USER_AGENT).header(AUTHORIZATION, authHeader.toString()).header(CONTENT_TYPE, APPLICATION_JSON).header(CLIENT_LOCAL_IP, apiheader.getHeaderClientLocalIP()).header(CLIENT_PUBLIC_IP, apiheader.getHeaderClientPublicIP()).header(X_MAC_ADDRESS, apiheader.getMacAddress()).header(ACCEPT, apiheader.getAccept()).header(PRIVATE_KEY, privateKey).header(X_USER_TYPE, apiheader.getUserType()).header(X_SOURCE_ID, apiheader.getSourceID()).build();
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
        if (validateInputNullCheck(url)) {
            throw new IllegalArgumentException(NULL_URL_MESSAGE);
        }
        HttpUrl httpUrl = HttpUrl.parse(url);
        if (validateInputNullCheck(httpUrl)) {
            throw new IllegalArgumentException(String.format("%s %s", INVALID_URL, url));
        }
        HttpUrl.Builder httpBuilder = httpUrl.newBuilder();
        for (String value : values) {
            httpBuilder.addQueryParameter(commonKey, value);
        }
        return new Request.Builder().url(httpBuilder.build()).header(SMARTAPIREQUESTHANDLER_USER_AGENT, SMARTAPIREQUESTHANDLER_USER_AGENT).header(SMART_API_VERSION, "3").header(AUTHORIZATION, String.format("%s%s:%s", TOKEN, apiKey, accessToken)).build();
    }

    /**
     * Creates a POST request with the specified API key, URL and parameters in JSON format.
     *
     * @param privateKey The API key to use for the request.
     * @param url        The URL to send the request to.
     * @param params     The JSON object containing the parameters for the request.
     * @return A Request object representing the POST request, with the specified API key, URL and parameters.
     */
    public Request createPostRequest(String privateKey, String url, String params) {
        try {

            MediaType jsonMediaType = MediaType.parse(APPLICATION_JSON_UTF8);
            RequestBody body = RequestBody.create(params, jsonMediaType);
            return new Request.Builder().url(url).post(body).header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON).header(Constants.CLIENT_LOCAL_IP, apiheader.getHeaderClientLocalIP()).header(Constants.CLIENT_PUBLIC_IP, apiheader.getHeaderClientPublicIP()).header(Constants.X_MAC_ADDRESS, apiheader.getMacAddress()).header(Constants.ACCEPT, apiheader.getAccept()).header(Constants.PRIVATE_KEY, privateKey).header(Constants.X_USER_TYPE, apiheader.getUserType()).header(Constants.X_SOURCE_ID, apiheader.getSourceID()).build();
        } catch (Exception e) {
            log.error("{} {}", API_REQUEST_FAILED_MSG, e.getMessage());
            throw new APIRequestCreationException(API_REQUEST_FAILED_MSG);
        }
    }

    /**
     * Creates a POST request.
     *
     * @param url         is the endpoint to which request has to be done.
     * @param privateKey  is the api key of the Smart API Connect app.
     * @param accessToken is the access token obtained after successful login
     *                    process.
     * @param params      is the map of data that has to be sent in the body.
     */
    public Request createPostRequest(String privateKey, String url, String params, String accessToken) {
        try {

            MediaType jsonMediaType = MediaType.parse(Constants.APPLICATION_JSON_UTF8);
            RequestBody body = RequestBody.create(params, jsonMediaType);
            return new Request.Builder().url(url).post(body).header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON).header(Constants.AUTHORIZATION, String.format("Bearer %s", accessToken)).header(Constants.CLIENT_LOCAL_IP, apiheader.getHeaderClientLocalIP()).header(Constants.CLIENT_PUBLIC_IP, apiheader.getHeaderClientPublicIP()).header(Constants.X_MAC_ADDRESS, apiheader.getMacAddress()).header(Constants.ACCEPT, apiheader.getAccept()).header(Constants.PRIVATE_KEY, privateKey).header(Constants.X_USER_TYPE, apiheader.getUserType()).header(Constants.X_SOURCE_ID, apiheader.getSourceID()).build();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new APIRequestCreationException(API_REQUEST_FAILED_MSG);
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
        return new Request.Builder().url(url).header(Constants.USER_AGENT, SMARTAPIREQUESTHANDLER_USER_AGENT).header(Constants.SMART_API_VERSION, "3").header(Constants.AUTHORIZATION, String.format("%s%s:%s", Constants.TOKEN, apiKey, accessToken)).post(body).build();
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
        return new Request.Builder().url(url).put(requestBody).header(Constants.USER_AGENT, SMARTAPIREQUESTHANDLER_USER_AGENT).header(Constants.SMART_API_VERSION, "3").header(Constants.AUTHORIZATION, String.format("%s%s:%s", Constants.TOKEN, apiKey, accessToken)).build();
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
        if (validateInputNullCheck(url)) {
            throw new IllegalArgumentException(NULL_URL_MESSAGE);
        }
        HttpUrl httpUrl = HttpUrl.parse(url);
        if (validateInputNullCheck(httpUrl)) {
            throw new IllegalArgumentException(String.format("%s %s", INVALID_URL, url));
        }
        HttpUrl.Builder httpBuilder = httpUrl.newBuilder();

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            httpBuilder.addQueryParameter(entry.getKey(), entry.getValue().toString());
        }

        return new Request.Builder().url(httpBuilder.build()).delete().header(Constants.USER_AGENT, SMARTAPIREQUESTHANDLER_USER_AGENT).header(Constants.SMART_API_VERSION, "3").header(Constants.AUTHORIZATION, String.format("%s%s:%s", Constants.TOKEN, apiKey, accessToken)).build();
    }


}
