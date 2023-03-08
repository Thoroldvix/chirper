package com.example.hoaxify.service;

import com.example.hoaxify.dto.UserDto;
import com.example.hoaxify.persistence.entity.UserEntity;
import com.example.hoaxify.persistence.entity.enums.Role;
import com.example.hoaxify.persistence.entity.repository.UserRepository;
import com.example.hoaxify.security.UserPrincipal;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final ModelMapper modelMapper;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    public UserEntity save(UserEntity userEntity) {
        userEntity.setRole(Role.USER);
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        return userRepository.save(userEntity);
    }

    public Page<UserDto> getUsers(UserPrincipal loggedInUser, Pageable page) {
        if (loggedInUser != null) {
            return userRepository.findByUsernameNot(loggedInUser.getUsername(), page)
                    .map(user -> modelMapper.map(user, UserDto.class));
        }
        return userRepository.findAll(page).map(user-> modelMapper.map(user, UserDto.class));
    }


}
