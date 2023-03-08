package com.example.hoaxify.web;

import com.example.hoaxify.dto.UserDto;
import com.example.hoaxify.security.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class LoginController {

    private final ModelMapper modelMapper;

    public LoginController(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @PostMapping("/api/1.0/login")
    public UserDto handleLogin(@AuthenticationPrincipal UserPrincipal loggedInUser) {
        log.info("User " + loggedInUser.getUsername() + " logged in");
        return modelMapper.map(loggedInUser, UserDto.class);
    }
}
