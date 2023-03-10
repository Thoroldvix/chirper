package com.example.chirper.service;

import com.example.chirper.dto.UserDto;
import com.example.chirper.dto.UserUpdateDto;
import com.example.chirper.persistence.entity.UserEntity;
import com.example.chirper.persistence.entity.enums.Role;
import com.example.chirper.persistence.entity.repository.UserRepository;
import com.example.chirper.security.UserPrincipal;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
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

    private final ModelMapper modelMapper;

    private final FileService fileService;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapper, FileService fileService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
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
                    .map(user -> modelMapper.map(user, UserDto.class));
        }
        return userRepository.findAll(page).map(this::toUserDto);
    }


    public UserDto getByUsername(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with username " + username + " not found"));

        return toUserDto(user);
    }

    private UserDto toUserDto(UserEntity user) {
        return modelMapper.map(user, UserDto.class);
    }

    @Transactional
    @SneakyThrows
    public UserDto update(Long id, UserUpdateDto userUpdate) {
        UserEntity inDB = userRepository.getReferenceById(id);
        inDB.setDisplayName(userUpdate.getDisplayName());
        if (userUpdate.getImage() != null) {
            String savedImageName = fileService.saveProfileImage(userUpdate.getImage());
            fileService.deleteProfileImage(inDB.getImage());
            inDB.setImage(savedImageName);
        }

        UserEntity user = userRepository.save(inDB);
        return modelMapper.map(user, UserDto.class);
    }


}
