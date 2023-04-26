package com.angelbroking.smartapi.http.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserResponseDTO {
    private boolean status;
    private String message;
    private String errorcode;
    private ObjectData data;
}
