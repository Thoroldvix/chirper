package com.example.chirper.persistence.entity.repository;

import com.example.chirper.persistence.entity.Post;
import com.example.chirper.persistence.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {


    Page<Post> findAllByUserId(Long userId, Pageable pageable);

    Page<Post> findByIdLessThan(Long id, Pageable pageable);

    Page<Post> findByIdLessThanAndUserId(Long id, Long userId, Pageable pageable);

    List<Post> findByIdGreaterThan(Long id, Sort sort);
    List<Post> findByIdGreaterThanAndUserId(Long id, Long userId, Sort sort);
}
