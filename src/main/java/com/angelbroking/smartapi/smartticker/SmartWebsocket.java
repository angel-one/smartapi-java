package com.angelbroking.smartapi.smartticker;

import com.angelbroking.smartapi.dto.WsMWJSONRequestDTO;
import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import com.angelbroking.smartapi.routes.Routes;
import com.angelbroking.smartapi.utils.Constants;
import com.angelbroking.smartapi.utils.NaiveSSLContext;
import com.google.gson.Gson;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.json.JSONArray;

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
import java.util.zip.InflaterOutputStream;

import static com.angelbroking.smartapi.utils.Utils.validateInputNotNullCheck;

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
@Slf4j
public class SmartWebsocket {

    private final String clientId;
    private final String jwtToken;
    private final String apiKey;
    private final String actionType;
    private final String feedType;

    SmartWSOnTicks onTickerArrivalListener;
    SmartWSOnConnect onConnectedListener;
    SmartWSOnDisconnect onDisconnectedListener;
    SmartWSOnError onErrorListener;
    WebSocket webSocket;

    /**
     * Initialize SmartAPITicker.
     */
    public SmartWebsocket(String clientId, String jwtToken, String apiKey, String actionType, String feedType) {

        this.clientId = clientId;
        this.jwtToken = jwtToken;
        this.apiKey = apiKey;
        this.actionType = actionType;
        this.feedType = feedType;
        Routes routes = new Routes();
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(routes.getSWsuri()).append(Constants.QUESTION_MARK).append(Constants.JWT_TOKEN_PARAM).append(Constants.EQUALS).append(this.jwtToken).append(Constants.AMPERSAND).append(Constants.AMPERSAND).append(Constants.CLIENT_CODE_PARAM).append(Constants.EQUALS).append(this.clientId).append(Constants.AMPERSAND).append(Constants.AMPERSAND).append(Constants.API_KEY_PARAM).append(Constants.EQUALS).append(this.apiKey);
            SSLContext context = NaiveSSLContext.getInstance("TLS");
            webSocket = new WebSocketFactory().setSSLContext(context).setVerifyHostname(false).createSocket(sb.toString());

        } catch (IOException e) {
            if (validateInputNotNullCheck(onErrorListener)) {
                onErrorListener.onError(e);
            }
        } catch (NoSuchAlgorithmException e) {
            log.error(e.getMessage());
        }

        WebSocketAdapter adapter = getWebsocketAdapter();
        if (validateInputNotNullCheck(adapter)) {
            webSocket.addListener(adapter);
        }
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
            public void onConnected(WebSocket websocket, Map<String, List<String>> headers) {
                onConnectedListener.onConnected();
                Runnable runnable = () -> webSocket.sendText(new Gson().toJson(new WsMWJSONRequestDTO(actionType, feedType, jwtToken, clientId, apiKey)));
                ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
                service.scheduleAtFixedRate(runnable, 0, 1, TimeUnit.MINUTES);

            }

            @Override
            public void onTextMessage(WebSocket websocket, String message) throws IOException {
                byte[] decoded = Base64.getDecoder().decode(message);
                byte[] result = decompress(decoded);
                String str = new String(result, StandardCharsets.UTF_8);

                JSONArray tickerData = new JSONArray(str);

                if (validateInputNotNullCheck(onTickerArrivalListener)) {
                    onTickerArrivalListener.onTicks(tickerData);
                }
            }

            @Override
            public void onBinaryMessage(WebSocket websocket, byte[] binary) {
                try {
                    super.onBinaryMessage(websocket, binary);
                } catch (Exception e) {
                    log.error(e.getMessage());
                    if (validateInputNotNullCheck(onErrorListener)) {
                        onErrorListener.onError(e);
                    }
                }
            }

            /**
             * Callback method that is called when the WebSocket is disconnected.
             *
             * @param websocket          the WebSocket that was disconnected
             * @param serverCloseFrame   the WebSocket frame sent by the server to close the connection
             * @param clientCloseFrame   the WebSocket frame sent by the client to close the connection
             * @param closedByServer     a boolean indicating whether the connection was closed by the server
             * @throws NullPointerException if the onDisconnectedListener is null
             */
            @Override
            public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) {
                if (validateInputNotNullCheck(onDisconnectedListener)) {
                    onDisconnectedListener.onDisconnected();
                }
            }

            @Override
            public void onError(WebSocket websocket, WebSocketException cause) {
                try {
                    super.onError(websocket, cause);
                } catch (Exception e) {
                    log.error(e.getMessage());
                    if (validateInputNotNullCheck(onErrorListener)) {
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

        if (validateInputNotNullCheck(webSocket) && webSocket.isOpen()) {
            webSocket.disconnect();
        }
    }

    /**
     * Returns true if websocket connection is open.
     *
     * @return boolean
     */
    public boolean isConnectionOpen() {
        return validateInputNotNullCheck(webSocket) && webSocket.isOpen();
    }

    /**
     * Subscribes script.
     */
    public void runscript() {

        if (validateInputNotNullCheck(webSocket)) {
            if (webSocket.isOpen()) {
                webSocket.sendText(new Gson().toJson(new WsMWJSONRequestDTO(this.actionType, this.feedType, this.jwtToken, this.clientId, this.apiKey)));

            } else {
                if (validateInputNotNullCheck(onErrorListener)) {
                    onErrorListener.onError(new SmartAPIException("ticker is not connected", String.valueOf(HttpStatus.SC_GATEWAY_TIMEOUT)));
                }
            }
        } else {
            if (validateInputNotNullCheck(onErrorListener)) {
                onErrorListener.onError(new SmartAPIException("ticker is null not connected", String.valueOf(HttpStatus.SC_GATEWAY_TIMEOUT)));
            }
        }
    }

    public void connect() {
        try {
            webSocket.connect();
        } catch (WebSocketException e) {
            log.error(e.getMessage());
        }

    }

}
