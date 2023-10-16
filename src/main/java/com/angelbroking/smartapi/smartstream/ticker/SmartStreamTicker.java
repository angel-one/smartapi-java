package com.angelbroking.smartapi.smartstream.ticker;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import com.angelbroking.smartapi.smartstream.models.*;
import org.json.JSONArray;
import org.json.JSONObject;

import com.angelbroking.smartapi.Routes;
import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import com.angelbroking.smartapi.utils.ByteUtils;
import com.angelbroking.smartapi.utils.Utils;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SmartStreamTicker {

	private static final int PING_INTERVAL = 10000; // 10 seconds
	private static final String CLIENT_ID_HEADER = "x-client-code";
	private static final String FEED_TOKEN_HEADER = "x-feed-token";
	private static final String CLIENT_LIB_HEADER = "x-client-lib";

	private final Routes routes = new Routes();
	private final String wsuri = routes.getSmartStreamWSURI();
	private final SmartStreamListener smartStreamListener;
	private WebSocket ws;
	private final String clientId;
	private final String feedToken;
	private EnumMap<SmartStreamSubsMode, Set<TokenID>> tokensByModeMap = new EnumMap<>(SmartStreamSubsMode.class);
	private Timer pingTimer;
	private LocalDateTime lastPongReceivedTime = LocalDateTime.now();

	/**
     * Initializes the SmartStreamTicker.
     *
     * @param clientId            - the client ID used for authentication
     * @param feedToken           - the feed token used for authentication
     * @param smartStreamListener - the SmartStreamListener for receiving callbacks
     * @throws IllegalArgumentException - if the clientId, feedToken, or SmartStreamListener is null or empty
     */
    public SmartStreamTicker(String clientId, String feedToken, SmartStreamListener smartStreamListener) {
        if (Utils.isEmpty(clientId) || Utils.isEmpty(feedToken) ||  Utils.validateInputNullCheck(smartStreamListener)) {
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
			ws = new WebSocketFactory()
					.setVerifyHostname(false)
					.createSocket(wsuri)
					.setPingInterval(PING_INTERVAL);
			ws.addHeader(CLIENT_ID_HEADER, clientId); 
			ws.addHeader(FEED_TOKEN_HEADER, feedToken);
			ws.addHeader(CLIENT_LIB_HEADER, "JAVA");
			ws.addListener(getWebsocketAdapter());
		} catch (IOException e) {
			if (Utils.validateInputNotNullCheck(smartStreamListener)) {
				smartStreamListener.onError(getErrorHolder(e));
			}
		}
	}

	private SmartStreamError getErrorHolder(Throwable e) {
		SmartStreamError error = new SmartStreamError();
		error.setException(e);
		return error;
	}

	/** Returns a WebSocketAdapter to listen to ticker related events. */
	public WebSocketAdapter getWebsocketAdapter() {
		return new WebSocketAdapter() {
			@Override
			public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws WebSocketException {
				smartStreamListener.onConnected();
                startPingTimer(websocket);
			}

			@Override
			public void onTextMessage(WebSocket websocket, String message) throws Exception {
				super.onTextMessage(websocket, message);
			}

			@Override
			public void onBinaryMessage(WebSocket websocket, byte[] binary) {
				SmartStreamSubsMode mode = SmartStreamSubsMode.findByVal(binary[0]);
				if (Utils.validateInputNullCheck(mode)) {
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
							LTP ltp = ByteUtils.mapToLTP(packet);
							smartStreamListener.onLTPArrival(ltp);
							break;
						}
						case QUOTE: {
							ByteBuffer packet = ByteBuffer.wrap(binary).order(ByteOrder.LITTLE_ENDIAN);
							Quote quote = ByteUtils.mapToQuote(packet);
							smartStreamListener.onQuoteArrival(quote);
							break;
						}
						case SNAP_QUOTE: {
							ByteBuffer packet = ByteBuffer.wrap(binary).order(ByteOrder.LITTLE_ENDIAN);
							SnapQuote snapQuote = ByteUtils.mapToSnapQuote(packet);
							smartStreamListener.onSnapQuoteArrival(snapQuote);
							break;
						}
						case DEPTH_20: {
							ByteBuffer packet = ByteBuffer.wrap(binary).order(ByteOrder.LITTLE_ENDIAN);
							Depth depth = ByteUtils.mapToDepth20(packet);
							smartStreamListener.onDepthArrival(depth);
							break;
						}
						default: {
							smartStreamListener.onError(getErrorHolder(
									new SmartAPIException("SubsMode=" + mode + " in the response is not handled.")));
							break;
						}
					}
				} catch (Exception e) {
					smartStreamListener.onError(getErrorHolder(e));
				}
			}

			@Override
			public void onPongFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
				try {
                    lastPongReceivedTime = LocalDateTime.now();
                    smartStreamListener.onPong();
                } catch (Exception e) {
                    SmartStreamError error = new SmartStreamError();
                    error.setException(e);
                    smartStreamListener.onError(error);
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
				try {
                    if (closedByServer) {
                        reconnectAndResubscribe();
                    } else {
                        stopPingTimer();
                        smartStreamListener.onDisconnected();
                    }
                } catch (Exception e) {
                	SmartStreamError error = new SmartStreamError();
                    error.setException(e);
                    smartStreamListener.onError(error);
                }
			}
			
			@Override
			public void onCloseFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
				super.onCloseFrame(websocket, frame);
			}
		};
	}
	
	private void startPingTimer(final WebSocket websocket) {

        pingTimer = new Timer();
        pingTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    LocalDateTime currentTime = LocalDateTime.now();
                    if (lastPongReceivedTime.isBefore(currentTime.minusSeconds(20))) {
                        websocket.disconnect();
                        reconnectAndResubscribe();
                    }
                } catch (Exception e) {
                    smartStreamListener.onError(getErrorHolder(e));
                }
            }
        }, 5000, 5000); // run at every 5 second
    }

    private void stopPingTimer() {
        if (Utils.validateInputNotNullCheck(pingTimer)) {
            pingTimer.cancel();
            pingTimer = null;
        }
    }

	private void reconnectAndResubscribe() throws WebSocketException {
		log.info("reconnectAndResubscribe - started");
		init();
		connect();
		// resubscribing the existing tokens as per the mode
		tokensByModeMap.forEach((mode,tokens) -> {
			subscribe(mode, tokens);
		});
		log.info("reconnectAndResubscribe - done");
	}

	/** Disconnects websocket connection. */
	public void disconnect() {

		if (ws != null) {
			stopPingTimer();
			ws.disconnect();
		}
	}

	/**
	 * Returns true if websocket connection is open.
	 * 
	 * @return boolean
	 */
	public boolean isConnectionOpen() {
		return (ws != null) && ws.isOpen();
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
	 * Subscribes tokens.
	 */
	public void subscribe(SmartStreamSubsMode mode, Set<TokenID> tokens) {
		if (ws != null) {
			if (ws.isOpen()) {
				for (TokenID token : tokens) {
					if (ExchangeType.NSE_CM.equals(token.getExchangeType()) && SmartStreamSubsMode.DEPTH_20.equals(mode)) {
						if (tokens.size() < 50) {
							JSONObject wsMWJSONRequest = getApiRequest(SmartStreamAction.SUBS, mode, tokens);
							ws.sendText(wsMWJSONRequest.toString());
							tokensByModeMap.put(mode, tokens);
						} else {
							smartStreamListener.onError(getErrorHolder(new SmartAPIException("Token size should be less than 50", "504")));
						}
					} else {
						if (!ExchangeType.NSE_CM.equals(token.getExchangeType()) && SmartStreamSubsMode.DEPTH_20.equals(mode)) {
							smartStreamListener.onError(getErrorHolder(new SmartAPIException("Invalid Exchange Type: Please check the exchange type and try again", "504")));
						} else {
							JSONObject wsMWJSONRequest = getApiRequest(SmartStreamAction.SUBS, mode, tokens);
							ws.sendText(wsMWJSONRequest.toString());
							tokensByModeMap.put(mode, tokens);
						}
					}
				}
			} else {
				smartStreamListener.onError(getErrorHolder(new SmartAPIException("ticker is not connected", "504")));
			}
		} else {
			smartStreamListener.onError(getErrorHolder(new SmartAPIException("ticker is null not connected", "504")));
		}
	}

	/**
	 * Unsubscribes tokens.
	 */
	public void unsubscribe(SmartStreamSubsMode mode, Set<TokenID> tokens) {
		if (ws != null) {
			if (ws.isOpen()) {
				JSONObject wsMWJSONRequest = getApiRequest(SmartStreamAction.UNSUBS, mode, tokens);
				ws.sendText(wsMWJSONRequest.toString());
				Set<TokenID> currentlySubscribedTokens = tokensByModeMap.get(mode);
				if(currentlySubscribedTokens != null) {
					currentlySubscribedTokens.removeAll(tokens);
				}
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

	private JSONObject getApiRequest(SmartStreamAction action, SmartStreamSubsMode mode, Set<TokenID> tokens) {
		JSONObject params = new JSONObject();
		params.put("mode", mode.getVal());
		params.put("tokenList", this.generateExchangeTokensList(tokens));

		JSONObject wsMWJSONRequest = new JSONObject();
		wsMWJSONRequest.put("action", action.getVal());
		wsMWJSONRequest.put("params", params);

		return wsMWJSONRequest;
	}

	public void connect() throws WebSocketException {
		ws.connect();
		log.info("connected to uri: {}", wsuri);
	}

}
