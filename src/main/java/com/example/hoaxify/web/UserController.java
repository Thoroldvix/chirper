package com.example.hoaxify.web;

import com.example.hoaxify.dto.GenericResponse;
import com.example.hoaxify.persistence.entity.UserEntity;
import com.example.hoaxify.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/api/1.0/users")
    GenericResponse createUser(@Valid @RequestBody  UserEntity userEntity) {
        userService.save(userEntity);
        return new GenericResponse("User saved");

    }

}


