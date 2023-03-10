package com.example.chirper.service;

import com.example.chirper.persistence.entity.Post;
import com.example.chirper.persistence.entity.repository.PostRepository;
import com.example.chirper.persistence.entity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PostService {

    private final PostRepository postRepository;

    private final UserRepository userRepository;

    @Autowired
    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;

        this.userRepository = userRepository;
    }

    public void save(Post post, Long id) {
        post.setCreatedAt(LocalDateTime.now());
        userRepository.findById(id).ifPresent(post::setUser);
        postRepository.save(post);
    }
}
