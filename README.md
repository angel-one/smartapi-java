# Smart API 1.0 Java client
The official Java client for communicating with [Smart API Connect API](https://smartapi.angelbroking.com).

Smart API is a set of REST-like APIs that expose many capabilities required to build a complete investment and trading platform. Execute orders in real time, manage user portfolio, stream live market data (WebSockets), and more, with the simple HTTP API collection.

## Documentation
- [Smart API - HTTP API documentation] (https://smartapi.angelbroking.com/docs)
- [Java library documentation](https://smartapi.angelbroking.com/docs/connect)

## Usage
- [Download SmartAPI jar file](https://github.com/angelbroking-github/smartapi-java/blob/main/dist) and include it in your build path.

- Include com.angelbroking.smartapi into build path from maven. Use version 1.0.0

## API usage
```java
	
	// Initialize SmartAPI
	String apiKey = "<apiKey>"; // PROVIDE YOUR API KEY HERE
	String clientId = "<clientId>"; // PROVIDE YOUR Client ID HERE
	String clientPin = "<clientPin>"; // PROVIDE YOUR Client PIN HERE
	String tOTP = "<tOTP>"; // PROVIDE THE CODE DISPLAYED ON YOUR AUTHENTICATOR APP - https://smartapi.angelbroking.com/enable-totp

	SmartConnect smartConnect = new SmartConnect(apiKey);
	// Generate User Session
	User user = smartConnect.generateSession(clientId, clientPin, tOTP);
	smartConnect.setAccessToken(user.getAccessToken());
	smartConnect.setUserId(user.getUserId());
	
	// Set session expiry callback.
	smartConnect.setSessionExpiryHook(new SessionExpiryHook() {
	@Override
	public void sessionExpired() {
		logger.info("session expired");
	}
	});
	
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
		// Order modify request will return order model which will contain only

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
    
	public void cancelOrder(SmartConnect smartConnect) throws SmartAPIException, IOException {
		// Order modify request will return order model which will contain only
		// order_id.
		// Cancel order will return order model which will only have orderId.
		Order order = smartConnect.cancelOrder("201009000000015", Constants.VARIETY_REGULAR);
	}

	/** Get order details */
    public void getOrder(SmartConnect smartConnect) throws SmartAPIException, IOException {
        List<Order> orders = smartConnect.getOrderHistory(smartConnect.getUserId());
        Iterator<Order> orderIterator = orders.iterator();
        while (orderIterator.hasNext()) {
            Order order = orderIterator.next();
            logger.info(order.orderId + " " + order.status);
        }
    }


    /**
     * Get last price for multiple instruments at once. USers can either pass
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
            Iterator<Trade> iterator = trades.iterator();
            while (iterator.hasNext()) {
            Trade trade = iterator.next();
            logger.info(trade.tradingSymbol + " " + trades.size());
            }
            }


    /** Get RMS */
	public void getRMS(SmartConnect smartConnect) throws SmartAPIException, IOException {
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
		/** Logout user and kill session. */
		JSONObject jsonObject = smartConnect.logout();
	}
	
```
For more details, take a look at Examples.java in sample directory.

## WebSocket live streaming data

```java

	// Initialize SmartAPI
	String apiKey = "<apiKey>"; // PROVIDE YOUR API KEY HERE
	String clientId = "<clientId>"; // PROVIDE YOUR Client ID HERE
	String clientPin = "<clientPin>"; // PROVIDE YOUR Client PIN HERE
	String tOTP = "<tOTP>"; // PROVIDE THE CODE DISPLAYED ON YOUR AUTHENTICATOR APP - https://smartapi.angelbroking.com/enable-totp

	SmartConnect smartConnect = new SmartConnect(apiKey);
	// Generate User Session
	User user = smartConnect.generateSession(clientId, clientPin, tOTP);
	smartConnect.setAccessToken(user.getAccessToken());
	smartConnect.setUserId(user.getUserId());
	
	// SmartStreamTicker
	String feedToken = user.getFeedToken();
	SmartStreamTicker ticker = new SmartStreamTicker(clientId, feedToken, new SmartStreamListenerImpl());
	ticker.connect();
	ticker.subscribe(SmartStreamSubsMode.QUOTE, getTokens());
	Thread.currentThread().join();
	
	
	// find out the required token from:
	// https://margincalculator.angelbroking.com/OpenAPI_File/files/OpenAPIScripMaster.json
	private static Set<TokenID> getTokens() {
		Set<TokenID> tokenSet = new HashSet<>();
		tokenSet.add(new TokenID(ExchangeType.NSE_CM, "26009")); // NIFTY BANK
		tokenSet.add(new TokenID(ExchangeType.NSE_CM, "1594")); // NSE Infosys
		tokenSet.add(new TokenID(ExchangeType.NCX_FO, "GUARGUM5")); // GUAREX (NCDEX)
		return tokenSet;
	}
	
```
For more details, take a look at Examples.java in sample directory.

