package com.angelbroking.smartapi.sample;

import com.angelbroking.smartapi.http.exceptions.SmartAPIException;
import com.angelbroking.smartapi.routes.Routes;
import com.angelbroking.smartapi.utils.Constants;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class LoginWithTOTPSample {

	public static void main(String[] args) throws SmartAPIException, IOException {
		Routes routes = new Routes();
		StringBuilder sb = new StringBuilder();
		String jwtToken = null;
		String clientId = null;
		String apiKey = null;
		sb.append(routes.getSWsuri()).append("?jwttoken=").append(jwtToken).append("&&clientcode=").append(clientId).append("&&apikey=").append(apiKey);

		StringBuilder sb2 = new StringBuilder();
		sb2.append(routes.getSWsuri())
				.append(Constants.QUESTION_MARK)
				.append(Constants.JWT_TOKEN_PARAM)
				.append(Constants.EQUALS)
				.append(jwtToken)
				.append(Constants.AMPERSAND)
				.append(Constants.AMPERSAND)
				.append(Constants.CLIENT_CODE_PARAM)
				.append(Constants.EQUALS)
				.append(clientId)
				.append(Constants.AMPERSAND)
				.append(Constants.AMPERSAND)
				.append(Constants.API_KEY_PARAM)
				.append(Constants.EQUALS)
				.append(apiKey);
		log.info("SB :{}",sb.toString());
		log.info("SB2:{}",sb2.toString());

	}
}
