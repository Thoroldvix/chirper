package com.example.chirper.maper;

import com.example.chirper.dto.UserDto;
import com.example.chirper.persistence.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toUserDto(UserEntity user);
}
