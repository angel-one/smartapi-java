package com.angelbroking.smartapi.smartstream.ticker;

import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import com.angelbroking.smartapi.dto.WsMWRequestDTO;
import com.angelbroking.smartapi.routes.Routes;
import com.angelbroking.smartapi.smartstream.models.ExchangeType;
import com.angelbroking.smartapi.smartstream.models.LTP;
import com.angelbroking.smartapi.smartstream.models.Quote;
import com.angelbroking.smartapi.smartstream.models.SmartStreamAction;
import com.angelbroking.smartapi.smartstream.models.SmartStreamError;
import com.angelbroking.smartapi.smartstream.models.SmartStreamSubsMode;
import com.angelbroking.smartapi.smartstream.models.SnapQuote;
import com.angelbroking.smartapi.smartstream.models.TokenID;
import com.angelbroking.smartapi.utils.ByteUtils;
import com.angelbroking.smartapi.utils.Utils;
import com.google.gson.Gson;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.angelbroking.smartapi.utils.Constants.CLIENT_ID_HEADER;
import static com.angelbroking.smartapi.utils.Constants.CLIENT_LIB_HEADER;
import static com.angelbroking.smartapi.utils.Constants.FEED_TOKEN_HEADER;
import static com.angelbroking.smartapi.utils.Constants.PARAM_ACTION;
import static com.angelbroking.smartapi.utils.Constants.PARAM_MODE;
import static com.angelbroking.smartapi.utils.Constants.PARAM_PARAMS;
import static com.angelbroking.smartapi.utils.Constants.PARAM_TOKEN_LIST;
import static com.angelbroking.smartapi.utils.Constants.PING_INTERVAL;
import static com.angelbroking.smartapi.utils.Constants.TICKER_NOT_CONNECTED;
import static com.angelbroking.smartapi.utils.Constants.TICKER_NOT_NULL_CONNECTED;

@Slf4j
public class SmartStreamTicker {

    private Routes routes = new Routes();
    private SmartStreamListener smartStreamListener;
    private WebSocket webSocket;
    private String clientId;
    private String feedToken;

    /**
     * Initializes the SmartStreamTicker.
     *
     * @param clientId            - the client ID used for authentication
     * @param feedToken           - the feed token used for authentication
     * @param smartStreamListener - the SmartStreamListener for receiving callbacks
     * @throws IllegalArgumentException - if the clientId, feedToken, or SmartStreamListener is null or empty
     */
    public SmartStreamTicker(String clientId, String feedToken, SmartStreamListener smartStreamListener) {
        if (Utils.isEmpty(clientId) || Utils.isEmpty(feedToken) || smartStreamListener == null) {
            throw new IllegalArgumentException(
                    "clientId, feedToken and SmartStreamListener should not be empty or null");
        }

        this.clientId = clientId;
        this.feedToken = feedToken;
        this.smartStreamListener = smartStreamListener;
        init();
    }

