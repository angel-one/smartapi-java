package com.angelbroking.smartapi.smartticker;

import com.angelbroking.smartapi.Routes;
import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import com.angelbroking.smartapi.utils.Constants;
import com.angelbroking.smartapi.utils.NaiveSSLContext;
import com.neovisionaries.ws.client.*;
import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.zip.DataFormatException;
import java.util.zip.InflaterOutputStream;

/**
 * The `SmartWebsocket` class provides a websocket client for connecting to a financial data feed provided by SmartAPI.
 * It allows for receiving real-time market data and provides event listeners for various websocket events, such as
 * when a connection is established or closed, and when new ticker data arrives.
 * <p>
 * To use this class, create an instance of `SmartWebsocket` and pass in the required parameters: `clientId`, `jwtToken`,
 * `apiKey`, `actionType`, and `feedType`. You can then register event listeners using the `setOn...Listener` methods.
 * Finally, call the `connect` method to initiate the websocket connection.
 * <p>
 * The `SmartWebsocket` class also provides a method `disconnect` to close the websocket connection.
 */
public class SmartWebsocket {

    private static final Logger logger = LoggerFactory.getLogger(SmartWebsocket.class);
    private final String clientId;
    private final String jwtToken;
    private final String apiKey;
    private final String actionType;
    private final String feedType;
    private final Routes routes = new Routes();
    private SmartWSOnTicks onTickerArrivalListener;
    private SmartWSOnConnect onConnectedListener;
    private SmartWSOnDisconnect onDisconnectedListener;
    private SmartWSOnError onErrorListener;
    private WebSocket webSocket;

    /**
     * Initialize SmartAPITicker.
     */
    public SmartWebsocket(String clientId, String jwtToken, String apiKey, String actionType, String feedType) {

        this.clientId = clientId;
        this.jwtToken = jwtToken;
        this.apiKey = apiKey;
        this.actionType = actionType;
        this.feedType = feedType;

        try {
            StringBuilder sb = new StringBuilder();
            sb.append(routes.getSWsuri())
                    .append("?jwttoken=")
                    .append(this.jwtToken)
                    .append("&&clientcode=")
                    .append(this.clientId)
                    .append("&&apikey=")
                    .append(this.apiKey);
            SSLContext context = NaiveSSLContext.getInstance("TLS");
            webSocket = new WebSocketFactory().setSSLContext(context).setVerifyHostname(false).createSocket(sb.toString());

        } catch (IOException e) {
            if (onErrorListener != null) {
                onErrorListener.onError(e);
            }
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage());
        }

