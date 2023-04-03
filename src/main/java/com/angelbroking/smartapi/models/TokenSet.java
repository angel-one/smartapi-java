package com.angelbroking.smartapi.models;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * A wrapper for user id, access token, refresh token.
 */

@Data
public class TokenSet {

	@SerializedName("clientcode")
	public String userId;
	@SerializedName("access_token")
	public String accessToken;
	@SerializedName("refresh_token")
	public String refreshToken;

}