    private void init() {
        try {
            webSocket = new WebSocketFactory().setVerifyHostname(false).createSocket(routes.getSmartStreamWSURI()).setPingInterval(PING_INTERVAL);
            webSocket.addHeader(CLIENT_ID_HEADER, clientId);
            webSocket.addHeader(FEED_TOKEN_HEADER, feedToken);
            webSocket.addHeader(CLIENT_LIB_HEADER, "JAVA");
            webSocket.addListener(getWebsocketAdapter());
        } catch (IOException e) {
            if (smartStreamListener != null) {
                smartStreamListener.onError(getErrorHolder(e));
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
            public void onConnected(WebSocket websocket, Map<String, List<String>> headers) {
                smartStreamListener.onConnected();
            }

            @Override
            public void onTextMessage(WebSocket websocket, String message) throws Exception {
                super.onTextMessage(websocket, message);
            }

            @Override
            public void onBinaryMessage(WebSocket websocket, byte[] binary) {
                SmartStreamSubsMode mode = SmartStreamSubsMode.findByVal(binary[0]);
                if (mode == null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Invalid SubsMode=");
                    sb.append(binary[0]);
                    sb.append(" in the response binary packet");
                    smartStreamListener.onError(getErrorHolder(new SmartAPIException(sb.toString())));
                }
                try {
                    switch (mode) {
                        case LTP: {
                            ByteBuffer packet = ByteBuffer.wrap(binary).order(ByteOrder.LITTLE_ENDIAN);
                            LTP ltp = ByteUtils.mapByteBufferToLTP(packet);
                            smartStreamListener.onLTPArrival(ltp);
                            break;
                        }
                        case QUOTE: {
                            ByteBuffer packet = ByteBuffer.wrap(binary).order(ByteOrder.LITTLE_ENDIAN);
                            Quote quote = ByteUtils.mapByteBufferToQuote(packet);
                            smartStreamListener.onQuoteArrival(quote);
                            break;
                        }
                        case SNAP_QUOTE: {
                            ByteBuffer packet = ByteBuffer.wrap(binary).order(ByteOrder.LITTLE_ENDIAN);
                            SnapQuote snapQuote = ByteUtils.mapByteBufferToSnapQuote(packet);
                            smartStreamListener.onSnapQuoteArrival(snapQuote);
                            break;
                        }
                        default:
                            smartStreamListener.onError(getErrorHolder(
                             new SmartAPIException("SubsMode=${mode} in the response is not handled.")));
                            break;
                    }
                    super.onBinaryMessage(websocket, binary);
                } catch (Exception e) {
                    smartStreamListener.onError(getErrorHolder(e));
                }
            }


            @Override
            public void onPongFrame(WebSocket websocket, WebSocketFrame frame) {
                try {
                    smartStreamListener.onPong();
                } catch (Exception e) {
                    SmartStreamError error = new SmartStreamError();
                    error.setException(e);
                    smartStreamListener.onError(error);
                }
            }


            @Override
            public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame,
                                       WebSocketFrame clientCloseFrame, boolean closedByServer) {

                try {
                    if (closedByServer) {
                        if (serverCloseFrame.getCloseCode() == 1001) {
                            // Log the server close code for debugging purposes
                            log.info("Server closed connection with code: {}", serverCloseFrame.getCloseCode());
                        }
                        reconnectAndResubscribe();
                    }

                } catch (Exception e) {
                  log.error(e.getMessage());
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
     */
    public void subscribe(SmartStreamSubsMode mode, Set<TokenID> tokens) {
        if (webSocket != null) {
            if (webSocket.isOpen()) {
                webSocket.sendText(getApiRequest(SmartStreamAction.SUBS, mode, tokens).toString());
            } else {
                smartStreamListener.onError(getErrorHolder(new SmartAPIException(TICKER_NOT_CONNECTED, "504")));
            }
        } else {
            smartStreamListener.onError(getErrorHolder(new SmartAPIException(TICKER_NOT_NULL_CONNECTED, "504")));
        }
    }

    public void unsubscribe(SmartStreamSubsMode mode, Set<TokenID> tokens) {
        if (webSocket != null) {
            if (webSocket.isOpen()) {
                getApiRequest(SmartStreamAction.UNSUBS, mode, tokens);
            } else {
                smartStreamListener.onError(getErrorHolder(new SmartAPIException("ticker is not connected", "504")));
            }
        } else {
            smartStreamListener.onError(getErrorHolder(new SmartAPIException("ticker is null not connected", "504")));
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
                webSocket.sendText(new Gson().toJson(new WsMWRequestDTO(this.feedToken,this.clientId,this.clientId)));
            } else {
                smartStreamListener.onError(getErrorHolder(new SmartAPIException("ticker is not connected", "504")));
            }
        } else {
            smartStreamListener.onError(getErrorHolder(new SmartAPIException("ticker is null not connected", "504")));
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
        params.put(PARAM_MODE, mode.getVal());
        params.put(PARAM_TOKEN_LIST, this.generateExchangeTokensList(tokens));

        JSONObject wsMWJSONRequest = new JSONObject();
        wsMWJSONRequest.put(PARAM_ACTION, action.getVal());
        wsMWJSONRequest.put(PARAM_PARAMS, params);

        return wsMWJSONRequest;
    }

    public void connect() throws WebSocketException {
        webSocket.connect();
    }
}
