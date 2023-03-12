package com.example.chirper.service;

import com.example.chirper.dto.UserDto;
import com.example.chirper.dto.UserUpdateDto;
import com.example.chirper.maper.UserMapper;
import com.example.chirper.persistence.entity.UserEntity;
import com.example.chirper.persistence.entity.Role;
import com.example.chirper.persistence.entity.repository.UserRepository;
import com.example.chirper.security.UserPrincipal;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserMapper userMapper;

    private final FileService fileService;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper, FileService fileService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.fileService = fileService;
    }

    @Transactional
    public UserEntity save(UserEntity userEntity) {
        userEntity.setRole(Role.USER);
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        return userRepository.save(userEntity);
    }

    public Page<UserDto> getUsers(UserPrincipal loggedInUser, Pageable page) {
        if (loggedInUser != null) {
            return userRepository.findByUsernameNot(loggedInUser.getUsername(), page)
                    .map(userMapper::toUserDto);
        }
        return userRepository.findAll(page).map(userMapper::toUserDto);
    }


    public UserDto getByUsername(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with username " + username + " not found"));

        return userMapper.toUserDto(user);
    }



    @Transactional
    @SneakyThrows
    public UserDto update(Long id, UserUpdateDto userUpdate) {
        UserEntity inDB = userRepository.getReferenceById(id);
        inDB.setDisplayName(userUpdate.displayName());
        if (userUpdate.image() != null) {
            String savedImageName = fileService.saveProfileImage(userUpdate.image());
            fileService.deleteProfileImage(inDB.getImage());
            inDB.setImage(savedImageName);
        }

        UserEntity user = userRepository.save(inDB);
        return userMapper.toUserDto(user);
    }


}
