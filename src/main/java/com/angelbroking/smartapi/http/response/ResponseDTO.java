package com.angelbroking.smartapi.http.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseDTO {
    @JsonProperty("status")
    private boolean status;
    @JsonProperty("message")
    private String message;
    @JsonProperty("errorcode")
    private String errorCode;
}
