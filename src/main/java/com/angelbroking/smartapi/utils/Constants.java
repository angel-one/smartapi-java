package com.angelbroking.smartapi.utils;


/**
 * Contains all the Strings that are being used in the Smart API Connect library.
 */
public class Constants {

    /**
     * Product types.
     */
    public static final String PRODUCT_INTRADAY = "INTRADAY";
    /**
     * Order types.
     */
    public static final String ORDER_TYPE_STOPLOSS_LIMIT = "STOPLOSS_LIMIT";
    /**
     * Variety types.
     */
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
    public static final String CLIENT_LIB_HEADER_VALUE = "JAVA";
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
    public static final int TIME_OUT_IN_MILLIS = 10000; // 10 seconds
    public static final String SMART_CONNECT_CLIENT_CODE = "clientcode";
    public static final String SMART_CONNECT_PASSWORD = "password";
    public static final String SMART_CONNECT_TOTP = "totp";
    public static final String SMART_CONNECT_DATA = "data";
    public static final String SMART_CONNECT_JWT_TOKEN = "jwtToken";
    public static final String SMART_CONNECT_REFRESH_TOKEN = "refreshToken";
    public static final String SMART_CONNECT_FEED_TOKEN = "feedToken";
    public static final String SMART_CONNECT_API_USER_PROFILE = "api.user.profile";
    public static final String NULL_URL_MESSAGE = "url cannot be null";
    public static final String INVALID_URL = "Invalid URL: ";
    public static final String API_REQUEST_FAILED_MSG = "Failed to create API request";
    public static final String SMART_API_EXCEPTION_ERROR_MSG = "The operation failed to execute because of a SmartAPIException error";
    public static final String IO_EXCEPTION_ERROR_MSG = "The operation failed to execute because of an IO error.";
    public static final String JSON_EXCEPTION_ERROR_MSG = "The operation failed to execute because of a JSON error";
    public static final String ID = "id";
    public static final String QTY = "qty";
    public static final String DISCLOSED_QTY = "disclosedqty";
    public static final String TIME_PERIOD = "timeperiod";
    public static final String ACCEPT = "accept";
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
    public static final String SMART_API_EXCEPTION_OCCURRED = "SmartAPIException occurred ";
    public static final String IO_EXCEPTION_OCCURRED = "IOException occurred ";
    public static final String JSON_EXCEPTION_OCCURRED = "JSONException occurred ";
    public static final String TICKER_NOT_CONNECTED = "ticker is not connected";
    public static final String TICKER_NOT_NULL_CONNECTED = "ticker is not connected";
    public static final String SYMBOL_SBINEQ = "SBIN-EQ";
    public static final String MARGIN = "MARGIN";
    public static final String PARAM_MODE = "mode";
    public static final String PARAM_TOKEN_LIST = "tokenList";
    public static final String PARAM_ACTION = "action";
    public static final String PARAM_PARAMS = "params";
    public static final String SMARTAPIREQUESTHANDLER_USER_AGENT = "javasmartapiconnect/3.0.0";
    public static boolean ENABLE_LOGGING = false;

    /**
     * Utils Constants
     */
    public static final String HEX_FORMAT = "%02x";
    public static final String MAC_ADDRESS_FORMAT = "%02X%s";
    public static final String URL_PROPERTY_KEY = "url";


    public static final String QUESTION_MARK = "?";
    public static final String AMPERSAND = "&";
    public static final String EQUALS = "=";
    public static final String JWT_TOKEN_PARAM = "jwttoken";
    public static final String CLIENT_CODE_PARAM = "clientcode";
    public static final String API_KEY_PARAM = "apikey";


    public static final int BEST_FIVE_DATA_SIZE = 200;
    public static final int BEST_FIVE_DATA_START_POSITION = 147;


    /**
     * LTP QUOTE SNAPQUOTE Constants
     */

    public static final int SEQUENCE_NUMBER_OFFSET = 27;
    public static final int EXCHANGE_FEED_TIME_OFFSET = 35;
    public static final int LAST_TRADED_PRICE_OFFSET = 43;
    public static final int SUBSCRIPTION_MODE = 0;
    public static final int EXCHANGE_TYPE = 1;
    public static final int LAST_TRADED_QTY_OFFSET = 51;
    public static final int AVG_TRADED_PRICE_OFFSET = 59;
    public static final int VOLUME_TRADED_TODAY_OFFSET = 67;
    public static final int TOTAL_BUY_QTY_OFFSET = 75;
    public static final int TOTAL_SELL_QTY_OFFSET = 83;
    public static final int OPEN_PRICE_OFFSET = 91;
    public static final int HIGH_PRICE_OFFSET = 99;
    public static final int LOW_PRICE_OFFSET = 107;
    public static final int CLOSE_PRICE_OFFSET = 115;
    public static final int LAST_TRADED_TIMESTAMP_OFFSET = 123;
    public static final int OPEN_INTEREST_OFFSET = 131;
    public static final int OPEN_INTEREST_CHANGE_PERC_OFFSET = 139;
    public static final int UPPER_CIRCUIT_OFFSET = 347;
    public static final int LOWER_CIRCUIT_OFFSET = 355;
    public static final int YEARLY_HIGH_PRICE_OFFSET = 363;
    public static final int YEARLY_LOW_PRICE_OFFSET = 371;



    public static final int BUY_START_POSITION = 147;
    public static final int SELL_START_POSITION = 247;
    public static final int NUM_PACKETS = 5;
    public static final int PACKET_SIZE = 20;
    public static final int BUY_SELL_FLAG_OFFSET = 0;
    public static final int QUANTITY_OFFSET = 2;
    public static final int PRICE_OFFSET = 10;
    public static final int NUMBER_OF_ORDERS_OFFSET = 18;
    public static final int PRICE_CONVERSION_FACTOR = 100;

    private Constants() {
    }



}
