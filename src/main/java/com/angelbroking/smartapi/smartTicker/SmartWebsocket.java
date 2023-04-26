package com.angelbroking.smartapi.smartTicker;

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

import javax.net.ssl.SSLContext;

import org.json.JSONArray;
import org.json.JSONObject;

import com.angelbroking.smartapi.Routes;
import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import com.angelbroking.smartapi.utils.NaiveSSLContext;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;

public class SmartWebsocket {

	private SmartWSOnTicks onTickerArrivalListener;
	private SmartWSOnConnect onConnectedListener;
	private SmartWSOnDisconnect onDisconnectedListener;
	private SmartWSOnError onErrorListener;
	private WebSocket ws;
	private final String clientId;
	private final String jwtToken;
	private final String apiKey;
	private final String actionType;
	private final String feedType;

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
			Routes routes = new Routes();
			String wsuri = routes.getSWsuri();
			String swsuri = wsuri + "?jwttoken=" + this.jwtToken + "&&clientcode=" + this.clientId + "&&apikey="
					+ this.apiKey;
			SSLContext context = NaiveSSLContext.getInstance("TLS");
			ws = new WebSocketFactory().setSSLContext(context).setVerifyHostname(false).createSocket(swsuri);

			ws.addListener(getWebsocketAdapter());
		} catch (IOException e) {
			if (onErrorListener != null) {
				onErrorListener.onError(e);
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

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

	/** Returns a WebSocketAdapter to listen to ticker related events. */
	public WebSocketAdapter getWebsocketAdapter() {
		return new WebSocketAdapter() {

			@Override
			public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws WebSocketException {
				onConnectedListener.onConnected();
				Runnable runnable = new Runnable() {
					public void run() {
						JSONObject wsMWJSONRequest = new JSONObject();
						wsMWJSONRequest.put("actiontype", actionType);
						wsMWJSONRequest.put("feedtype", feedType);
						wsMWJSONRequest.put("jwttoken", jwtToken);
						wsMWJSONRequest.put("clientcode", clientId);
						wsMWJSONRequest.put("apikey", apiKey);
						ws.sendText(wsMWJSONRequest.toString());
					}
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
					e.printStackTrace();
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
					e.printStackTrace();
					if (onErrorListener != null) {
						onErrorListener.onError(e);
					}
				}
			}

		};
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
		if (ws != null) {
			return ws.isOpen();
		}
		return false;
	}

	/**
	 * Subscribes script.
	 */
	public void runscript() {

		if (ws != null) {
			if (ws.isOpen()) {

				JSONObject wsMWJSONRequest = new JSONObject();
				wsMWJSONRequest.put("actiontype", this.actionType);
				wsMWJSONRequest.put("feedtype", this.feedType);
				wsMWJSONRequest.put("jwttoken", this.jwtToken);
				wsMWJSONRequest.put("clientcode", this.clientId);
				wsMWJSONRequest.put("apikey", this.apiKey);

				ws.sendText(wsMWJSONRequest.toString());

			} else {
				if (onErrorListener != null) {
					onErrorListener.onError(new SmartAPIException("ticker is not connected", "504"));
				}
			}
		} else {
			if (onErrorListener != null) {
				onErrorListener.onError(new SmartAPIException("ticker is null not connected", "504"));
			}
		}
	}

	public static byte[] decompress(byte[] compressedTxt) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try (OutputStream ios = new InflaterOutputStream(os)) {
			ios.write(compressedTxt);
		}

		return os.toByteArray();
	}

	public void connect() {
		try {
			ws.connect();
		} catch (WebSocketException e) {
			e.printStackTrace();
		}

	}

}
