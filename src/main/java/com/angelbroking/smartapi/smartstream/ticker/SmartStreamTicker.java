package com.angelbroking.smartapi.smartstream.ticker;

import com.angelbroking.smartapi.Routes;
import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import com.angelbroking.smartapi.sample.SmartStreamListenerImplTest;
import com.angelbroking.smartapi.smartstream.models.*;
import com.angelbroking.smartapi.utils.ByteUtils;
import com.angelbroking.smartapi.utils.Constants;
import com.angelbroking.smartapi.utils.Utils;
import com.neovisionaries.ws.client.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SmartStreamTicker {

    private static final Logger logger = LoggerFactory.getLogger(SmartStreamTicker.class);

    private Routes routes = new Routes();
    private final String wsuri = routes.getSmartStreamWSURI();
    private SmartStreamListenerImplTest SmartStreamListenerImplTest;
    private WebSocket webSocket;
    private String clientId;
    private String feedToken;

    private EnumMap<SmartStreamSubsMode, Set<TokenID>> tokensByModeMap = new EnumMap<>(SmartStreamSubsMode.class);

    /**
     * Initializes the SmartStreamTicker.
     *
     * @param clientId            - the client ID used for authentication
     * @param feedToken           - the feed token used for authentication
     * @param SmartStreamListenerImplTest - the SmartStreamListenerImplTest for receiving callbacks
     * @throws IllegalArgumentException - if the clientId, feedToken, or SmartStreamListenerImplTest is null or empty
     */
    public SmartStreamTicker(String clientId, String feedToken, SmartStreamListenerImplTest SmartStreamListenerImplTest) {
        if (Utils.isEmpty(clientId) || Utils.isEmpty(feedToken) || SmartStreamListenerImplTest == null) {
            throw new IllegalArgumentException(
                    "clientId, feedToken and SmartStreamListenerImplTest should not be empty or null");
        }

        this.clientId = clientId;
        this.feedToken = feedToken;
        this.SmartStreamListenerImplTest = SmartStreamListenerImplTest;
        init();
    }

    private void init() {
        try {
            webSocket = new WebSocketFactory().setVerifyHostname(false).createSocket(wsuri).setPingInterval(Constants.PING_INTERVAL);
            webSocket.addHeader(Constants.CLIENT_ID_HEADER, clientId);
            webSocket.addHeader(Constants.FEED_TOKEN_HEADER, feedToken);
            webSocket.addHeader(Constants.CLIENT_LIB_HEADER, "JAVA");
            webSocket.addListener(getWebsocketAdapter());
        } catch (IOException e) {
            if (SmartStreamListenerImplTest != null) {
                SmartStreamListenerImplTest.onError(getErrorHolder(e));
            }
        }
    }

    /**
     * Returns a SmartStreamError object with the given Throwable object.
     *
     * @param e - the Throwable object
     * @return a SmartStreamError object
     */
    private SmartStreamError getErrorHolder(Throwable e) {
        SmartStreamError error = new SmartStreamError();
        error.setException(e);
        return error;
    }


    public WebSocketAdapter getWebsocketAdapter() {
        return new WebSocketAdapter() {

            @Override
            public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws WebSocketException {
                SmartStreamListenerImplTest.onConnected();
            }

            @Override
            public void onTextMessage(WebSocket websocket, String message) throws Exception {
                super.onTextMessage(websocket, message);
            }

            @Override
            public void onBinaryMessage(WebSocket websocket, byte[] binary) {
                SmartStreamSubsMode mode = SmartStreamSubsMode.findByVal(binary[0]);
                if (mode == null) {
                    SmartStreamListenerImplTest.onError(getErrorHolder(new SmartAPIException(
                            "Invalid SubsMode=" + binary[0] + " in the response binary packet")));
                    return;
                }
                try {
                    switch (mode) {
                        case LTP: {
                            ByteBuffer packet = ByteBuffer.wrap(binary).order(ByteOrder.LITTLE_ENDIAN);
                            LTP ltp = ByteUtils.mapByteBufferToLTP(packet);
                            SmartStreamListenerImplTest.onLTPArrival(ltp);
                            break;
                        }
                        case QUOTE: {
                            ByteBuffer packet = ByteBuffer.wrap(binary).order(ByteOrder.LITTLE_ENDIAN);
                            Quote quote = ByteUtils.mapByteBufferToQuote(packet);
                            SmartStreamListenerImplTest.onQuoteArrival(quote);
                            break;
                        }
                        case SNAP_QUOTE: {
                            ByteBuffer packet = ByteBuffer.wrap(binary).order(ByteOrder.LITTLE_ENDIAN);
                            SnapQuote snapQuote = ByteUtils.mapByteBufferToSnapQuote(packet);
                            SmartStreamListenerImplTest.onSnapQuoteArrival(snapQuote);
                            break;
                        }
                        default:
                            SmartStreamListenerImplTest.onError(getErrorHolder(
                                    new SmartAPIException("SubsMode=" + mode + " in the response is not handled.")));
                            break;
                    }
                    super.onBinaryMessage(websocket, binary);
                } catch (Exception e) {
                    SmartStreamListenerImplTest.onError(getErrorHolder(e));
                }
            }


            @Override
            public void onPongFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
                try {
                    SmartStreamListenerImplTest.onPong();
                } catch (Exception e) {
                    SmartStreamError error = new SmartStreamError();
                    error.setException(e);
                    SmartStreamListenerImplTest.onError(error);
                }
            }


            @Override
            public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame,
                                       WebSocketFrame clientCloseFrame, boolean closedByServer) {

                try {
                    if (closedByServer) {
                        if (serverCloseFrame.getCloseCode() == 1001) {

                        }
                        reconnectAndResubscribe();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCloseFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
                super.onCloseFrame(websocket, frame);
            }
        };
    }

    private void reconnectAndResubscribe() throws WebSocketException {
        init();
        connect();
        resubscribe();
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
        return (webSocket != null) && webSocket.isOpen();
    }

    /**
     * Returns true if websocket connection is closed.
     *
     * @return boolean
     */
    public boolean isConnectionClosed() {
        return !isConnectionOpen();
    }

    /**
     * Subscribes to the SmartStream for a given set of tokens and subscription mode.
     *
     * @param mode   the subscription mode, indicating the level of data required.
     * @param tokens the set of tokens to subscribe to.
     * @throws SmartAPIException if an error occurs while attempting to subscribe.
     */
    public void subscribe(SmartStreamSubsMode mode, Set<TokenID> tokens) {
        if (webSocket != null) {
            if (webSocket.isOpen()) {
                JSONObject wsMWJSONRequest = getApiRequest(SmartStreamAction.SUBS, mode, tokens);
                webSocket.sendText(wsMWJSONRequest.toString());
            } else {
                SmartStreamListenerImplTest.onError(getErrorHolder(new SmartAPIException(Constants.TICKER_NOT_CONNECTED, "504")));
            }
        } else {
            SmartStreamListenerImplTest.onError(getErrorHolder(new SmartAPIException(Constants.TICKER_NOT_NULL_CONNECTED, "504")));
        }
    }

    public void unsubscribe(SmartStreamSubsMode mode, Set<TokenID> tokens) {
        if (webSocket != null) {
            if (webSocket.isOpen()) {
                getApiRequest(SmartStreamAction.UNSUBS, mode, tokens);
            } else {
                SmartStreamListenerImplTest.onError(getErrorHolder(new SmartAPIException("ticker is not connected", "504")));
            }
        } else {
            SmartStreamListenerImplTest.onError(getErrorHolder(new SmartAPIException("ticker is null not connected", "504")));
        }
    }

    private JSONArray generateExchangeTokensList(Set<TokenID> tokens) {
        Map<ExchangeType, JSONArray> tokensByExchange = new EnumMap<>(ExchangeType.class);
        tokens.stream().forEach(t -> {
            JSONArray tokenList = tokensByExchange.get(t.getExchangeType());
            if (tokenList == null) {
                tokenList = new JSONArray();
                tokensByExchange.put(t.getExchangeType(), tokenList);
            }

            tokenList.put(t.getToken());
        });

        JSONArray exchangeTokenList = new JSONArray();
        tokensByExchange.forEach((ex, t) -> {
            JSONObject exchangeTokenObj = new JSONObject();
            exchangeTokenObj.put("exchangeType", ex.getVal());
            exchangeTokenObj.put("tokens", t);

            exchangeTokenList.put(exchangeTokenObj);
        });

        return exchangeTokenList;
    }

    public void resubscribe() {
        if (webSocket != null) {
            if (webSocket.isOpen()) {

                JSONObject wsMWJSONRequest = new JSONObject();
                wsMWJSONRequest.put("token", this.feedToken);
                wsMWJSONRequest.put("user", this.clientId);
                wsMWJSONRequest.put("acctid", this.clientId);

                webSocket.sendText(wsMWJSONRequest.toString());

            } else {
                SmartStreamListenerImplTest.onError(getErrorHolder(new SmartAPIException("ticker is not connected", "504")));
            }
        } else {
            SmartStreamListenerImplTest.onError(getErrorHolder(new SmartAPIException("ticker is null not connected", "504")));
        }
    }

    /**
     * Generates a JSON request for the SmartStream API with the given parameters.
     *
     * @param action The SmartStream action to perform.
     * @param mode   The subscription mode to use.
     * @param tokens The set of token IDs to subscribe to.
     * @return The JSON request object.
     */
    private JSONObject getApiRequest(SmartStreamAction action, SmartStreamSubsMode mode, Set<TokenID> tokens) {
        JSONObject params = new JSONObject();
        params.put(Constants.PARAM_MODE, mode.getVal());
        params.put(Constants.PARAM_TOKEN_LIST, this.generateExchangeTokensList(tokens));

        JSONObject wsMWJSONRequest = new JSONObject();
        wsMWJSONRequest.put(Constants.PARAM_ACTION, action.getVal());
        wsMWJSONRequest.put(Constants.PARAM_PARAMS, params);

        return wsMWJSONRequest;
    }

    public void connect() throws WebSocketException {
        webSocket.connect();
        if (logger.isDebugEnabled()) {
            logger.info(String.format("connected to uri: %s", wsuri));
        }
    }
}
