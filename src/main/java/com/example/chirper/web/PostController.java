package com.example.chirper.web;

import com.example.chirper.persistence.entity.Post;
import com.example.chirper.security.UserPrincipal;
import com.example.chirper.service.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/1.0")
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("/posts")
    public void createHoax(@Valid @RequestBody Post post, @AuthenticationPrincipal UserPrincipal loggedInUser) {
        postService.save(post, loggedInUser.getId());
    }
}
