package com.example.chirper.dto;

import com.example.chirper.persistence.entity.Post;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

import java.time.ZoneId;

@Data
@NoArgsConstructor
public class PostDto {
    private Long id;
    private String content;

    private Long timestamp;
    private UserDto user;




}
