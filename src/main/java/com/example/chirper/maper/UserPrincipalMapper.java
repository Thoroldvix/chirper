package com.example.chirper.maper;

import com.example.chirper.dto.UserDto;
import com.example.chirper.security.UserPrincipal;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserPrincipalMapper {

    UserDto toUserDto(UserPrincipal principal);
}
