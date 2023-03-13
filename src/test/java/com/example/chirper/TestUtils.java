package com.example.chirper;

import com.example.chirper.persistence.entity.Post;
import com.example.chirper.persistence.entity.Role;
import com.example.chirper.persistence.entity.UserEntity;
import lombok.experimental.UtilityClass;


@UtilityClass
public class TestUtils {
   public static UserEntity createValidUser() {
       return UserEntity.builder()
                .username("test-user")
                .displayName("test-display")
                .password("P4ssword")
                .image("profile-image.png")
                .role(Role.USER)
                .build();

    }
    public static UserEntity createValidUser(String username) {
        UserEntity userEntity = createValidUser();
        userEntity.setUsername(username);

        return userEntity;
    }
    public static Post createValidPost() {
       return Post.builder()
                .content("test content for the test post")
                .build();
    }

}
