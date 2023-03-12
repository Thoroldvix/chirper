package com.example.chirper.persistence.entity.repository;

import com.example.chirper.persistence.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, Long> {


    Page<Post> findAllByUserId(Long id, Pageable pageable);

    Page<Post> findByIdLessThan(Long id, Pageable pageable);
}
