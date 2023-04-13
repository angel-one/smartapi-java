package com.angelbroking.smartapi.smartticker.ticker;

import com.angelbroking.smartapi.Routes;
import com.angelbroking.smartapi.http.exceptions.CustomException;
import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import com.angelbroking.smartapi.smartticker.SmartWebsocket;
import com.angelbroking.smartapi.utils.Constants;
import com.angelbroking.smartapi.utils.NaiveSSLContext;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
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
import java.util.zip.InflaterOutputStream;

public class SmartAPITicker {

    private final SmartApiTickerParams params;
    private final Routes routes = new Routes();
    private final String wsuri = routes.getWsuri();
    private OnTicks onTickerArrivalListener;
    private OnConnect onConnectedListener;
    private final OnError onErrorListener;
    private final WebSocket ws;
    private final SSLContext context;
    private final SmartApiTickerScheduler scheduler;

    private static final Logger logger = LoggerFactory.getLogger(SmartAPITicker.class);


    /**
     * Initialize SmartAPITicker.
     */
    public SmartAPITicker(SmartApiTickerParams params, OnTicks onTickerArrivalListener,
                          OnConnect onConnectedListener, OnError onErrorListener) throws NoSuchAlgorithmException {
        this.params = params;
        this.onTickerArrivalListener = onTickerArrivalListener;
        this.onConnectedListener = onConnectedListener;
        this.onErrorListener = onErrorListener;
        try {
            context = NaiveSSLContext.getInstance("TLS");
            ws = new WebSocketFactory().setSSLContext(context).setVerifyHostname(false).createSocket(wsuri);
        } catch (IOException e) {
            if (onErrorListener != null) {
                onErrorListener.onError(e);
            }
            throw new CustomException("Could not create WebSocket instance.", e);
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage());
            throw new NoSuchAlgorithmException("Could not create SSL context.", e);
        }
        ws.addListener(getWebsocketAdapter());
        scheduler = new SmartApiTickerScheduler();
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
     * Set listener for listening to ticks.
     *
     * @param onTickerArrivalListener is listener which listens for each tick.
     */
    public void setOnTickerArrivalListener(OnTicks onTickerArrivalListener) {
        this.onTickerArrivalListener = onTickerArrivalListener;
    }

    /**
     * Set listener for on connection established.
     *
     * @param listener is used to listen to onConnected event.
     */
    public void setOnConnectedListener(OnConnect listener) {
        onConnectedListener = listener;
    }

    /**
     * Returns a WebSocketAdapter to listen to ticker related events.
     */
    public WebSocketAdapter getWebsocketAdapter() {
        return new WebSocketAdapter() {

            @Override
            public void onConnected(WebSocket websocket, Map<String, List<String>> headers) {
                // Send a text frame.

                ws.sendText(createWsCNJSONRequest().toString());
                onConnectedListener.onConnected();

                Runnable runnable = () -> ws.sendText(createWsMWJSONRequest().toString());
                ScheduledExecutorService service = Executors
                        .newSingleThreadScheduledExecutor();

                service.scheduleAtFixedRate(runnable, 0, 1, TimeUnit.MINUTES);

            }

            private JSONObject createWsCNJSONRequest() {
                JSONObject wsCNJSONRequest = new JSONObject();
                wsCNJSONRequest.put(Constants.SMART_API_TICKER_TASK, "cn");
                wsCNJSONRequest.put(Constants.SMART_API_TICKER_CHANNEL, "");
                wsCNJSONRequest.put(Constants.SMART_API_TICKER_CHANNEL, params.getFeedToken());
                wsCNJSONRequest.put(Constants.SMART_API_TICKER_USER, params.getClientId() );
                wsCNJSONRequest.put(Constants.SMART_API_TICKER_ACCTID, params.getClientId() );
                return wsCNJSONRequest;
            }

            private JSONObject createWsMWJSONRequest() {
                JSONObject wsMWJSONRequest = new JSONObject();
                wsMWJSONRequest.put(Constants.SMART_API_TICKER_TASK, "hb");
                wsMWJSONRequest.put(Constants.SMART_API_TICKER_CHANNEL, "");
                wsMWJSONRequest.put(Constants.SMART_API_TICKER_TOKEN,params.getFeedToken() );
                wsMWJSONRequest.put(Constants.SMART_API_TICKER_USER,params.getClientId() );
                wsMWJSONRequest.put(Constants.SMART_API_TICKER_ACCTID,params.getClientId() );
                return wsMWJSONRequest;
            }

            @Override
            public void onTextMessage(WebSocket websocket, String message) throws IOException {
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
        };
    }


    /**
     * Returns true if websocket connection is open.
     *
     * @return boolean
     */
    public boolean isConnectionOpen() {
        return ws != null && ws.isOpen();
    }

    /**
     * Subscribes script.
     */
    public void subscribe() {

        if (ws != null) {
            if (ws.isOpen()) {
                ws.sendText(createWsMWJSONRequest().toString());
            } else {
                if (onErrorListener != null) {
                    onErrorListener.onError(new SmartAPIException(Constants.TICKER_NOT_CONNECTED, String.valueOf(HttpStatus.SC_GATEWAY_TIMEOUT)));
                }
            }
        } else {
            if (onErrorListener != null) {
                onErrorListener.onError(new SmartAPIException(Constants.TICKER_NOT_CONNECTED, String.valueOf(HttpStatus.SC_GATEWAY_TIMEOUT)));
            }
        }
    }

    private JSONObject createWsMWJSONRequest() {
        JSONObject wsMWJSONRequest = new JSONObject();
        wsMWJSONRequest.put("task", this.params.getTask());
        wsMWJSONRequest.put("channel", this.params.getScript());
        wsMWJSONRequest.put("token", this.params.getFeedToken());
        wsMWJSONRequest.put("user", this.params.getClientId() );
        wsMWJSONRequest.put("acctid", this.params.getClientId());
        return wsMWJSONRequest;
    }

    public void connect() {
        try {
            ws.connect();
        } catch (WebSocketException e) {
            logger.error(e.getMessage());
        }

    }

}
