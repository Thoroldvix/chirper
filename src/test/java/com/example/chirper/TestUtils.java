package com.example.chirper;

import com.example.chirper.persistence.entity.Post;
import com.example.chirper.persistence.entity.UserEntity;
import com.example.chirper.persistence.entity.enums.Role;
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
    public static UserEntity createValidUser(String username) {
        UserEntity userEntity = createValidUser();
        userEntity.setUsername(username);

        return userEntity;
    }
    public static Post createValidPost() {
        Post post = new Post();
        post.setContent("test content for the test post");
        return post;
    }

}
