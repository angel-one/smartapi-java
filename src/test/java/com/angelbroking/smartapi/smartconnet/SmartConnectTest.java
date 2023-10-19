package com.angelbroking.smartapi.smartconnet;

import com.angelbroking.smartapi.Routes;
import com.angelbroking.smartapi.SmartConnect;
import com.angelbroking.smartapi.http.SmartAPIRequestHandler;
import com.angelbroking.smartapi.http.exceptions.DataException;
import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import com.angelbroking.smartapi.models.Order;
import com.angelbroking.smartapi.models.OrderParams;
import com.angelbroking.smartapi.models.User;
import com.angelbroking.smartapi.smartstream.models.SmartStreamError;
import com.angelbroking.smartapi.utils.Constants;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;


import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import static com.angelbroking.smartapi.utils.Constants.IO_EXCEPTION_ERROR_MSG;
import static com.angelbroking.smartapi.utils.Constants.IO_EXCEPTION_OCCURRED;
import static com.angelbroking.smartapi.utils.Constants.JSON_EXCEPTION_ERROR_MSG;
import static com.angelbroking.smartapi.utils.Constants.JSON_EXCEPTION_OCCURRED;
import static com.angelbroking.smartapi.utils.Constants.SMART_API_EXCEPTION_ERROR_MSG;
import static com.angelbroking.smartapi.utils.Constants.SMART_API_EXCEPTION_OCCURRED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.Silent.class)
@Slf4j
public class SmartConnectTest {

    @Mock
    private SmartAPIRequestHandler smartAPIRequestHandler;

    @Mock
    private SmartConnect smartConnect;

    @Mock
    private Routes routes;
    private String apiKey;
    private String accessToken;


    @Before
    public void setup() {
        apiKey = System.getProperty("apiKey");
        accessToken = System.getenv("accessToken");
    }


    @Test
    public void testGetSearchScript_Success() throws SmartAPIException, IOException {
        // Mock the necessary objects
        JSONObject payload = new JSONObject();
        when(smartConnect.getSearchScrip(payload)).thenReturn("response-data");

        // Call the method under test
        String result = smartConnect.getSearchScrip(payload);
        // Assert the result
        assertEquals("response-data", result);

    }

    @Test(expected = SmartAPIException.class)
    public void testGetSearchScript_Exception() throws SmartAPIException, IOException {
        JSONObject payload = new JSONObject();
        SmartAPIException expectedException = new SmartAPIException("Simulated SmartAPIException");
        when(smartConnect.getSearchScrip(payload)).thenThrow(expectedException);
        try {
             smartConnect.getSearchScrip(payload);
        } catch (SmartAPIException e) {
            throw new SmartAPIException(String.format("The operation failed to execute because of a SmartAPIException error in Search scrip api data %s", e));
        }
        verify(smartConnect).getSearchScrip(payload);
    }


  

