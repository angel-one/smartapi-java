package com.angelbroking.smartapi.smartstream.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import static com.angelbroking.smartapi.utils.Utils.validateInputNullCheck;

@Data
@NoArgsConstructor
public class TokenID {

	private ExchangeType exchangeType;
	private String token;

	public TokenID(ExchangeType exchangeType, String token) throws IllegalArgumentException {
		if(validateInputNullCheck(exchangeType)|| validateInputNullCheck(token) || token.isEmpty()) {
			throw new IllegalArgumentException("Invalid exchangeType or token.");
		}
		this.exchangeType = exchangeType;
		this.token = token;
	}



	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof TokenID)) {
			return false;
		}
		
		TokenID newObj = (TokenID) obj;
		return this.exchangeType.equals(newObj.getExchangeType()) && this.token.equals(newObj.getToken());
	}
	
	@Override
	public int hashCode() {
		return toString().hashCode();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(exchangeType.name()).append("-").append(token);
		return sb.toString();

	}
}
