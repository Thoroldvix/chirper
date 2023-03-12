package com.example.chirper.web;

import com.example.chirper.dto.PostDto;
import com.example.chirper.persistence.entity.Post;
import com.example.chirper.security.UserPrincipal;
import com.example.chirper.service.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/1.0")
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("/posts")
    public PostDto createPost(@Valid @RequestBody Post post, @AuthenticationPrincipal UserPrincipal loggedInUser) {
        return postService.save(post, loggedInUser.getId());
    }

    @GetMapping("/posts")
    public Page<PostDto> getAllPosts(Pageable pageable) {
        return postService.getAllPosts(pageable);
    }

    @GetMapping("/users/{username}/posts")
    public Page<PostDto> getPostsOfUser(@PathVariable String username, Pageable pageable) {
        return postService.getPostsOfUser(username, pageable);
    }

    @GetMapping("/posts/{id:\\d+}")
    public Page<PostDto> getPostsRelative(@PathVariable Long id, Pageable pageable) {
        return postService.getOldPosts(id, pageable);
    }

    @GetMapping("/users/{username}/posts/{id:\\d+}")
    public Page<PostDto> getPostsRelativeForUser(@PathVariable String username,
                                                 @PathVariable Long id,
                                                 Pageable pageable) {
      return  postService.getOldPostsOfUser(username, id, pageable);
    }
}

