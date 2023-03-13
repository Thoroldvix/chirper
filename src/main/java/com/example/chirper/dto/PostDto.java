package com.example.chirper.dto;

public record PostDto(Long id,
                      String content,
                      Long timestamp,
                      UserDto user,
                      FileAttachmentDto attachment) {

}
