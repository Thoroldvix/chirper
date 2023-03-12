package com.example.chirper.maper;

import com.example.chirper.dto.UserDto;
import com.example.chirper.security.UserPrincipal;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserPrincipalMapper {

    UserDto toUserDto(UserPrincipal principal);
}
