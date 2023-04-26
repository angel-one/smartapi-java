package com.angelbroking.smartapi.ticker;

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

public class SmartAPITicker {

	private final Routes routes = new Routes();
	private final String wsuri = routes.getWsuri();
	private OnTicks onTickerArrivalListener;
	private OnConnect onConnectedListener;
	private OnError onErrorListener;
	private WebSocket ws;
	private final String clientId;
	private final String feedToken;
	private final String script;
	private final String task;
	private SSLContext context;

	/**
	 * Initialize SmartAPITicker.
	 */
	public SmartAPITicker(String clientId, String feedToken, String script, String task) {

		this.clientId = clientId;
		this.feedToken = feedToken;
		this.script = script;
		this.task = task;

		try {

			context = NaiveSSLContext.getInstance("TLS");
			ws = new WebSocketFactory().setSSLContext(context).setVerifyHostname(false).createSocket(wsuri);

		} catch (IOException e) {
			if (onErrorListener != null) {
				onErrorListener.onError(e);
			}
			return;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		ws.addListener(getWebsocketAdapter());

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

	/** Returns a WebSocketAdapter to listen to ticker related events. */
	public WebSocketAdapter getWebsocketAdapter() {
		return new WebSocketAdapter() {

			@Override
			public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws WebSocketException {
				// Send a text frame.
				JSONObject wsCNJSONRequest = new JSONObject();
				wsCNJSONRequest.put("task", "cn");
				wsCNJSONRequest.put("channel", "");
				wsCNJSONRequest.put("token", feedToken);
				wsCNJSONRequest.put("user", clientId);
				wsCNJSONRequest.put("acctid", clientId);
				ws.sendText(wsCNJSONRequest.toString());
				onConnectedListener.onConnected();

				Runnable runnable = new Runnable() {
					public void run() {
						JSONObject wsMWJSONRequest = new JSONObject();
						wsMWJSONRequest.put("task", "hb");
						wsMWJSONRequest.put("channel", "");
						wsMWJSONRequest.put("token", feedToken);
						wsMWJSONRequest.put("user", clientId);
						wsMWJSONRequest.put("acctid", clientId);
						ws.sendText(wsMWJSONRequest.toString());
					}
				};

				ScheduledExecutorService service = Executors

						.newSingleThreadScheduledExecutor();

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

				try {
					context = NaiveSSLContext.getInstance("TLS");
					ws = new WebSocketFactory().setSSLContext(context).setVerifyHostname(false).createSocket(wsuri);
					ws.addListener(getWebsocketAdapter());
					connect();

				} catch (Exception e) {
					e.printStackTrace();
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
	public void subscribe() {

		if (ws != null) {
			if (ws.isOpen()) {

				JSONObject wsMWJSONRequest = new JSONObject();
				wsMWJSONRequest.put("task", this.task);
				wsMWJSONRequest.put("channel", this.script);
				wsMWJSONRequest.put("token", this.feedToken);
				wsMWJSONRequest.put("user", this.clientId);
				wsMWJSONRequest.put("acctid", this.clientId);

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

	/**
	 * runSript script.
	 */
	public void resubscribe() {
		subscribe();
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
