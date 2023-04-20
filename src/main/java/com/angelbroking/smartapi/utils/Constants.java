package com.angelbroking.smartapi.utils;


/**
 * Contains all the Strings that are being used in the Smart API Connect library.
 */
public class Constants {

    // Private constructor to prevent instantiation from outside the class
    private Constants() {
        // This constructor is intentionally left blank
    }

    /**
     * Product types.
     */
    public static final String PRODUCT_DELIVERY = "DELIVERY";
    public static final String PRODUCT_INTRADAY = "INTRADAY";


    /**
     * Order types.
     */
    public static final String ORDER_TYPE_LIMIT = "LIMIT";
    public static final String ORDER_TYPE_STOPLOSS_LIMIT = "STOPLOSS_LIMIT";

    /**
     * Variety types.
     */
    public static final String VARIETY_NORMAL = "NORMAL";
    public static final String VARIETY_STOPLOSS = "STOPLOSS";

    /**
     * Transaction types.
     */
    public static final String TRANSACTION_TYPE_BUY = "BUY";

    /**
     * Duration types.
     */
    public static final String DURATION_DAY = "DAY";

    /**
     * Exchanges.
     */
    public static final String EXCHANGE_NSE = "NSE";


    /**
     * SmartStream Routes
     */
    public static final String ROOT_URL = "https://apiconnect.angelbroking.com";
    public static final String LOGIN_URL = "https://apiconnect.angelbroking.com/rest/auth/angelbroking/user/v1/loginByPassword";
    public static final String WSURI = "wss://wsfeeds.angelbroking.com/NestHtml5Mobile/socket/stream";
    public static final String SMARTSTREAM_WSURI = "ws://smartapisocket.angelone.in/smart-stream";
    public static final String SWSURI = "wss://smartapisocket.angelbroking.com/websocket";

    /**
     * User Model Constants
     */
    public static final String USER_DATA = "data";
    public static final String USER_PRODUCTS = "products";
    public static final String USER_EXCHANGES = "exchanges";


    /**
     * Smart WebSocket Constants
     */
    public static final String ACTION_TYPE = "actiontype";
    public static final String FEEED_TYPE = "feedtype";
    public static final String JWT_TOKEN = "jwttoken";
    public static final String CLIENT_CODE = "clientcode";
    public static final String API_KEY = "apikey";

    /**
     * SmartAPITicker Constants
     */
    public static final String SMART_API_TICKER_TASK = "task";
    public static final String SMART_API_TICKER_CHANNEL = "channel";
    public static final String SMART_API_TICKER_TOKEN = "token";
    public static final String SMART_API_TICKER_USER = "user";
    public static final String SMART_API_TICKER_ACCTID = "acctid";

    /**
     * SmartStreamTicker Constants
     */
    public static final int PING_INTERVAL = 10000; // 10 seconds
    public static final String CLIENT_ID_HEADER = "x-client-code";
    public static final String FEED_TOKEN_HEADER = "x-feed-token";
    public static final String CLIENT_LIB_HEADER = "x-client-lib";


    /**
     * SmartConnect Routes Constants
     */
    public static final String SMART_CONNECT_API_ORDER_PLACE = "api.order.place";
    public static final String SMART_CONNECT_API_ORDER_MODIFY = "api.order.modify";
    public static final String SMART_CONNECT_API_ORDER_CANCEL = "api.order.cancel";
    public static final String SMART_CONNECT_API_ORDER_BOOK = "api.order.book";
    public static final String SMART_CONNECT_API_LTP_DATA = "api.ltp.data";
    public static final String SMART_CONNECT_API_ORDER_TRADE_BOOK = "api.order.trade.book";
    public static final String SMART_CONNECT_API_ORDER_RMS_DATA = "api.order.rms.data";
    public static final String SMART_CONNECT_API_ORDER_RMS_HOLDING = "api.order.rms.holding";
    public static final String SMART_CONNECT_API_ORDER_RMS_POSITION = "api.order.rms.position";
    public static final String SMART_CONNECT_API_ORDER_RMS_POSITION_CONVERT = "api.order.rms.position.convert";
    public static final String SMART_CONNECT_API_GTT_CREATE = "api.gtt.create";
    public static final String SMART_CONNECT_API_GTT_MODIFY = "api.gtt.modify";
    public static final String SMART_CONNECT_API_GTT_CANCEL = "api.gtt.cancel";
    public static final String SMART_CONNECT_API_GTT_DETAILS = "api.gtt.details";
    public static final String SMART_CONNECT_API_GTT_LIST = "api.gtt.list";
    public static final String SMART_CONNECT_API_CANDLE_DATA = "api.candle.data";
    public static final String SMART_CONNECT_API_USER_LOGOUT = "api.user.logout";

