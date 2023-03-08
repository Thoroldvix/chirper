package com.example.hoaxify.persistence.entity.repository;

import com.example.hoaxify.persistence.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);

    Page<UserEntity> findByUsernameNot(String username, Pageable page);
}