        webSocket.addListener(getWebsocketAdapter());

    }

    public static byte[] decompress(byte[] compressedTxt) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try (OutputStream ios = new InflaterOutputStream(os)) {
            ios.write(compressedTxt);
        } finally {
            os.close();
        }

        return os.toByteArray();
    }

    /**
     * Set error listener.
     *
     * @param listener of type OnError which listens to all the type of errors that
     *                 may arise in SmartAPITicker class.
     */
    public void setOnErrorListener(SmartWSOnError listener) {
        onErrorListener = listener;
    }

    /**
     * Set listener for listening to ticks.
     *
     * @param onTickerArrivalListener is listener which listens for each tick.
     */
    public void setOnTickerArrivalListener(SmartWSOnTicks onTickerArrivalListener) {
        this.onTickerArrivalListener = onTickerArrivalListener;
    }

    /**
     * Set listener for on connection established.
     *
     * @param listener is used to listen to onConnected event.
     */
    public void setOnConnectedListener(SmartWSOnConnect listener) {
        onConnectedListener = listener;
    }

    /**
     * Set listener for on connection is disconnected.
     *
     * @param listener is used to listen to onDisconnected event.
     */
    public void setOnDisconnectedListener(SmartWSOnDisconnect listener) {
        onDisconnectedListener = listener;
    }

    /**
     * Returns a WebSocketAdapter to listen to ticker related events.
     */
    public WebSocketAdapter getWebsocketAdapter() {
        return new WebSocketAdapter() {

            @Override
            public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws WebSocketException {
                onConnectedListener.onConnected();
                Runnable runnable = () -> {
                    JSONObject wsMWJSONRequest = new JSONObject();
                    wsMWJSONRequest.put(Constants.ACTION_TYPE, actionType);
                    wsMWJSONRequest.put(Constants.FEEED_TYPE, feedType);
                    wsMWJSONRequest.put(Constants.JWT_TOKEN, jwtToken);
                    wsMWJSONRequest.put(Constants.CLIENT_CODE, clientId);
                    wsMWJSONRequest.put(Constants.API_KEY, apiKey);
                    webSocket.sendText(wsMWJSONRequest.toString());
                };
                ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
                service.scheduleAtFixedRate(runnable, 0, 1, TimeUnit.MINUTES);

            }

            @Override
            public void onTextMessage(WebSocket websocket, String message) throws IOException, DataFormatException {
                byte[] decoded = Base64.getDecoder().decode(message);
                byte[] result = decompress(decoded);
                String str = new String(result, StandardCharsets.UTF_8);

                JSONArray tickerData = new JSONArray(str);

                if (onTickerArrivalListener != null) {
                    onTickerArrivalListener.onTicks(tickerData);
                }
            }

            @Override
            public void onBinaryMessage(WebSocket websocket, byte[] binary) {
                try {
                    super.onBinaryMessage(websocket, binary);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                    if (onErrorListener != null) {
                        onErrorListener.onError(e);
                    }
                }
            }

            /**
             * On disconnection, return statement ensures that the thread ends.
             *
             * @param websocket
             * @param serverCloseFrame
             * @param clientCloseFrame
             * @param closedByServer
             * @throws Exception
             */
            @Override
            public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame,
                                       WebSocketFrame clientCloseFrame, boolean closedByServer) {
                if (onDisconnectedListener != null) {
                    onDisconnectedListener.onDisconnected();
                }
            }

            @Override
            public void onError(WebSocket websocket, WebSocketException cause) {
                try {
                    super.onError(websocket, cause);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                    if (onErrorListener != null) {
                        onErrorListener.onError(e);
                    }
                }
            }

        };
    }

    /**
     * Disconnects websocket connection.
     */
    public void disconnect() {

        if (webSocket != null && webSocket.isOpen()) {
            webSocket.disconnect();
        }
    }

    /**
     * Returns true if websocket connection is open.
     *
     * @return boolean
     */
    public boolean isConnectionOpen() {
        return webSocket != null && webSocket.isOpen();
    }

    /**
     * Subscribes script.
     */
    public void runscript() {

        if (webSocket != null) {
            if (webSocket.isOpen()) {

                JSONObject wsMWJSONRequest = new JSONObject();
                wsMWJSONRequest.put("actiontype", this.actionType);
                wsMWJSONRequest.put("feedtype", this.feedType);
                wsMWJSONRequest.put("jwttoken", this.jwtToken);
                wsMWJSONRequest.put("clientcode", this.clientId);
                wsMWJSONRequest.put("apikey", this.apiKey);

                webSocket.sendText(wsMWJSONRequest.toString());

            } else {
                if (onErrorListener != null) {
                    onErrorListener.onError(new SmartAPIException("ticker is not connected", String.valueOf(HttpStatus.SC_GATEWAY_TIMEOUT)));
                }
            }
        } else {
            if (onErrorListener != null) {
                onErrorListener.onError(new SmartAPIException("ticker is null not connected", String.valueOf(HttpStatus.SC_GATEWAY_TIMEOUT)));
            }
        }
    }

    public void connect() {
        try {
            webSocket.connect();
        } catch (WebSocketException e) {
            logger.error(e.getMessage());
        }

    }

}