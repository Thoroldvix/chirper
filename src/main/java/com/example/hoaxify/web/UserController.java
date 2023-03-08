package com.example.hoaxify.web;

import com.example.hoaxify.dto.GenericResponse;
import com.example.hoaxify.dto.UserDto;
import com.example.hoaxify.dto.UserUpdateDto;
import com.example.hoaxify.persistence.entity.UserEntity;
import com.example.hoaxify.security.UserPrincipal;
import com.example.hoaxify.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public Page<UserDto> getUsers(@AuthenticationPrincipal UserPrincipal loggedInUser, Pageable page) {
        return (userService.getUsers(loggedInUser, page));
    }

    @GetMapping("/users/{username}")
    public UserDto getUserByName(@PathVariable String username) {
        return userService.getByUsername(username);
    }

    @PutMapping("/users/{id:[0-9]+}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or #id == #loggedInUser.id")
    public UserDto updateUser(@PathVariable Long id,
                           @AuthenticationPrincipal UserPrincipal loggedInUser,
                           @RequestBody(required = false) UserUpdateDto userUpdate) {
       return userService.update(id, userUpdate);
    }

    @PostMapping("/users")
    public GenericResponse createUser(@Valid @RequestBody UserEntity userEntity) {
        userService.save(userEntity);
        return new GenericResponse("User saved");

    }

}


