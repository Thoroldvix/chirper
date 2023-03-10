package com.example.chirper.persistence.entity.repository;

import com.example.chirper.persistence.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    @EntityGraph(attributePaths = {"posts"}, type = EntityGraph.EntityGraphType.LOAD)
    Optional<UserEntity> findByUsername(String username);

    Page<UserEntity> findByUsernameNot(String username, Pageable page);


}
