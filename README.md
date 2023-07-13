# SmartAPI 2.1.0 Java client
The official Java client for communicating with [SmartAPI Connect API](https://smartapi.angelbroking.com).

SmartAPI is a set of REST-like APIs that expose many capabilities required to build a complete investment and trading platform. Execute orders in real-time, manage user portfolios, stream live market data (WebSockets), and more, with the simple HTTP API collection.

## Documentation
- [SmartAPI - HTTP API documentation] (https://smartapi.angelbroking.com/docs)
- [Java library documentation](https://smartapi.angelbroking.com/docs)

## Usage
- [Download SmartAPI jar file](https://github.com/angel-one/smartapi-java/blob/main/dist/) and include it in your build path.

- Include com.angelbroking.smartapi into build path from maven. Use version 2.1.0

## API usage
```java
	// Initialize SamartAPI using Client code, Password, and TOTP.
	SmartConnect smartConnect = new SmartConnect();
	
	// Provide your API key here
	smartConnect.setApiKey("<api_key>");
	
	// Set session expiry callback.
	smartConnect.setSessionExpiryHook(new SessionExpiryHook() {
	@Override
	public void sessionExpired() {
		System.out.println("session expired");
	}
	});
	
	User user = smartConnect.generateSession(<clientId>, <password>, <totp>);
	smartConnect.setAccessToken(user.getAccessToken());
	smartConnect.setUserId(user.getUserId());
	
	// token re-generate
	
	TokenSet tokenSet = smartConnect.renewAccessToken(user.getAccessToken(),
	user.getRefreshToken());
	smartConnect.setAccessToken(tokenSet.getAccessToken());
	
	/** CONSTANT Details */

	/* VARIETY */
	/*
	 * VARIETY_NORMAL: Normal Order (Regular) 
	 * VARIETY_AMO: After Market Order
	 * VARIETY_STOPLOSS: Stop loss order 
	 * VARIETY_ROBO: ROBO (Bracket) Order
	 */
	/* TRANSACTION TYPE */
	/*
	 * TRANSACTION_TYPE_BUY: Buy TRANSACTION_TYPE_SELL: Sell
	 */

	/* ORDER TYPE */
	/*
	 * ORDER_TYPE_MARKET: Market Order(MKT) 
	 * ORDER_TYPE_LIMIT: Limit Order(L)
	 * ORDER_TYPE_STOPLOSS_LIMIT: Stop Loss Limit Order(SL)
	 * ORDER_TYPE_STOPLOSS_MARKET: Stop Loss Market Order(SL-M)
	 */

	/* PRODUCT TYPE */
	/*
	 * PRODUCT_DELIVERY: Cash & Carry for equity (CNC) 
	 * PRODUCT_CARRYFORWARD: Normal
	 * for futures and options (NRML) 
	 * PRODUCT_MARGIN: Margin Delivery
	 * PRODUCT_INTRADAY: Margin Intraday Squareoff (MIS) 
	 * PRODUCT_BO: Bracket Order
	 * (Only for ROBO)
	 */

	/* DURATION */
	/*
	 * DURATION_DAY: Valid for a day 
	 * DURATION_IOC: Immediate or Cancel
	 */

	/* EXCHANGE */
	/*
	 * EXCHANGE_BSE: BSE Equity 
	 * EXCHANGE_NSE: NSE Equity 
	 * EXCHANGE_NFO: NSE Future and Options 
	 * EXCHANGE_CDS: NSE Currency 
	 * EXCHANGE_NCDEX: NCDEX Commodity
	 * EXCHANGE_MCX: MCX Commodity
	 */

	/** Place order. */
	public void placeOrder(SmartConnect smartConnect) throws SmartAPIException, IOException {

		OrderParams orderParams = new OrderParams();
		orderParams.variety = "NORMAL";
		orderParams.quantity = 1;
		orderParams.symboltoken = "3045";
		orderParams.exchange = Constants.EXCHANGE_NSE;
		orderParams.ordertype = Constants.ORDER_TYPE_LIMIT;
		orderParams.tradingsymbol = "SBIN-EQ";
		orderParams.producttype = Constants.PRODUCT_INTRADAY;
		orderParams.duration = Constants.VALIDITY_DAY;
		orderParams.transactiontype = Constants.TRANSACTION_TYPE_BUY;
		orderParams.price = 122.2;
		orderParams.squareoff = "0";
		orderParams.stoploss = "0";

		Order order = smartConnect.placeOrder(orderParams, Constants.VARIETY_REGULAR);
	}

	/** Modify order. */
	public void modifyOrder(SmartConnect smartConnect) throws SmartAPIException, IOException {
		// Modify order request will return the order model which will contain order_id.

		OrderParams orderParams = new OrderParams();
		orderParams.quantity = 1;
		orderParams.ordertype = Constants.ORDER_TYPE_LIMIT;
		orderParams.tradingsymbol = "ASHOKLEY";
		orderParams.symboltoken = "3045";
		orderParams.producttype = Constants.PRODUCT_DELIVERY;
		orderParams.exchange = Constants.EXCHANGE_NSE;
		orderParams.duration = Constants.VALIDITY_DAY;
		orderParams.price = 122.2;

		String orderId = "201216000755110";
		Order order = smartConnect.modifyOrder(orderId, orderParams, Constants.VARIETY_REGULAR);
	}

	/** Cancel order */
	public void cancelOrder(SmartConnect smartConnect) throws SmartAPIException, IOException {
		// Cancel order will return the order model which will have orderId.
		String orderId = "201216000755110";
		Order order = smartConnect.cancelOrder(orderId, Constants.VARIETY_REGULAR);
	}

	/** Get order details */
	public void getOrder(SmartConnect smartConnect) throws SmartAPIException, IOException {
		List<Order> orders = smartConnect.getOrderHistory(smartConnect.getUserId());
		for (int i = 0; i < orders.size(); i++) {
			System.out.println(orders.get(i).orderId + " " + orders.get(i).status);
		}
	}

	/**
	 * Get the last price for multiple instruments at once. Users can either pass
	 * exchange with tradingsymbol or instrument token only. For example {NSE:NIFTY
	 * 50, BSE:SENSEX} or {256265, 265}
	 */
	public void getLTP(SmartConnect smartConnect) throws SmartAPIException, IOException {
		String exchange = "NSE";
		String tradingSymbol = "SBIN-EQ";
		String symboltoken = "3045";
		JSONObject ltpData = smartConnect.getLTP(exchange, tradingSymbol, symboltoken);
	}

	/** Get tradebook */
	public void getTrades(SmartConnect smartConnect) throws SmartAPIException, IOException {
		// Returns tradebook.
		List<Trade> trades = smartConnect.getTrades();
		for (int i = 0; i < trades.size(); i++) {
			System.out.println(trades.get(i).tradingSymbol + " " + trades.size());
		}
	}

	/** Get Margin in trading account*/
	public void getRMS(SmartConnect smartConnect) throws SmartAPIException, IOException {
		// Returns RMS.
		JSONObject response = smartConnect.getRMS();
	}

	/** Get Holdings */
	public void getHolding(SmartConnect smartConnect) throws SmartAPIException, IOException {
		// Returns Holding.
		JSONObject response = smartConnect.getHolding();
	}

	/** Get Position */
	public void getPosition(SmartConnect smartConnect) throws SmartAPIException, IOException {
		// Returns Position.
		JSONObject response = smartConnect.getPosition();
	}

	/** convert Position */
	public void convertPosition(SmartConnect smartConnect) throws SmartAPIException, IOException {

		JSONObject requestObejct = new JSONObject();
		requestObejct.put("exchange", "NSE");
		requestObejct.put("oldproducttype", "DELIVERY");
		requestObejct.put("newproducttype", "MARGIN");
		requestObejct.put("tradingsymbol", "SBIN-EQ");
		requestObejct.put("transactiontype", "BUY");
		requestObejct.put("quantity", 1);
		requestObejct.put("type", "DAY");

		JSONObject response = smartConnect.convertPosition(requestObejct);
	}
	
	/** Create Gtt Rule*/
	public void createRule(SmartConnect smartConnect)throws SmartAPIException,IOException{
		GttParams gttParams= new GttParams();
		
		gttParams.tradingsymbol="SBIN-EQ";
		gttParams.symboltoken="3045";
		gttParams.exchange="NSE";
		gttParams.producttype="MARGIN";
		gttParams.transactiontype="BUY";
		gttParams.price= 100000.01;
		gttParams.qty=10;
		gttParams.disclosedqty=10;
		gttParams.triggerprice=20000.1;
		gttParams.timeperiod=300;
		
		Gtt gtt = smartConnect.gttCreateRule(gttParams);
	}

	
	/** Modify Gtt Rule */
	public void modifyRule(SmartConnect smartConnect)throws SmartAPIException,IOException{
		GttParams gttParams= new GttParams();
		
		gttParams.tradingsymbol="SBIN-EQ";
		gttParams.symboltoken="3045";
		gttParams.exchange="NSE";
		gttParams.producttype="MARGIN";
		gttParams.transactiontype="BUY";
		gttParams.price= 100000.1;
		gttParams.qty=10;
		gttParams.disclosedqty=10;
		gttParams.triggerprice=20000.1;
		gttParams.timeperiod=300;
		
		Integer id= 1000051;
		
		Gtt gtt = smartConnect.gttModifyRule(id,gttParams);
	}
	
	/** Cancel Gtt Rule */
	public void cancelRule(SmartConnect smartConnect)throws SmartAPIException, IOException{
		Integer id=1000051;
		String symboltoken="3045";
		String exchange="NSE";
		
		Gtt gtt = smartConnect.gttCancelRule(id,symboltoken,exchange);
	}
	
	/** Gtt Rule Details */
	public void ruleDetails(SmartConnect smartConnect)throws SmartAPIException, IOException{
		Integer id=1000051;
	
		JSONObject gtt = smartConnect.gttRuleDetails(id);
	}
	
	/** Gtt Rule Lists */
	public void ruleList(SmartConnect smartConnect)throws SmartAPIException, IOException{
		
		List<String> status=new ArrayList<String>(){{
			add("NEW");
			add("CANCELLED");
			add("ACTIVE");
			add("SENTTOEXCHANGE");
			add("FORALL");
			}};
		Integer page=1;
		Integer count=10;
	
		JSONArray gtt = smartConnect.gttRuleList(status,page,count);
	}

	/** Historic Data */
	public void getCandleData(SmartConnect smartConnect) throws SmartAPIException, IOException {

		JSONObject requestObejct = new JSONObject();
		requestObejct.put("exchange", "NSE");
		requestObejct.put("symboltoken", "3045");
		requestObejct.put("interval", "ONE_MINUTE");
		requestObejct.put("fromdate", "2021-03-08 09:00");
		requestObejct.put("todate", "2021-03-09 09:20");

		String response = smartConnect.candleData(requestObejct);
	}
	
	/** Logout user. */
	public void logout(SmartConnect smartConnect) throws SmartAPIException, IOException {
		/** Logout user and kill the session. */
		JSONObject jsonObject = smartConnect.logout();
	}
	
```
For more details, take a look at Examples.java in the sample directory.

## WebSocket live streaming data

```java

	/* SmartAPITicker */
	String clientId = "<clientId>";
	User user = smartConnect.generateSession("<clientId>", "<password>", "<totp>");
	String feedToken = user.getFeedToken();
	String strWatchListScript = "nse_cm|2885&nse_cm|1594&nse_cm|11536&mcx_fo|221658";
	String task = "mw";

	examples.tickerUsage(clientId, feedToken, strWatchListScript, task);

	/*
	* String jwtToken = user.getAccessToken(); 
	* String apiKey = "<api_key>";
	* String actionType = "subscribe"; String feedType = "order_feed";
	* 
	* examples.smartWebSocketUsage(clientId, jwtToken, apiKey, actionType,
	* feedType);
	* 
	*/
	
	public void tickerUsage(String clientId, String feedToken, String strWatchListScript, String task)
			throws SmartAPIException {

		SmartAPITicker tickerProvider = new SmartAPITicker(clientId, feedToken, strWatchListScript, task);

		tickerProvider.setOnConnectedListener(new OnConnect() {
			@Override
			public void onConnected() {
				System.out.println("subscribe() called!");
				tickerProvider.subscribe();
			}
		});

		tickerProvider.setOnTickerArrivalListener(new OnTicks() {
			@Override
			public void onTicks(JSONArray ticks) {
				System.out.println("ticker data: " + ticks.toString());
			}
		});

		/**
		 * connects to SmartAPI ticker server for getting live quotes
		 */
		tickerProvider.connect();

		/**
		 * You can check, if websocket connection is open or not using the following
		 * method.
		 */
		boolean isConnected = tickerProvider.isConnectionOpen();
		System.out.println(isConnected);

		// After using the SmartAPI ticker, close websocket connection.
		// tickerProvider.disconnect();

	}
			
	public void smartWebSocketUsage(String clientId, String jwtToken, String apiKey, String actionType, String feedType)
			throws SmartAPIException {

		SmartWebsocket smartWebsocket = new SmartWebsocket(clientId, jwtToken, apiKey, actionType, feedType);

		smartWebsocket.setOnConnectedListener(new SmartWSOnConnect() {

			@Override
			public void onConnected() {

				smartWebsocket.runscript();
			}
		});

		smartWebsocket.setOnDisconnectedListener(new SmartWSOnDisconnect() {
			@Override
			public void onDisconnected() {
				System.out.println("onDisconnected");
			}
		});

		/** Set error listener to listen to errors. */
		smartWebsocket.setOnErrorListener(new SmartWSOnError() {
			@Override
			public void onError(Exception exception) {
				System.out.println("onError: " + exception.getMessage());
			}

			@Override
			public void onError(SmartAPIException smartAPIException) {
				System.out.println("onError: " + smartAPIException.getMessage());
			}

			@Override
			public void onError(String error) {
				System.out.println("onError: " + error);
			}
		});

		smartWebsocket.setOnTickerArrivalListener(new SmartWSOnTicks() {
			@Override
			public void onTicks(JSONArray ticks) {
				System.out.println("ticker data: " + ticks.toString());
			}
		});

		/**
		 * connects to SmartAPI ticker server for getting live quotes
		 */
		smartWebsocket.connect();

		/**
		 * You can check, if websocket connection is open or not using the following
		 * method.
		 */
		boolean isConnected = smartWebsocket.isConnectionOpen();
		System.out.println(isConnected);

		// After using the SmartAPI ticker, close websocket connection.
		// smartWebsocket.disconnect();

	}

```
For more details, take a look at Examples.java in the sample directory.

