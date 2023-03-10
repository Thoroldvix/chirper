package com.example.chirper.security;

import com.example.chirper.persistence.entity.UserEntity;
import com.example.chirper.persistence.entity.enums.Role;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;

public class UserPrincipal extends User {
    private final UserEntity user;

    public UserPrincipal(UserEntity user) {
        super(user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole())));
        this.user = user;
    }

    public Long getId() {
        return this.user.getId();
    }
    public String getImage() {
        return this.user.getImage();
    }
    public String getDisplayName() {
        return this.user.getDisplayName();
    }
    public Role getRole() {
        return this.user.getRole();
    }

}
