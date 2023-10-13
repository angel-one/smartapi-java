package com.angelbroking.smartapi.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SearchScripResponseDTO {

    @JsonProperty("tradingsymbol")
    private String tradingSymbol;

    @JsonProperty("exchange")
    private String exchange;

    @JsonProperty("symboltoken")
    private String symbolToken;

}
