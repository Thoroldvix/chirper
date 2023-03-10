package com.example.chirper.persistence.entity.repository;

import com.example.chirper.persistence.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
