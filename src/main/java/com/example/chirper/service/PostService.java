package com.example.chirper.service;

import com.example.chirper.dto.PostDto;
import com.example.chirper.persistence.entity.Post;
import com.example.chirper.persistence.entity.repository.PostRepository;
import com.example.chirper.persistence.entity.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    @Autowired
    public PostService(PostRepository postRepository, UserRepository userRepository, ModelMapper modelMapper) {
        this.postRepository = postRepository;

        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public PostDto save(Post post, Long id) {
        post.setCreatedAt(LocalDateTime.now());
        userRepository.findById(id).ifPresent(post::setUser);
        Post savedPost = postRepository.save(post);


        return modelMapper.map(savedPost, PostDto.class);
    }

    public Page<PostDto> getAllPosts(Pageable pageable) {
        return postRepository.findAll(pageable).map(post -> modelMapper.map(post, PostDto.class));
    }
}