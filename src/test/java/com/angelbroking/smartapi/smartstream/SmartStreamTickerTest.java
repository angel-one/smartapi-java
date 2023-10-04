package com.angelbroking.smartapi.smartstream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.angelbroking.smartapi.SmartConnect;
import com.angelbroking.smartapi.models.User;
import com.angelbroking.smartapi.smartstream.models.ExchangeType;
import com.angelbroking.smartapi.smartstream.models.SmartStreamSubsMode;
import com.angelbroking.smartapi.smartstream.models.TokenID;
import com.angelbroking.smartapi.smartstream.ticker.SmartStreamTicker;
import com.neovisionaries.ws.client.WebSocketException;

public class SmartStreamTickerTest {

	private static String clientID;
	private static String clientPass;
	private static String apiKey;
	private static String feedToken;
	private static String totp;

	@BeforeAll
	public static void initClass() throws InterruptedException {
		//    clientID = System.getProperty("clientID");
//    clientPass = System.getProperty("clientPass");
//    apiKey = System.getProperty("apiKey");
//
//    Scanner sc = new Scanner(System.in);
//    System.out.print("enter totp: ");
//    totp = sc.nextLine();

//       SmartConnect smartConnect = new SmartConnect("VG2s34Cq"); // PROVIDE YOUR API KEY HERE
//       String clientId = "A52163134";
//       GoogleAuthenticator gAuth = new GoogleAuthenticator();
//       String totp_key = "DOBXCSIGFBJAKJH4BUDCTCLKEI";
//       String tOTP = String.valueOf(gAuth.getTotpPassword(totp_key));
//       User user = smartConnect.generateSession("A52163134", "4321", tOTP);
		clientID = "A52163134";
		clientPass = "4321";
		apiKey = "VG2s34Cq";

		GoogleAuthenticator gAuth = new GoogleAuthenticator();
		String totp_key = "DOBXCSIGFBJAKJH4BUDCTCLKEI";
		totp = String.valueOf(gAuth.getTotpPassword(totp_key));

		SmartConnect smartConnect = new SmartConnect(apiKey);
		User user = smartConnect.generateSession(clientID, clientPass, totp);
		feedToken = user.getFeedToken();
//    feedToken = "123";
	}

	@Test
	void testSmartStreamTicketLTP() throws WebSocketException, InterruptedException {
		try {
			SmartStreamTicker ticker = new SmartStreamTicker(clientID, feedToken, new SmartStreamListenerImpl());
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
		tokenSet.add(new TokenID(ExchangeType.BSE_FO, "1165360")); // NIFTY
//		tokenSet.add(new TokenID(ExchangeType.BSE_FO, "1165360")); // NIFTY BANK
//		tokenSet.add(new TokenID(ExchangeType.BSE_CM, "19000")); // Sensex
//
//		tokenSet.add(new TokenID(ExchangeType.NSE_CM, "99926000")); // NIFTY
//		tokenSet.add(new TokenID(ExchangeType.NSE_CM, "99926009")); // NIFTY BANK
//		tokenSet.add(new TokenID(ExchangeType.BSE_CM, "99919000")); // Sensex
//
//		tokenSet.add(new TokenID(ExchangeType.NSE_CM, "1594")); // NSE Infosys
//		tokenSet.add(new TokenID(ExchangeType.NSE_FO, "35003")); // Nifty June 2023 FUT
//		tokenSet.add(new TokenID(ExchangeType.CDE_FO, "1185")); // 1185 USDINR
//		tokenSet.add(new TokenID(ExchangeType.BSE_CM, "532540")); // BSE TCS
//		tokenSet.add(new TokenID(ExchangeType.NCX_FO, "GUARGUM5")); // GUAREX (NCDEX)
//		tokenSet.add(new TokenID(ExchangeType.MCX_FO, "252453")); //CRUDEOIL
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
