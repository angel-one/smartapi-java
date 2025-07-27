# SmartAPI 2.2.6 Java client
The official Java client for communicating with [SmartAPI Connect API](https://smartapi.angelbroking.com).

SmartAPI is a set of REST-like APIs that expose many capabilities required to build a complete investment and trading platform. Execute orders in real-time, manage user portfolios, stream live market data (WebSockets), and more, with the simple HTTP API collection.

This project also includes a comprehensive **JavaFX GUI application** that provides a visual interface for most API features, including charting and strategy backtesting.

## Documentation
- [SmartAPI - HTTP API documentation](https://smartapi.angelbroking.com/docs)
- [Java library documentation](https://smartapi.angelbroking.com/docs)

## Usage of the Core Library
- [Download SmartAPI jar file](https://github.com/angel-one/smartapi-java/blob/main/dist/) and include it in your build path.

- Include com.angelbroking.smartapi into build path from maven. Use version 2.2.6

---

## SmartAPI Java GUI

This repository includes a powerful, feature-rich desktop application built with JavaFX. It provides a user-friendly interface to interact with the SmartAPI, visualize data, and backtest trading strategies.

### Features
- **Secure Login:** Authenticate using your Angel One credentials and TOTP.
- **Dashboard:** View real-time funds, positions, and holdings.
- **Live Order Book:** See all your open, executed, and rejected orders with options to modify or cancel.
- **Interactive Charting:**
    - View live and historical candlestick charts for any instrument.
    - Detach charts into separate windows for multi-monitor setups.
- **Strategy Backtesting:**
    - Test trading strategies (e.g., RSI, MACD) against historical data.
    - View detailed performance reports and equity curves.
    - Visualize trade entry/exit points directly on the chart.
- **Place & Modify Orders:** A simple interface to place new orders or modify existing ones.
- **Light & Dark Themes:** Switch between themes for comfortable viewing.

### Screenshots



**Login Window**

![Screenshot 2025-06-18 001113](https://github.com/user-attachments/assets/0def4c50-30fc-4c3e-8ca1-2511f64f61f0)


**Dashboard**
![Screenshot 2025-06-18 001852](https://github.com/user-attachments/assets/bb822727-957a-4a12-9d2a-ac03aff4bf92)


### How to Run the GUI

#### Option 1: Run from Source (Recommended for Developers)
This is the quickest way to run the application directly from the source code using Maven, without needing to package it first.

1.  Ensure you have JDK 11 or newer and Maven installed.
2.  Open a terminal in the project's root directory and run:
    ```bash
    mvn javafx:run
    ```
    The application will compile and launch automatically.

#### Option 2: Running the Executable JAR
After building the project with Maven, you can run the application directly from the command line.

1.  Build the project to create a "fat JAR":
    ```bash
    mvn clean package
    ```
2.  Run the JAR file (ensure you are using JDK 11 or newer):
    ```bash
    java -jar target/smartapi-java-2.2.6.jar
    ```

#### Option 3: Building a Native Windows Executable (.exe)
You can create a standalone `.exe` file that bundles the Java runtime, so users don't need to have Java installed.

1.  Ensure you have JDK 14 or newer and that it's added to your system's PATH.
2.  Build the fat JAR using Maven:
    ```bash
    mvn clean package
    ```
3.  Run `jpackage` to create the executable. Open a new terminal and run:
    ```powershell
    jpackage --type exe `
      --input target/ `
      --name "Smart API GUI" `
      --main-jar smartapi-java-2.2.6.jar `
      --main-class com.angelbroking.smartapi.gui.SmartApiGui `
      --dest dist `
      --win-console
    ```
4.  The final application will be located in the `dist/Smart API GUI` folder. You can zip this folder and distribute it.

---

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

        /** Get All Holdings */
        public void getAllHolding(SmartConnect smartConnect) throws SmartAPIException, IOException {
                // Returns Holdings.
                JSONObject response = smartConnect.getAllHolding();
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


    /** Search Scrip Data */
    public void getSearchScrip(SmartConnect smartConnect) throws SmartAPIException{
        JSONObject payload = new JSONObject();
        payload.put("exchange", "MCX");
        payload.put("searchscrip", "Crude");
        String response = smartConnect.getSearchScrip(payload);
    }
        
	/** Logout user. */

    /** Market Data  FULL*/
    public void getMarketData(SmartConnect smartConnect) {
      
        JSONObject payload = new JSONObject();
        payload.put("mode", "FULL");
        JSONObject exchangeTokens = new JSONObject();
        JSONArray nseTokens = new JSONArray();
        nseTokens.put("3045");
        exchangeTokens.put("NSE", nseTokens);
        payload.put("exchangeTokens", exchangeTokens);
        JSONObject response = smartConnect.marketData(payload);
        
        }

    /** Market Data  OHLC*/
    public void getMarketData(SmartConnect smartConnect) {

        JSONObject payload = new JSONObject();
        payload.put("mode", "OHLC");
        JSONObject exchangeTokens = new JSONObject();
        JSONArray nseTokens = new JSONArray();
        nseTokens.put("3045");
        exchangeTokens.put("NSE", nseTokens);
        payload.put("exchangeTokens", exchangeTokens);
        JSONObject response = smartConnect.marketData(payload);

        }

    /** Market Data  LTP*/
    public void getMarketData(SmartConnect smartConnect) {

        JSONObject payload = new JSONObject();
        payload.put("mode", "LTP");
        JSONObject exchangeTokens = new JSONObject();
        JSONArray nseTokens = new JSONArray();
        nseTokens.put("3045");
        exchangeTokens.put("NSE", nseTokens);
        payload.put("exchangeTokens", exchangeTokens);
        JSONObject response = smartConnect.marketData(payload);

        }

        /** Logout user. */
	public void logout(SmartConnect smartConnect) throws SmartAPIException, IOException {
		/** Logout user and kill the session. */
		JSONObject jsonObject = smartConnect.logout();
	}


/** Margin data. */
public void getMarginDetails(SmartConnect smartConnect) throws SmartAPIException, IOException {
        List<MarginParams> marginParamsList = new ArrayList<>();
        MarginParams marginParams = new MarginParams();
        marginParams.quantity = 1;
        marginParams.token = "12740";
        marginParams.exchange = Constants.EXCHANGE_NSE;
        marginParams.productType = Constants.PRODUCT_DELIVERY;
        marginParams.price = 0.0;
        marginParams.tradeType = Constants.TRADETYPE_BUY;

        marginParamsList.add(marginParams);
        JSONObject jsonObject = smartConnect.getMarginDetails(marginParamsList);
        System.out.println(jsonObject);
        }

        /** Get Individual Order */
        public void getIndividualOrder(SmartConnect smartConnect, String orderId) throws SmartAPIException, IOException {
            
                JSONObject jsonObject = smartConnect.getIndividualOrderDetails(orderId);
        }
}

```

## WebSocket live streaming data

```java

    /* Smart Stream */
	String clientCode = "client_code";
    User user = smartConnect.generateSession(clientCode, "<password>", "<totp>");
    String feedToken = user.getFeedToken();
    SmartStreamListener smartStreamListener = new SmartStreamListener() {
            @Override
            public void onLTPArrival(LTP ltp) {
                    System.out.println("ltp value==========>" + ltp.getExchangeType());
                    }
            
            @Override
            public void onQuoteArrival(Quote quote) {
            
                    }
            
            @Override
            public void onSnapQuoteArrival(SnapQuote snapQuote) {
            
                    }
            
            @Override
            public void onDepthArrival(Depth depth) {
            
                    }
            
            @Override
            public void onConnected() {
                    System.out.println("connected successfully");
                    }
            
            @Override
            public void onDisconnected() {
            
                    }
            
            @Override
            public void onError(SmartStreamError smartStreamError) {
            
                    }
            
            @Override
            public void onPong() {
            
                    }
            
            @Override
            public SmartStreamError onErrorCustom() {
                    return null;
                    }
    };

SmartStreamTicker smartStreamTicker = new SmartStreamTicker(clientCode,feedToken,smartStreamListener);
smartStreamTicker.connect();
Boolean connection =  smartStreamTicker.isConnectionOpen();

Set<TokenID> tokenSet = new HashSet<>();
tokenSet.add(new TokenID(ExchangeType.NSE_CM, "26000")); // NIFTY
tokenSet.add(new TokenID(ExchangeType.NSE_CM, "26009")); // NIFTY BANK
tokenSet.add(new TokenID(ExchangeType.BSE_CM, "19000"));

smartStreamTicker.subscribe(SmartStreamSubsMode.LTP,tokenSet);
smartStreamTicker.disconnect();
```
For more details, take a look at Examples.java in the sample directory.


## Order Websocket Data

```java

    /* Order Websocket */
    String userClientId = "<clientId>";
    User userGenerateSession = smartConnect.generateSession("<clientId>", "<password>", "<totp>");
    smartConnect.setAccessToken(userGenerateSession.getAccessToken());
    smartConnect.setUserId(userGenerateSession.getUserId());
    String accessToken = userGenerateSession.getAccessToken();
    
    examples.orderUpdateUsage(accessToken);

    /**
     * Order update websocket
     *
     * To retrieve order update websocket data
     * @param accessToken
     */
    public void orderUpdateUsage(String accessToken){
            OrderUpdateWebsocket orderUpdateWebsocket = new OrderUpdateWebsocket(accessToken, new OrderUpdateListner() {
    /**
     * Check if the websocket is connected or not
     */
    @Override
    public void onConnected() {
        
        log.info("order update websocket connected");
        
    }

    /**
     * Handle the onDisconnected event
     */
    @Override
    public void onDisconnected() {
        
        log.info("order update websocket disconnected");
        
    }

    /**
     * Handle the onError event
     * @param error
     */
    @Override
    public void onError(SmartStreamError error) {
        
        log.info("on error event");
        
    }

    /**
     * Handle the onPong event
     */
    @Override
    public void onPong() {
        
        log.info("or pong event");
        
    }

    /**
     * Handle the onOrderUpdate event
     * @param data
     */
    @Override
    public void onOrderUpdate(String data) {
        
        log.info("order update data {} ",data);
            
     }});            
    }

```
For more details, take a look at Examples.java in the sample directory.

