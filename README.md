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

    Proxy proxy = Proxy.NO_PROXY;
    SmartConnect smartConnect = new SmartConnect(apiKey,proxy,TIME_OUT_IN_MILLIS);
	// Generate User Session
	User user = smartConnect.generateSession(clientId, clientPin, tOTP);
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
    public HttpResponse placeOrder(SmartConnect smartConnect) throws SmartAPIException, IOException {
            OrderParams orderParams = new OrderParams();
            orderParams.setDuration("DAY");
            orderParams.setQuantity(1);
            orderParams.setVariety("NORMAL");
            orderParams.setPrice(19500.0);
            orderParams.setTradingSymbol("SBIN-EQ");
            orderParams.setExchange("NSE");
            orderParams.setTransactionType("BUY");
            orderParams.setSymbolToken("3045");
            orderParams.setProductType("INTRADAY");
            orderParams.setOrderType("LIMIT");
            HttpResponse order = smartConnect.placeOrder(orderParams, "NORMAL");
            return order;
            }

	/** Modify order. */
    public HttpResponse modifyOrder(SmartConnect smartConnect, String orderid) throws SmartAPIException, IOException {
            // Order modify request will return order model which will contain only

            OrderParams orderParams = new OrderParams();
            orderParams.setDuration("DAY");
            orderParams.setQuantity(2);
            orderParams.setVariety("NORMAL");
            orderParams.setPrice(19500.0);
            orderParams.setTradingSymbol("SBIN-EQ");
            orderParams.setExchange("NSE");
            orderParams.setTransactionType("BUY");
            orderParams.setSymbolToken("3045");
            orderParams.setProductType("INTRADAY");
            orderParams.setOrderType("LIMIT");
            HttpResponse order = smartConnect.modifyOrder(orderid, orderParams, "NORMAL");
            return order;

            }

	/** Get order details */
    public HttpResponse cancelOrder(SmartConnect smartConnect, String orderid) throws SmartAPIException, IOException {
            HttpResponse order = smartConnect.cancelOrder(orderid, "NORMAL");
            return order;
    }

    public void getOrder(SmartConnect smartConnect) throws SmartAPIException, IOException {
            HttpResponse orders = smartConnect.getOrderHistory();
    }

    /**
     * Get last price for multiple instruments at once. USers can either pass
     * exchange with tradingsymbol or instrument token only. For example {NSE:NIFTY
     * 50, BSE:SENSEX} or {256265, 265}
     */
    public void getLTP(SmartConnect smartConnect) throws SmartAPIException, IOException {

            String exchange = "NSE";
            String symboltoken = "3045";
            HttpResponse ltpData = smartConnect.getLTP(exchange, SYMBOL_SBINEQ, symboltoken);
            }

	/** Get tradebook */
    public void getTrades(SmartConnect smartConnect) throws SmartAPIException, IOException {
            HttpResponse trades = smartConnect.getTrades();
            }
           


    /** Get RMS */
    public void getRMS(SmartConnect smartConnect) throws SmartAPIException, IOException {
            HttpResponse response = smartConnect.getRMS();
            }

	/** Get Holdings */
    public void getHolding(SmartConnect smartConnect) throws SmartAPIException, IOException {
            HttpResponse response = smartConnect.getHolding();
    }

	/** Get Position */
    public void getPosition(SmartConnect smartConnect) throws SmartAPIException, IOException {
            HttpResponse response = smartConnect.getPosition();
    }

	/** convert Position */
    public void convertPosition(SmartConnect smartConnect) throws SmartAPIException, IOException {

            TradeRequestDTO tradeRequestDTO = new TradeRequestDTO();
            tradeRequestDTO.setExchange("NSE");
            tradeRequestDTO.setSymbolToken("2885");
            tradeRequestDTO.setOldProductType("DELIVERY");
            tradeRequestDTO.setNewProductType("INTRADAY");
            tradeRequestDTO.setTradingSymbol("RELIANCE-EQ");
            tradeRequestDTO.setSymbolName("RELIANCE");
            tradeRequestDTO.setInstrumentType("");
            tradeRequestDTO.setPriceDen("1");
            tradeRequestDTO.setPriceNum("1");
            tradeRequestDTO.setGenDen("1");
            tradeRequestDTO.setGenNum("1");
            tradeRequestDTO.setPrecision("2");
            tradeRequestDTO.setMultiplier("-1");
            tradeRequestDTO.setBoardLotSize("1");
            tradeRequestDTO.setBuyQty("1");
            tradeRequestDTO.setSellQty("0");
            tradeRequestDTO.setBuyAmount("2235.80");
            tradeRequestDTO.setSellAmount("0");
            tradeRequestDTO.setTransactionType("BUY");
            tradeRequestDTO.setQuantity(1);
            tradeRequestDTO.setType("DAY");
            HttpResponse response = smartConnect.convertPosition(tradeRequestDTO);
            }
	
	/** Create Gtt Rule*/
    public HttpResponse createRule(SmartConnect smartConnect) throws SmartAPIException, IOException {
            GttParams gttParams = new GttParams();

            gttParams.setTradingSymbol(SYMBOL_SBINEQ);
            gttParams.setSymbolToken("3045");
            gttParams.setExchange("NSE");
            gttParams.setProductType(MARGIN);
            gttParams.setTransactionType("BUY");
            gttParams.setPrice(100000.01);
            gttParams.setQty(10);
            gttParams.setDisclosedQty(10);
            gttParams.setTriggerPrice(20000.1);
            gttParams.setTimePeriod(300);
            HttpResponse response = smartConnect.gttCreateRule(gttParams);
            return response;
    }

	
	/** Modify Gtt Rule */
    public HttpResponse modifyRule(SmartConnect smartConnect, String ruleID) throws SmartAPIException, IOException {
            GttParams gttParams = new GttParams();
            gttParams.setTradingSymbol(SYMBOL_SBINEQ);
            gttParams.setSymbolToken("3045");
            gttParams.setExchange("NSE");
            gttParams.setProductType(MARGIN);
            gttParams.setTransactionType("BUY");
            gttParams.setPrice(100000.1);
            gttParams.setQty(11);
            gttParams.setDisclosedQty(11);
            gttParams.setTriggerPrice(20000.1);
            gttParams.setTimePeriod(300);
            HttpResponse response = smartConnect.gttModifyRule(Integer.valueOf(ruleID), gttParams);
            return response;
            }
	
	/** Cancel Gtt Rule */
    public void cancelRule(SmartConnect smartConnect, String modifyRuleID) throws SmartAPIException, IOException {
            String symboltoken = "3045";
            String exchange = "NSE";
            HttpResponse gtt = smartConnect.gttCancelRule(Integer.valueOf(modifyRuleID), symboltoken, exchange);

    }
	
	/** Gtt Rule Details */
    public void ruleDetails(SmartConnect smartConnect, String modifyRuleID) throws SmartAPIException, IOException {
            HttpResponse gtt = smartConnect.gttRuleDetails(Integer.valueOf(modifyRuleID));
    }
	
	/** Gtt Rule Lists */
    public void ruleList(SmartConnect smartConnect) throws SmartAPIException, IOException {
            List<String> status = new ArrayList<>();
            status.add("NEW");
            status.add("CANCELLED");
            status.add("ACTIVE");
            status.add("SENTTOEXCHANGE");
            status.add("FORALL");
            Integer page = 1;
            Integer count = 10;
            HttpResponse gtt = smartConnect.gttRuleList(status, page, count);
    }

	/** Historic Data */
    public void getCandleData(SmartConnect smartConnect) throws SmartAPIException, IOException {
            StockHistoryRequestDTO requestDTO = new StockHistoryRequestDTO();
            requestDTO.setToDate("2021-03-10 11:00");
            requestDTO.setExchange("NSE");
            requestDTO.setInterval("FIVE_MINUTE");
            requestDTO.setSymbolToken("3045");
            requestDTO.setFromDate("2021-02-10 09:15");
            HttpResponse response = smartConnect.candleData(requestDTO);
            }
	
	/** Logout user. */
    public void logout(SmartConnect smartConnect) throws SmartAPIException, IOException {
            HttpResponse httpResponse = smartConnect.logout();
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

    Proxy proxy = Proxy.NO_PROXY;
    SmartConnect smartConnect = new SmartConnect(apiKey,proxy,TIME_OUT_IN_MILLIS);
    // Generate User Session
	User user = smartConnect.generateSession(clientId, clientPin, tOTP);
	smartConnect.setAccessToken(user.getAccessToken());
	smartConnect.setUserId(user.getUserId());
	
	// SmartStreamTicker
	String feedToken = user.getFeedToken();
	SmartStreamTicker ticker = new SmartStreamTicker(clientId, feedToken, new SmartStreamListener());
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

