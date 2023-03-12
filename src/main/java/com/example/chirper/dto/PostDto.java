package com.example.chirper.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostDto {
    private Long id;
    private String content;

    private Long timestamp;
    private UserDto user;




}