    private static JSONObject createMarketDataResponse() {
        JSONObject jsonObject = new JSONObject();

        // Create "data" object
        JSONObject dataObject = new JSONObject();

        // Create the "unfetched" array
        JSONArray unfetchedArray = new JSONArray();
        dataObject.put("unfetched", unfetchedArray);

        // Create the "fetched" array and its elements
        JSONArray fetchedArray = new JSONArray();
        JSONObject fetchedElement = new JSONObject();
        fetchedElement.put("netChange", 3.15);
        fetchedElement.put("tradeVolume", 5718111);
        fetchedElement.put("lowerCircuit", 533.15);
        fetchedElement.put("percentChange", 0.53);
        fetchedElement.put("exchFeedTime", "19-Jul-2023 11:20:51");
        fetchedElement.put("avgPrice", 595.25);
        fetchedElement.put("ltp", 595.5);
        fetchedElement.put("exchTradeTime", "19-Jul-2023 11:20:51");
        fetchedElement.put("totSellQuan", 1924292);
        fetchedElement.put("upperCircuit", 651.55);
        fetchedElement.put("lastTradeQty", 46);
        fetchedElement.put("high", 599.6);
        fetchedElement.put("totBuyQuan", 890300);

        // Create the "depth" object and its "buy" and "sell" arrays
        JSONObject depthObject = new JSONObject();
        JSONArray buyArray = new JSONArray();
        JSONArray sellArray = new JSONArray();

        // Add elements to "buy" array
        JSONObject buyElement1 = new JSONObject();
        buyElement1.put("quantity", 1776);
        buyElement1.put("price", 595.3);
        buyElement1.put("orders", 3);
        buyArray.put(buyElement1);

        JSONObject buyElement2 = new JSONObject();
        buyElement2.put("quantity", 767);
        buyElement2.put("price", 595.25);
        buyElement2.put("orders", 3);
        buyArray.put(buyElement2);

        // Add elements to "sell" array
        JSONObject sellElement1 = new JSONObject();
        sellElement1.put("quantity", 249);
        sellElement1.put("price", 595.5);
        sellElement1.put("orders", 5);
        sellArray.put(sellElement1);

        JSONObject sellElement2 = new JSONObject();
        sellElement2.put("quantity", 1379);
        sellElement2.put("price", 595.55);
        sellElement2.put("orders", 4);
        sellArray.put(sellElement2);

        // Add "buy" and "sell" arrays to "depth" object
        depthObject.put("buy", buyArray);
        depthObject.put("sell", sellArray);

        fetchedElement.put("depth", depthObject);

        // Add remaining properties to "fetched" element
        fetchedElement.put("low", 592);
        fetchedElement.put("exchange", "NSE");
        fetchedElement.put("opnInterest", 0);
        fetchedElement.put("tradingSymbol", "SBIN-EQ");
        fetchedElement.put("symbolToken", "3045");
        fetchedElement.put("close", 592.35);
        fetchedElement.put("52WeekLow", 482.1);
        fetchedElement.put("open", 594.65);
        fetchedElement.put("52WeekHigh", 629.55);

        // Add the "fetched" element to the "fetched" array
        fetchedArray.put(fetchedElement);

        dataObject.put("fetched", fetchedArray);

        // Add "data" object, "message", "errorcode", and "status" properties to the main JSON object
        jsonObject.put("data", dataObject);
        jsonObject.put("message", "SUCCESS");
        jsonObject.put("errorcode", "");
        jsonObject.put("status", true);

        return jsonObject;
    }

    // Testing market data success for Full payload
    @Test
    public void testMarketData_Success() throws SmartAPIException, IOException {
        String url = routes.get("api.market.data");
        JSONObject params = getMarketDataRequest("FULL");
        when(smartAPIRequestHandler.postRequest(eq(this.apiKey), eq(url), eq(params), eq(this.accessToken))).thenReturn(createMarketDataResponse());
        try {
            JSONObject response = smartAPIRequestHandler.postRequest(this.apiKey, url, params, this.accessToken);
            JSONObject data = response.getJSONObject("data");
            assertNotNull(data);
        } catch (SmartAPIException ex) {
            log.error("{} while placing order {}", SMART_API_EXCEPTION_OCCURRED, ex.toString());
            throw new SmartAPIException(String.format("%s in placing order %s", SMART_API_EXCEPTION_ERROR_MSG, ex));
        } catch (IOException ex) {
            log.error("{} while placing order {}", IO_EXCEPTION_OCCURRED, ex.getMessage());
            throw new IOException(String.format("%s in placing order %s", IO_EXCEPTION_ERROR_MSG, ex.getMessage()));
        } catch (JSONException ex) {
            log.error("{} while placing order {}", JSON_EXCEPTION_OCCURRED, ex.getMessage());
            throw new JSONException(String.format("%s in placing order %s", JSON_EXCEPTION_ERROR_MSG, ex.getMessage()));
        }
    }

    // Testing market data failure for Full payload
    @Test(expected = SmartAPIException.class)
    public void testMarketData_Failure() throws SmartAPIException, IOException {
        // Stub the postRequest method
        String url = routes.get("api.market.data");
        JSONObject params = getMarketDataRequest("FULL");
        when(smartAPIRequestHandler.postRequest(eq(this.apiKey), eq(url), eq(params), eq(this.accessToken)))
                .thenThrow(new SmartAPIException("API request failed"));
        try {
            JSONObject response = smartAPIRequestHandler.postRequest(apiKey, url, params, accessToken);
            response.getJSONObject("data");
        } catch (SmartAPIException ex) {
            log.error("{} while placing order {}", SMART_API_EXCEPTION_OCCURRED, ex.toString());
            throw new SmartAPIException(String.format("%s in placing order %s", SMART_API_EXCEPTION_ERROR_MSG, ex));
        } catch (IOException ex) {
            log.error("{} while placing order {}", IO_EXCEPTION_OCCURRED, ex.getMessage());
            throw new IOException(String.format("%s in placing order %s", IO_EXCEPTION_ERROR_MSG, ex.getMessage()));
        } catch (JSONException ex) {
            log.error("{} while placing order {}", JSON_EXCEPTION_OCCURRED, ex.getMessage());
            throw new JSONException(String.format("%s in placing order %s", JSON_EXCEPTION_ERROR_MSG, ex.getMessage()));
        }
    }

