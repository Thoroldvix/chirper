package com.example.hoaxify;

import com.example.hoaxify.persistence.entity.UserEntity;
import com.example.hoaxify.persistence.entity.enums.Role;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TestUtils {
   public static UserEntity createValidUser() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("test-user");
        userEntity.setDisplayName("test-display");
        userEntity.setPassword("P4ssword");
        userEntity.setImage("profile-image.png");
        userEntity.setRole(Role.USER);
        return userEntity;
    }
}
