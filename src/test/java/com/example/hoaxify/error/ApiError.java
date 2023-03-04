package com.example.hoaxify.error;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Data
public class ApiError {

    private long timestamp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);

    private int status;

    private String message;

    private String url;
}
