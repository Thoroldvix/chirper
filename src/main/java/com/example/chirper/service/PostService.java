package com.example.chirper.service;

import com.example.chirper.dto.PostDto;
import com.example.chirper.dto.UserDto;
import com.example.chirper.maper.PostMapper;
import com.example.chirper.persistence.entity.Post;
import com.example.chirper.persistence.entity.repository.PostRepository;
import com.example.chirper.persistence.entity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;

    private final UserRepository userRepository;

    private final PostMapper postMapper;

    private final UserService userService;

    @Autowired
    public PostService(PostRepository postRepository, UserRepository userRepository, PostMapper postMapper, UserService userService) {
        this.postRepository = postRepository;

        this.userRepository = userRepository;
        this.postMapper = postMapper;

        this.userService = userService;
    }

    @Transactional
    public PostDto save(Post post, Long id) {
        post.setCreatedAt(LocalDateTime.now());
        userRepository.findById(id).ifPresent(post::setUser);
        Post savedPost = postRepository.save(post);


        return postMapper.toPostDto(savedPost);
    }

    public Page<PostDto> getAllPosts(Pageable pageable) {
        return postRepository.findAll(pageable).map(postMapper::toPostDto);
    }

    public Page<PostDto> getPostsOfUser(String username, Pageable pageable) {
        UserDto user = userService.getByUsername(username);


        return postRepository.findAllByUserId(user.id(), pageable)
                .map(postMapper::toPostDto);
    }

    public Page<PostDto> getOldPosts(Long id, Pageable pageable) {
        return postRepository.findByIdLessThan(id, pageable).map(postMapper::toPostDto);
    }


    public Page<PostDto> getOldPostsOfUser(String username, Long id, Pageable pageable) {
        UserDto user = userService.getByUsername(username);
        return postRepository.findByIdLessThanAndUserId(id, user.id(), pageable).map(postMapper::toPostDto);
    }

    public List<PostDto> getNewPosts(Long id, Pageable pageable) {
        return postRepository.findByIdGreaterThan(id, pageable.getSort()).stream()
                .map(postMapper::toPostDto)
                .collect(Collectors.toList());
    }

    public List<PostDto> getNewPostsOfUser(String username, Long id, Pageable pageable) {
        UserDto user = userService.getByUsername(username);
        return postRepository.findByIdGreaterThanAndUserId(id, user.id(), pageable.getSort()).stream()
                .map(postMapper::toPostDto)
                .collect(Collectors.toList());
    }
}
