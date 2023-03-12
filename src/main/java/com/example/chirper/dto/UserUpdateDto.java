package com.example.chirper.dto;

import com.example.chirper.validation.ProfileImage;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public record UserUpdateDto(
        @NotNull
        @Size(min = 4, max = 255)
        String displayName,
        @ProfileImage
        String image
) {


}
