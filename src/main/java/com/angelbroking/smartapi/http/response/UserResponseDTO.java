package com.angelbroking.smartapi.http.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserResponseDTO {
    private boolean status;
    private String message;
    @JsonProperty("errorcode")
    private String errorCode;
    private ObjectData data;
}
