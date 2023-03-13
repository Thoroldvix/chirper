package com.example.chirper.service;

import com.example.chirper.persistence.entity.Post;
import com.example.chirper.persistence.entity.repository.PostRepository;
import com.example.chirper.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class PostSecurityService {

    private final PostRepository postRepository;

    @Autowired
    public PostSecurityService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public boolean isAllowedToDelete(Long id, UserPrincipal loggedInUser) {
        Optional<Post> post = postRepository.findById(id);
        return post.isPresent() && Objects.equals(post.get().getUser().getId(), loggedInUser.getId());
    }

}
