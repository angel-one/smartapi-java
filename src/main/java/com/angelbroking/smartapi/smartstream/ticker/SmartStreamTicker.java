package com.angelbroking.smartapi.smartstream.ticker;

import com.angelbroking.smartapi.Routes;
import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import com.angelbroking.smartapi.smartstream.models.*;
import com.angelbroking.smartapi.utils.ByteUtils;
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


	private static final int PING_INTERVAL = 10000; // 10 seconds
	private static final String CLIENT_ID_HEADER = "x-client-code";
	private static final String FEED_TOKEN_HEADER = "x-feed-token";
	private static final String CLIENT_LIB_HEADER = "x-client-lib";

	private Routes routes = new Routes();
	private final String wsuri = routes.getSmartStreamWSURI();
	private SmartStreamListener smartStreamListener;
	private WebSocket ws;
	private String clientId;
	private String feedToken;
	@SuppressWarnings("unused")
	private EnumMap<SmartStreamSubsMode, Set<TokenID>> tokensByModeMap = new EnumMap<>(SmartStreamSubsMode.class);

	/**
	 * Initialize SmartStreamTicker.
	 */
	public SmartStreamTicker(String clientId, String feedToken, SmartStreamListener smartStreamListener) {
		if (Utils.isEmpty(clientId) || Utils.isEmpty(feedToken) || smartStreamListener == null) {
			throw new IllegalArgumentException(
					"clientId, feedToken and smartStreamListener should not be empty or null");
		}

		this.clientId = clientId;
		this.feedToken = feedToken;
		this.smartStreamListener = smartStreamListener;
		init();
	}

	private void init() {
		try {
			ws = new WebSocketFactory().setVerifyHostname(false).createSocket(wsuri).setPingInterval(PING_INTERVAL);
			ws.addHeader(CLIENT_ID_HEADER, clientId); // mandatory header
			ws.addHeader(FEED_TOKEN_HEADER, feedToken); // mandatory header
			ws.addHeader(CLIENT_LIB_HEADER, "JAVA"); // optional header on the server
			ws.addListener(getWebsocketAdapter());
		} catch (IOException e) {
			if (smartStreamListener != null) {
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
			}

			@Override
			public void onTextMessage(WebSocket websocket, String message) throws Exception {
				super.onTextMessage(websocket, message);
			}

			@Override
			public void onBinaryMessage(WebSocket websocket, byte[] binary) {
				try {
					SmartStreamSubsMode mode = SmartStreamSubsMode.findByVal(binary[0]);
					if (mode == null) {
						smartStreamListener.onError(getErrorHolder(new SmartAPIException(
								"Invalid SubsMode=" + binary[0] + " in the response binary packet")));
						return;
					}

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
								new SmartAPIException("SubsMode=" + mode + " in the response is not handled.")));
						break;
					}
					super.onBinaryMessage(websocket, binary);
				} catch (Exception e) {
					smartStreamListener.onError(getErrorHolder(e));
				}
			}

			@Override
			public void onPongFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
				try {
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

	/** Disconnects websocket connection. */
	public void disconnect() {

		if (ws != null && ws.isOpen()) {
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
				JSONObject wsMWJSONRequest = getApiRequest(SmartStreamAction.SUBS, mode, tokens);
				ws.sendText(wsMWJSONRequest.toString());
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

	/**
	 * resubscribe existing tokens.
	 */
	public void resubscribe() {
		if (ws != null) {
			if (ws.isOpen()) {

				JSONObject wsMWJSONRequest = new JSONObject();
				wsMWJSONRequest.put("token", this.feedToken);
				wsMWJSONRequest.put("user", this.clientId);
				wsMWJSONRequest.put("acctid", this.clientId);

				ws.sendText(wsMWJSONRequest.toString());

			} else {
				smartStreamListener.onError(getErrorHolder(new SmartAPIException("ticker is not connected", "504")));
			}
		} else {
			smartStreamListener.onError(getErrorHolder(new SmartAPIException("ticker is null not connected", "504")));
		}
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
		logger.info("connected to uri: "+ wsuri);
	}

}
