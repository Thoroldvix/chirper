package com.example.chirper.web;

import com.example.chirper.dto.UserDto;
import com.example.chirper.maper.UserPrincipalMapper;
import com.example.chirper.security.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class LoginController {

    private final UserPrincipalMapper userPrincipalMapper;

    @Autowired
    public LoginController(UserPrincipalMapper userPrincipalMapper) {
        this.userPrincipalMapper = userPrincipalMapper;
    }


    @PostMapping("/api/1.0/login")
    public UserDto handleLogin(@AuthenticationPrincipal UserPrincipal loggedInUser) {
        log.info("User " + loggedInUser.getUsername() + " logged in");
        return userPrincipalMapper.toUserDto(loggedInUser);
    }
}
