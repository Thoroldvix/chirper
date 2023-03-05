package com.example.hoaxify.error;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;

@Data
@NoArgsConstructor
public class ApiError {

    private long timestamp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);

    private int status;

    private String message;

    private String url;

    private Map<String, String> validationErrors;
    public ApiError(int status, String message, String url) {
        this.status = status;
        this.message = message;
        this.url = url;
    }
}
