package com.example.chirper.controller;

import com.example.chirper.TestUtils;
import com.example.chirper.error.ApiError;
import com.example.chirper.persistence.entity.Post;
import com.example.chirper.persistence.entity.repository.PostRepository;
import com.example.chirper.persistence.entity.repository.UserRepository;
import com.example.chirper.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class PostControllerTest {

    private  final String API_1_0_POSTS = "/api/1.0/posts";
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    public void cleanup() {
        postRepository.deleteAll();
        userRepository.deleteAll();
        testRestTemplate.getRestTemplate().getInterceptors().clear();
    }

    @Test
    public void postPost_whenPostIsValidAndUserIsAuthorized_receiveOk() {
        userService.save(TestUtils.createValidUser("user1"));
        authenticate("user1");

        Post post = TestUtils.createValidPost();
        ResponseEntity<Object> response = postPost(post, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
    @Test
    public void postPost_whenPostIsValidAndUserIsUnauthorized_receiveUnauthorized() {


        Post post = TestUtils.createValidPost();
        ResponseEntity<Object> response = postPost(post, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
    @Test
    public void postPost_whenPostIsValidAndUserIsUnauthorized_receiveApiError() {


        Post post = TestUtils.createValidPost();
        ResponseEntity<ApiError> response = postPost(post, ApiError.class);
        assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }
    @Test
    public void postPost_whenPostIsValidAndUserIsAuthorized_PostSavedToDatabase() {
        userService.save(TestUtils.createValidUser("user1"));
        authenticate("user1");

        Post post = TestUtils.createValidPost();
        postPost(post, Object.class);
        assertThat(postRepository.count()).isEqualTo(1);
    }
    @Test
    public void postPost_whenPostIsValidAndUserIsAuthorized_PostSavedToDatabaseWithTimestamp() {
        userService.save(TestUtils.createValidUser("user1"));
        authenticate("user1");

        Post post = TestUtils.createValidPost();
        postPost(post, Object.class);

        Post inDB = postRepository.findAll().get(0);
        assertThat(inDB.getCreatedAt()).isNotNull();
    }
    @Test
    public void postPost_whenPostContentIsNullAndUserIsAuthorized_receiveBadRequest() {
        userService.save(TestUtils.createValidUser("user1"));
        authenticate("user1");

        Post post = new Post();
        ResponseEntity<Object> response = postPost(post, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
    @Test
    public void postPost_whenPostContentLessThan10CharsAndUserIsAuthorized_receiveBadRequest() {
        userService.save(TestUtils.createValidUser("user1"));
        authenticate("user1");

        Post post = new Post();
        post.setContent("123456789");
        ResponseEntity<Object> response = postPost(post, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
    @Test
    public void postPost_whenPostContentIs5000CharsAndUserIsAuthorized_receiveOk() {
        userService.save(TestUtils.createValidUser("user1"));
        authenticate("user1");

        Post post = new Post();
        String longString = IntStream.rangeClosed(1, 5000).mapToObj(i -> "a").collect(Collectors.joining());
        post.setContent(longString);
        ResponseEntity<Object> response = postPost(post, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
    @Test
    public void postPost_whenPostContentMoreThan5000CharsAndUserIsAuthorized_receiveBadRequest() {
        userService.save(TestUtils.createValidUser("user1"));
        authenticate("user1");

        Post post = new Post();
        String longString = IntStream.rangeClosed(1, 5001).mapToObj(i -> "a").collect(Collectors.joining());
        post.setContent(longString);
        ResponseEntity<Object> response = postPost(post, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
    @Test
    public void postPost_whenPostContentIsNullAndUserIsAuthorized_receiveApiErrorWithValidationErrors() {
        userService.save(TestUtils.createValidUser("user1"));
        authenticate("user1");

        Post post = new Post();
        ResponseEntity<ApiError> response = postPost(post, ApiError.class);
        Map<String, String> validationErrors = response.getBody().getValidationErrors();
        assertThat(validationErrors.get("content")).isNotNull();
    }

    private<T> ResponseEntity<T> postPost(Post post, Class<T> responseType) {
        return testRestTemplate.postForEntity(API_1_0_POSTS, post, responseType);
    }

    public void authenticate(String username) {
        testRestTemplate.getRestTemplate().getInterceptors().add(new BasicAuthenticationInterceptor(username, "P4ssword"));
    }

}
