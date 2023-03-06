package com.example.hoaxify.web;

import com.example.hoaxify.security.UserPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @PostMapping("/api/1.0/login")
    public UserPrincipal handleLogin(@AuthenticationPrincipal UserPrincipal loggedInUser) {
       return loggedInUser;
    }
}
