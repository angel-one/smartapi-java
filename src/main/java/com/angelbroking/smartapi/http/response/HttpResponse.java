package com.angelbroking.smartapi.http.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HttpResponse {
    private int statusCode;
    private Map<String, List<String>> headers;
    private String body;

}