    /**
     * Order model Constants
     */
    public static final String EXCHANGE = "exchange";
    public static final String TRADING_SYMBOL = "tradingsymbol";
    public static final String TRANSACTION_TYPE = "transactiontype";
    public static final String QUANTITY = "quantity";
    public static final String PRICE = "price";
    public static final String PRODUCT_TYPE = "producttype";
    public static final String ORDER_TYPE = "ordertype";
    public static final String DURATION = "duration";
    public static final String SYMBOL_TOKEN = "symboltoken";
    public static final String SQUARE_OFF = "squareoff";
    public static final String STOP_LOSS = "stoploss";
    public static final String TRIGGER_PRICE = "triggerprice";
    public static final String VARIETY = "variety";
    public static final String ORDER_ID = "orderid";

    /**
     * ruleList Constants
     */
    public static final String STATUS = "status";
    public static final String PAGE = "page";
    public static final String COUNT = "count";


    /**
     * SmartConnect Constants
     */
    public static final String SMART_CONNECT_CLIENT_CODE = "clientcode";
    public static final String SMART_CONNECT_PASSWORD = "password";
    public static final String SMART_CONNECT_TOTP = "totp";
    public static final String SMART_CONNECT_DATA = "data";
    public static final String SMART_CONNECT_API_REFRESH = "api.refresh";
    public static final String SMART_CONNECT_JWT_TOKEN = "jwtToken";
    public static final String SMART_CONNECT_REFRESH_TOKEN = "refreshToken";
    public static final String SMART_CONNECT_FEED_TOKEN = "feedToken";
    public static final String SMART_CONNECT_API_USER_PROFILE = "api.user.profile";
    public static final String SMART_CONNECT_CHECKSUM = "checksum";
    private String orderID = "orderid";

    public static final String ID = "id";
    public static final String QTY = "qty";
    public static final String DISCLOSED_QTY = "disclosedqty";
    public static final String TIME_PERIOD = "timeperiod";

    public static final String MAC_ADDRESS = "macAddress";
    public static final String ACCEPT = "accept";
    public static final String USER_TYPE = "userType";
    public static final String SOURCE_ID = "sourceID";

    public static final String USER_AGENT = "User-Agent";
    public static final String SMART_API_VERSION = "X-Smart API-Version";
    public static final String AUTHORIZATION = "Authorization";

    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CLIENT_LOCAL_IP = "X-ClientLocalIP";
    public static final String CLIENT_PUBLIC_IP = "X-ClientPublicIP";
    public static final String PRIVATE_KEY = "X-PrivateKey";
    public static final String TOKEN = "token ";
    public static final String APPLICATION_JSON_UTF8 = "application/json; charset=utf-8";
    public static final String APPLICATION_JSON = "application/json";
    public static final String X_MAC_ADDRESS = "X-MACAddress";
    public static final String X_USER_TYPE = "X-UserType";
    public static final String X_SOURCE_ID = "X-SourceID";
    public static final String MESSAGE = "message";


    public static final String HEADER_CLIENT_LOCAL_IP = "clientLocalIP";
    public static final String HEADER_CLIENT_PUBLIC_IP = "clientPublicIP";

    public static final String SMART_API_EXCEPTION_OCCURRED = "SmartAPIException occurred: ";
    public static final String IO_EXCEPTION_OCCURRED = "IOException occurred: ";
    public static final String JSON_EXCEPTION_OCCURRED = "JSONException occurred: ";
    public static final String TICKER_NOT_CONNECTED = "ticker is not connected";
    public static final String TICKER_NOT_NULL_CONNECTED = "ticker is not connected";


    public static final String SYMBOL_SBINEQ = "SBIN-EQ";
    public static final String MARGIN = "MARGIN";


    public static final String PARAM_MODE = "mode";
    public static final String PARAM_TOKEN_LIST = "tokenList";
    public static final String PARAM_ACTION = "action";
    public static final String PARAM_PARAMS = "params";


    public static final String SMARTAPIREQUESTHANDLER_USER_AGENT = "javasmartapiconnect/3.0.0";


}