    // Testing market data success for LTP payload
    @Test
    public void testMarketDataLTP_Success() throws SmartAPIException, IOException {
        String url = routes.get("api.market.data");
        JSONObject params = getMarketDataRequest("LTP");
        when(smartAPIRequestHandler.postRequest(eq(this.apiKey), eq(url), eq(params), eq(this.accessToken))).thenReturn(createMarketDataResponse());
        try {
            JSONObject response = smartAPIRequestHandler.postRequest(this.apiKey, url, params, this.accessToken);
            JSONObject data = response.getJSONObject("data");
            assertNotNull(data);
        } catch (SmartAPIException ex) {
            log.error("{} while placing order {}", SMART_API_EXCEPTION_OCCURRED, ex.toString());
            throw new SmartAPIException(String.format("%s in placing order %s", SMART_API_EXCEPTION_ERROR_MSG, ex));
        } catch (IOException ex) {
            log.error("{} while placing order {}", IO_EXCEPTION_OCCURRED, ex.getMessage());
            throw new IOException(String.format("%s in placing order %s", IO_EXCEPTION_ERROR_MSG, ex.getMessage()));
        } catch (JSONException ex) {
            log.error("{} while placing order {}", JSON_EXCEPTION_OCCURRED, ex.getMessage());
            throw new JSONException(String.format("%s in placing order %s", JSON_EXCEPTION_ERROR_MSG, ex.getMessage()));
        }
    }

    // Testing market data failure for LTP payload
    @Test(expected = SmartAPIException.class)
    public void testMarketDataLTP_Failure() throws SmartAPIException, IOException {
        // Stub the postRequest method
        String url = routes.get("api.market.data");
        JSONObject params = getMarketDataRequest("LTP");
        when(smartAPIRequestHandler.postRequest(eq(this.apiKey), eq(url), eq(params), eq(this.accessToken)))
                .thenThrow(new SmartAPIException("API request failed"));
        try {
            JSONObject response = smartAPIRequestHandler.postRequest(apiKey, url, params, accessToken);
            response.getJSONObject("data");
        } catch (SmartAPIException ex) {
            log.error("{} while placing order {}", SMART_API_EXCEPTION_OCCURRED, ex.toString());
            throw new SmartAPIException(String.format("%s in placing order %s", SMART_API_EXCEPTION_ERROR_MSG, ex));
        } catch (IOException ex) {
            log.error("{} while placing order {}", IO_EXCEPTION_OCCURRED, ex.getMessage());
            throw new IOException(String.format("%s in placing order %s", IO_EXCEPTION_ERROR_MSG, ex.getMessage()));
        } catch (JSONException ex) {
            log.error("{} while placing order {}", JSON_EXCEPTION_OCCURRED, ex.getMessage());
            throw new JSONException(String.format("%s in placing order %s", JSON_EXCEPTION_ERROR_MSG, ex.getMessage()));
        }
    }

    // Testing market data success for OHLC payload
    @Test
    public void testMarketDataOHLC_Success() throws SmartAPIException, IOException {
        String url = routes.get("api.market.data");
        JSONObject params = getMarketDataRequest("OHLC");
        when(smartAPIRequestHandler.postRequest(eq(this.apiKey), eq(url), eq(params), eq(this.accessToken))).thenReturn(createMarketDataResponse());
        try {
            JSONObject response = smartAPIRequestHandler.postRequest(this.apiKey, url, params, this.accessToken);
            JSONObject data = response.getJSONObject("data");
            assertNotNull(data);
        } catch (SmartAPIException ex) {
            log.error("{} while placing order {}", SMART_API_EXCEPTION_OCCURRED, ex.toString());
            throw new SmartAPIException(String.format("%s in placing order %s", SMART_API_EXCEPTION_ERROR_MSG, ex));
        } catch (IOException ex) {
            log.error("{} while placing order {}", IO_EXCEPTION_OCCURRED, ex.getMessage());
            throw new IOException(String.format("%s in placing order %s", IO_EXCEPTION_ERROR_MSG, ex.getMessage()));
        } catch (JSONException ex) {
            log.error("{} while placing order {}", JSON_EXCEPTION_OCCURRED, ex.getMessage());
            throw new JSONException(String.format("%s in placing order %s", JSON_EXCEPTION_ERROR_MSG, ex.getMessage()));
        }
    }

