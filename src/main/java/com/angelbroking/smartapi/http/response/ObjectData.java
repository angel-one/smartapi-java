package com.angelbroking.smartapi.http.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ObjectData {
    private String jwtToken;
    private String refreshToken;
    private String feedToken;
}
