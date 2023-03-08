package com.example.hoaxify.security;

import com.example.hoaxify.persistence.entity.UserEntity;
import com.example.hoaxify.persistence.entity.enums.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class UserPrincipal implements UserDetails {
    private final UserEntity user;

    public UserPrincipal(UserEntity user) {
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



    @Override

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + this.user.getRole()));
    }

    @Override

    public String getPassword() {
        return this.user.getPassword();
    }

    @Override
    public String getUsername() {
        return this.user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