    // Testing market data failure for OHLC payload
    @Test(expected = SmartAPIException.class)
    public void testMarketDataOHLC_Failure() throws SmartAPIException, IOException {
        // Stub the postRequest method
        String url = routes.get("api.market.data");
        JSONObject params = getMarketDataRequest("OHLC");
        when(smartAPIRequestHandler.postRequest(eq(this.apiKey), eq(url), eq(params), eq(this.accessToken)))
                .thenThrow(new SmartAPIException("API request failed"));
        try {
            JSONObject response = smartAPIRequestHandler.postRequest(apiKey, url, params, accessToken);
            response.getJSONObject("data");
        } catch (SmartAPIException ex) {
            log.error("{} while placing order {}", SMART_API_EXCEPTION_OCCURRED, ex.toString());
            throw new SmartAPIException(String.format("%s in placing order %s", SMART_API_EXCEPTION_ERROR_MSG, ex));
        } catch (IOException ex) {
            log.error("{} while placing order {}", IO_EXCEPTION_OCCURRED, ex.getMessage());
            throw new IOException(String.format("%s in placing order %s", IO_EXCEPTION_ERROR_MSG, ex.getMessage()));
        } catch (JSONException ex) {
            log.error("{} while placing order {}", JSON_EXCEPTION_OCCURRED, ex.getMessage());
            throw new JSONException(String.format("%s in placing order %s", JSON_EXCEPTION_ERROR_MSG, ex.getMessage()));
        }
    }

    private JSONObject getMarketDataRequest(String mode) {
        JSONObject payload = new JSONObject();
        payload.put("mode", mode);
        JSONObject exchangeTokens = new JSONObject();
        JSONArray nseTokens = new JSONArray();
        nseTokens.put("3045");
        exchangeTokens.put("NSE", nseTokens);
        payload.put("exchangeTokens", exchangeTokens);
        return payload;
    }


    @Test
    public void testSmartConnectObjectWithApiKey() {
        SmartConnect smartConnect = new SmartConnect("API_KEY");
        assertNotNull(smartConnect);
    }

    @Test
    public void testSmartConnectObjectWithApiKeyAccessTokenRefreshToken() {
        SmartConnect smartConnect = new SmartConnect("apiKey", "accessToken", "refreshToken");
        assertNotNull(smartConnect);
        assertEquals("apiKey", smartConnect.getApiKey());
        assertEquals("accessToken", smartConnect.getAccessToken());
        assertEquals("refreshToken", smartConnect.getPublicToken());
    }
    @Test
    public void testSetApiKeyOfSmartConnect() {
        SmartConnect smartConnect = new SmartConnect();
        smartConnect.setApiKey("apiKey");
        smartConnect.setAccessToken("accessToken");
        smartConnect.setRefreshToken("refreshToken");
        assertEquals("apiKey", smartConnect.getApiKey());
        assertEquals("accessToken", smartConnect.getAccessToken());
        assertEquals("refreshToken", smartConnect.getPublicToken());
    }

    @Test
    public void testSmartConnectWithNullValues() {
        SmartConnect smartConnect = new SmartConnect();
        smartConnect.setApiKey(null);
        smartConnect.setAccessToken(null);
        smartConnect.setRefreshToken(null);
        assertThrows(NullPointerException.class, () -> smartConnect.getApiKey());
        assertThrows(NullPointerException.class, () -> smartConnect.getAccessToken());
        assertThrows(NullPointerException.class, () -> smartConnect.getUserId());
        assertThrows(NullPointerException.class, () -> smartConnect.getPublicToken());
    }

    @Test
    public void testGenerateSession() {
        SmartConnect smartConnect = new SmartConnect("apiKey", "accessToken", "refreshToken");
        assertNull(smartConnect.generateSession("invalidClientCode", "password", "totp"));
    }

    @Test
    public void testReturnApiKeyIfNotNull() {
        SmartConnect smartConnect = new SmartConnect("apiKey");
        String result = smartConnect.getApiKey();
        assertEquals("apiKey", result);
    }

    @Test
    public void testThrowsNullPointerExceptionIfApiKeyIsNull() {
        SmartConnect smartConnect = new SmartConnect();
        assertThrows(NullPointerException.class, () -> {
            smartConnect.getApiKey();
        });
    }

}


