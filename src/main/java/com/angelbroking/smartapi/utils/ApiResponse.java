package com.angelbroking.smartapi.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ApiResponse<T> {
    @JsonProperty("status")
    private boolean status;

    @JsonProperty("message")
    private String message;

    @JsonProperty("errorcode")
    private String errorCode;

    @JsonProperty("data")
    private T data;

    @Override
    public String toString() {
        return "ApiResponse{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", errorCode='" + errorCode + '\'' +
                ", data=" + data +
                '}';
    }
}

