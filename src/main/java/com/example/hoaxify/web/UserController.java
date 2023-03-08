package com.example.hoaxify.web;

import com.example.hoaxify.dto.GenericResponse;
import com.example.hoaxify.dto.UserDto;
import com.example.hoaxify.persistence.entity.UserEntity;
import com.example.hoaxify.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/1.0")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public Page<UserDto> getUsers(Pageable page) {
        return (userService.getUsers(page));
    }


    @PostMapping("/users")
    public GenericResponse createUser(@Valid @RequestBody UserEntity userEntity) {
        userService.save(userEntity);
        return new GenericResponse("User saved");

    }

}


