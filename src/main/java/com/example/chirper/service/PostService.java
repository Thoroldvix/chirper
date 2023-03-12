package com.example.chirper.service;

import com.example.chirper.dto.PostDto;
import com.example.chirper.maper.PostMapper;
import com.example.chirper.persistence.entity.Post;
import com.example.chirper.persistence.entity.UserEntity;
import com.example.chirper.persistence.entity.repository.PostRepository;
import com.example.chirper.persistence.entity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
        UserEntity user = userService.getByUsername(username);


        return postRepository.findAllByUser(user, pageable)
                .map(postMapper::toPostDto);
    }

    public Page<PostDto> getOldPosts(Long id, String username, Pageable pageable) {
        Specification<Post> spec = Specification.where(idLessThan(id));
        if (username != null) {
            UserEntity user = userService.getByUsername(username);
            spec = spec.and(userIs(user));
            return postRepository.findAll(spec, pageable).map(postMapper::toPostDto);
        }
        return postRepository.findAll(spec, pageable).map(postMapper::toPostDto);
    }


    public List<PostDto> getNewPosts(Long id, String username, Pageable pageable) {
        Specification<Post> spec = Specification.where(idGreaterThan(id));
        if (username != null) {
            UserEntity user = userService.getByUsername(username);
            spec = spec.and(userIs(user));
            return postRepository.findAll(spec, pageable.getSort()).stream()
                    .map(postMapper::toPostDto)
                    .collect(Collectors.toList());
        }
        return postRepository.findAll(spec, pageable.getSort()).stream()
                .map(postMapper::toPostDto)
                .collect(Collectors.toList());
    }


    public Long getNewPostCount(Long id, String username) {
        Specification<Post> spec = Specification.where(idGreaterThan(id));
        if (username != null) {
            UserEntity user = userService.getByUsername(username);
            spec = spec.and(userIs(user));
            return postRepository.count(spec);
        }
        return postRepository.count(spec);
    }


    private Specification<Post> userIs(UserEntity user) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("user"), user);
    }

    private Specification<Post> idLessThan(Long id) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.lessThan(root.get("id"), id);
    }

    private Specification<Post> idGreaterThan(Long id) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(root.get("id"), id);
    }
}
