package com.example.chirper.service;

import com.example.chirper.dto.PostDto;
import com.example.chirper.maper.PostMapper;
import com.example.chirper.persistence.entity.Post;
import com.example.chirper.persistence.entity.UserEntity;
import com.example.chirper.persistence.entity.repository.FileAttachmentRepository;
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

import static com.example.chirper.persistence.entity.PostSpecifications.*;

@Service
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;

    private final UserRepository userRepository;

    private final PostMapper postMapper;

    private final UserService userService;

    private final FileAttachmentRepository fileAttachmentRepository;

    @Autowired
    public PostService(PostRepository postRepository, UserRepository userRepository, PostMapper postMapper,
                       UserService userService,
                       FileAttachmentRepository fileAttachmentRepository) {
        this.postRepository = postRepository;

        this.userRepository = userRepository;
        this.postMapper = postMapper;

        this.userService = userService;
        this.fileAttachmentRepository = fileAttachmentRepository;
    }

    @Transactional
    public PostDto save(Post post, Long id) {
        post.setCreatedAt(LocalDateTime.now());
        userRepository.findById(id).ifPresent(post::setUser);
        if (post.getAttachment() != null) {
            fileAttachmentRepository.findById(post.getAttachment().getId())
                    .ifPresent(attachment -> {
                        attachment.setPost(post);
                        post.setAttachment(attachment);
                    });
        }
        return postMapper.toPostDto(postRepository.save(post));
    }

    public Page<PostDto> getAllPosts(Pageable pageable) {
        return postRepository.findAll(pageable).map(postMapper::toPostDto);
    }

    public Page<PostDto> getPostsOfUser(String username, Pageable pageable) {
        UserEntity userFromDB = userService.getByUsername(username);


        return postRepository.findAllByUser(userFromDB, pageable)
                .map(postMapper::toPostDto);
    }

    public Page<PostDto> getOldPosts(Long id, String username, Pageable pageable) {
        Specification<Post> spec = Specification.where(idLessThan(id));
        if (username != null) {
            UserEntity userFromDB = userService.getByUsername(username);
            spec = spec.and(isSameUser(userFromDB));
            return postRepository.findAll(spec, pageable).map(postMapper::toPostDto);
        }
        return postRepository.findAll(spec, pageable).map(postMapper::toPostDto);
    }


    public List<PostDto> getNewPosts(Long id, String username, Pageable pageable) {
        Specification<Post> spec = Specification.where(idGreaterThan(id));
        if (username != null) {
            UserEntity userFromDB = userService.getByUsername(username);
            spec = spec.and(isSameUser(userFromDB));
            return postMapper.toPostDtoList(postRepository.findAll(spec, pageable.getSort()));
        }
        return postMapper.toPostDtoList(postRepository.findAll(spec, pageable.getSort()));
    }


    public Long getNewPostCount(Long id, String username) {
        Specification<Post> spec = Specification.where(idGreaterThan(id));
        if (username != null) {
            UserEntity userFromDB = userService.getByUsername(username);
            spec = spec.and(isSameUser(userFromDB));
            return postRepository.count(spec);
        }
        return postRepository.count(spec);
    }


}
