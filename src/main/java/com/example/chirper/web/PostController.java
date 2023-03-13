package com.example.chirper.web;

import com.example.chirper.dto.GenericResponse;
import com.example.chirper.dto.PostDto;
import com.example.chirper.persistence.entity.Post;
import com.example.chirper.security.UserPrincipal;
import com.example.chirper.service.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

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

    @GetMapping({"/posts/{id:\\d+}", "/users/{username}/posts/{id:\\d+}"})
    public ResponseEntity<?> getPostsRelative(@PathVariable Long id,
                                              @PathVariable(required = false) String username,
                                              @RequestParam(name = "direction", defaultValue = "after") String direction,
                                              @RequestParam(name = "count", defaultValue = "false", required = false) boolean count,
                                              Pageable pageable) {
        if (count) {
            Long newPostCount = postService.getNewPostCount(id, username);
            return ResponseEntity.ok(Collections.singletonMap("count", newPostCount));
        }
        return !"after".equalsIgnoreCase(direction) ?
                ResponseEntity.ok(postService.getOldPosts(id, username, pageable))
                : ResponseEntity.ok(postService.getNewPosts(id, username, pageable));

    }
    @DeleteMapping("/posts/{id:\\d+}")
    @PreAuthorize("@postSecurityService.isAllowedToDelete(#id, principal)")
    public GenericResponse deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return new GenericResponse("Post deleted");
    }

}

