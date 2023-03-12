package com.example.chirper.maper;

import com.example.chirper.dto.UserDto;
import com.example.chirper.persistence.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toUserDto(UserEntity user);
}
