package com.angelbroking.smartapi.models;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WsMWJSONRequestDTO {
    @SerializedName("actiontype")
    private String actionType;
    @SerializedName("feedtype")
    private String feedType;
    @SerializedName("jwttoken")
    private String jwtToken;
    @SerializedName("clientcode")
    private String clientId;
    @SerializedName("apikey")
    private String apiKey;

}
