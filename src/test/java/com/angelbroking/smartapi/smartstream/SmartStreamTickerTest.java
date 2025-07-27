package com.angelbroking.smartapi.smartstream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

// import lombok.extern.slf4j.Slf4j; // Removed Lombok import
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.angelbroking.smartapi.SmartConnect;
import com.angelbroking.smartapi.models.User;
import com.angelbroking.smartapi.smartstream.models.ExchangeType;
import com.angelbroking.smartapi.smartstream.models.SmartStreamSubsMode;
import com.angelbroking.smartapi.smartstream.models.TokenID;
import com.angelbroking.smartapi.smartstream.ticker.SmartStreamTicker;
import com.neovisionaries.ws.client.WebSocketException;
// @Slf4j // Removed Lombok annotation
public class SmartStreamTickerTest {
	private static final Logger log = LoggerFactory.getLogger(SmartStreamTickerTest.class);

	private static String clientID;
	private static String clientPass;
	private static String apiKey;
	private static String feedToken;
	private static String totp;

	@BeforeAll
	public static void initClass() throws InterruptedException {
		clientID = System.getProperty("clientID");
		clientPass = System.getProperty("clientPass");
		apiKey = System.getProperty("apiKey");

		Scanner sc = new Scanner(System.in);
		log.info("enter totp: ");
		totp = sc.nextLine();

		SmartConnect smartConnect = new SmartConnect(apiKey);
		User user = smartConnect.generateSession(clientID, clientPass, totp);
		feedToken = user.getFeedToken();
//		feedToken = "123";
	}

	@Test
	void testSmartStreamTicketLTP() throws WebSocketException, InterruptedException {
		try {
			SmartStreamTicker ticker = new SmartStreamTicker(clientID, feedToken, new SmartStreamListenerImpl());
			ticker.connect();
			ticker.subscribe(SmartStreamSubsMode.QUOTE, getTokens());
//			ticker.subscribe(SmartStreamSubsMode.SNAP_QUOTE, getTokens());
			// uncomment the below line to allow test thread to keep running so that ticks
			// can be received in the listener
//			Thread.sleep(5000);
			ticker.disconnect();
			log.info("isConnected = "+ticker.isConnectionOpen());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}

	@Test
	void testSmartStreamTicket_WithInitialDelayAndPeriod() throws WebSocketException, InterruptedException {
		try {
			SmartStreamTicker ticker = new SmartStreamTicker(clientID, feedToken, new SmartStreamListenerImpl(), 6000, 6000);
			ticker.connect();
			ticker.subscribe(SmartStreamSubsMode.SNAP_QUOTE, getTokens());
//			ticker.subscribe(SmartStreamSubsMode.SNAP_QUOTE, getTokens());
			// uncomment the below line to allow test thread to keep running so that ticks
			// can be received in the listener
//			Thread.sleep(5000);
			ticker.disconnect();
			System.out.println("isConnected = "+ticker.isConnectionOpen());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}

	private Set<TokenID> getTokens(){
		// find out the required token from https://margincalculator.angelbroking.com/OpenAPI_File/files/OpenAPIScripMaster.json
		Set<TokenID> tokenSet = new HashSet<>();
		tokenSet.add(new TokenID(ExchangeType.NSE_CM, "26000")); // NIFTY
		tokenSet.add(new TokenID(ExchangeType.NSE_CM, "26009")); // NIFTY BANK
		tokenSet.add(new TokenID(ExchangeType.BSE_CM, "19000")); // Sensex

		tokenSet.add(new TokenID(ExchangeType.NSE_CM, "99926000")); // NIFTY
		tokenSet.add(new TokenID(ExchangeType.NSE_CM, "99926009")); // NIFTY BANK
		tokenSet.add(new TokenID(ExchangeType.BSE_CM, "99919000")); // Sensex

		tokenSet.add(new TokenID(ExchangeType.NSE_CM, "1594")); // NSE Infosys
		tokenSet.add(new TokenID(ExchangeType.NSE_FO, "35003")); // Nifty June 2023 FUT
		tokenSet.add(new TokenID(ExchangeType.CDE_FO, "1185")); // 1185 USDINR
		tokenSet.add(new TokenID(ExchangeType.BSE_CM, "532540")); // BSE TCS
		tokenSet.add(new TokenID(ExchangeType.NCX_FO, "GUARGUM5")); // GUAREX (NCDEX)
		tokenSet.add(new TokenID(ExchangeType.MCX_FO, "252453")); //CRUDEOIL
		return tokenSet;
	}

	@Test
	void testTokenID() {
		TokenID t1 = new TokenID(ExchangeType.NSE_CM, "1594");
		TokenID t2 = new TokenID(ExchangeType.NSE_CM, "4717");
		TokenID t3 = new TokenID(ExchangeType.NSE_CM, "1594");
		TokenID t4 = new TokenID(ExchangeType.NCX_FO, "GUAREX31MAR2022");

		assertNotEquals(t1, t2);
		assertEquals(t1, t3);

	}
}