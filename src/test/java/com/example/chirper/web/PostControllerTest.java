package com.example.chirper.web;

import com.example.chirper.TestPage;
import com.example.chirper.config.AppConfiguration;
import com.example.chirper.dto.PostDto;
import com.example.chirper.error.ApiError;
import com.example.chirper.persistence.entity.FileAttachment;
import com.example.chirper.persistence.entity.Post;
import com.example.chirper.persistence.entity.UserEntity;
import com.example.chirper.persistence.entity.repository.FileAttachmentRepository;
import com.example.chirper.persistence.entity.repository.PostRepository;
import com.example.chirper.persistence.entity.repository.UserRepository;
import com.example.chirper.service.FileService;
import com.example.chirper.service.PostService;
import com.example.chirper.service.UserService;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.example.chirper.TestUtils.createValidPost;
import static com.example.chirper.TestUtils.createValidUser;
import static com.example.chirper.web.UserControllerTest.API_1_0_USERS;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class PostControllerTest {

    private final String API_1_0_POSTS = "/api/1.0/posts";
    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostService postService;
    @Autowired
    private FileAttachmentRepository fileAttachmentRepository;
    @Autowired
    private AppConfiguration appConfiguration;
    @Autowired
    private FileService fileService;

    @AfterEach
    public void cleanup() {
        fileAttachmentRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
        testRestTemplate.getRestTemplate().getInterceptors().clear();
    }

    @Test
    public void postPost_whenPostIsValidAndUserIsAuthorized_receiveOk() {
        userService.save(createValidUser("user1"));
        authenticate("user1");

        Post post = createValidPost();
        ResponseEntity<Object> response = postPost(post, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void postPost_whenPostIsValidAndUserIsUnauthorized_receiveUnauthorized() {


        Post post = createValidPost();
        ResponseEntity<Object> response = postPost(post, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void postPost_whenPostIsValidAndUserIsUnauthorized_receiveApiError() {


        Post post = createValidPost();
        ResponseEntity<ApiError> response = postPost(post, ApiError.class);
        assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    public void postPost_whenPostIsValidAndUserIsAuthorized_PostSavedToDatabase() {
        userService.save(createValidUser("user1"));
        authenticate("user1");

        Post post = createValidPost();
        postPost(post, Object.class);
        assertThat(postRepository.count()).isEqualTo(1);
    }

    @Test
    public void postPost_whenPostIsValidAndUserIsAuthorized_PostSavedToDatabaseWithTimestamp() {
        userService.save(createValidUser("user1"));
        authenticate("user1");

        Post post = createValidPost();
        postPost(post, Object.class);

        Post inDB = postRepository.findAll().get(0);
        assertThat(inDB.getCreatedAt()).isNotNull();
    }

    @Test
    public void postPost_whenPostContentIsNullAndUserIsAuthorized_receiveBadRequest() {
        userService.save(createValidUser("user1"));
        authenticate("user1");

        Post post = new Post();
        ResponseEntity<Object> response = postPost(post, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postPost_whenPostContentLessThan10CharsAndUserIsAuthorized_receiveBadRequest() {
        userService.save(createValidUser("user1"));
        authenticate("user1");

        Post post = new Post();
        post.setContent("123456789");
        ResponseEntity<Object> response = postPost(post, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postPost_whenPostContentIs5000CharsAndUserIsAuthorized_receiveOk() {
        userService.save(createValidUser("user1"));
        authenticate("user1");

        Post post = new Post();
        String longString = IntStream.rangeClosed(1, 5000).mapToObj(i -> "a").collect(Collectors.joining());
        post.setContent(longString);
        ResponseEntity<Object> response = postPost(post, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void postPost_whenPostContentMoreThan5000CharsAndUserIsAuthorized_receiveBadRequest() {
        userService.save(createValidUser("user1"));
        authenticate("user1");

        Post post = new Post();
        String longString = IntStream.rangeClosed(1, 5001).mapToObj(i -> "a").collect(Collectors.joining());
        post.setContent(longString);
        ResponseEntity<Object> response = postPost(post, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postPost_whenPostContentIsNullAndUserIsAuthorized_receiveApiErrorWithValidationErrors() {
        userService.save(createValidUser("user1"));
        authenticate("user1");

        Post post = new Post();
        ResponseEntity<ApiError> response = postPost(post, ApiError.class);
        Map<String, String> validationErrors = response.getBody().getValidationErrors();
        assertThat(validationErrors.get("content")).isNotNull();
    }

    @Test
    public void postPost_whenPostIsValidAndUserIsAuthorized_postSavedWithAuthenticatedUserInfo() {
        userService.save(createValidUser("user1"));
        authenticate("user1");
        Post post = createValidPost();
        postPost(post, Object.class);

        Post inDB = postRepository.findAll().get(0);
        assertThat(inDB.getUser().getUsername()).isEqualTo("user1");

    }

    @Test
    public void postPost_whenPostIsValidAndUserIsAuthorized_postCanBeAccessedFromUserEntity() {
        userService.save(createValidUser("user1"));
        authenticate("user1");

        Post post = createValidPost();
        postPost(post, Object.class);

        UserEntity inDBUser = userRepository.findByUsername("user1").orElse(null);
        assertThat(inDBUser).isNotNull();
        assertThat(inDBUser.getPosts().size()).isEqualTo(1);
    }

    @Test
    public void getPosts_whenThereAreNoPostsInDB_receiveOk() {
        ResponseEntity<Object> response = getPosts(new ParameterizedTypeReference<Object>() {
        });
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void getPosts_whenThereAreNoPosts_receivePageWithZeroItems() {
        ResponseEntity<TestPage<Object>> response = getPosts(new ParameterizedTypeReference<TestPage<Object>>() {
        });
        assertThat(response.getBody().getTotalElements()).isEqualTo(0);
    }

    @Test
    public void getPosts_whenThereArePosts_receivePageWithItems() {
        UserEntity user = userService.save(createValidUser("user1"));
        postService.save(createValidPost(), user.getId());
        postService.save(createValidPost(), user.getId());
        postService.save(createValidPost(), user.getId());
        ResponseEntity<TestPage<Object>> response = getPosts(new ParameterizedTypeReference<TestPage<Object>>() {
        });
        assertThat(response.getBody().getTotalElements()).isEqualTo(3);
    }

    @Test
    public void getPosts_whenThereArePosts_receivePageWithPostDto() {
        UserEntity user = userService.save(createValidUser("user1"));
        postService.save(createValidPost(), user.getId());

        ResponseEntity<TestPage<PostDto>> response = getPosts(new ParameterizedTypeReference<TestPage<PostDto>>() {
        });
        PostDto storedPost = response.getBody().getContent().get(0);
        assertThat(storedPost.user().username()).isEqualTo(user.getUsername());
    }

    @Test
    public void postPosts_whenPostIsValidAndUserIsAuthorized_receivePostDto() {
        UserEntity user = userService.save(createValidUser("user1"));
        authenticate(user.getUsername());

        ResponseEntity<PostDto> response = postPost(createValidPost(), PostDto.class);

        assertThat(response.getBody().user().username()).isEqualTo(user.getUsername());
    }

    @Test
    public void getPostsOfUser_whenUserExists_receiveOk() {
        UserEntity user = userService.save(createValidUser("user1"));
        ResponseEntity<Object> response = getPostsOfUser(user.getUsername(), new ParameterizedTypeReference<Object>() {
        });
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void getPostsOfUser_whenUserDoesNotExists_receiveNotFound() {
        ResponseEntity<Object> response = getPostsOfUser("unknown-user", new ParameterizedTypeReference<Object>() {
        });
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void getPostsOfUser_whenUserExists_receivePageWithZeroHoaxes() {
        ResponseEntity<TestPage<Object>> response = getPosts(new ParameterizedTypeReference<TestPage<Object>>() {
        });
        assertThat(response.getBody().getTotalElements()).isEqualTo(0);
    }

    @Test
    public void getPostsOfUser_whenUserExistsWithPosts_receivePageWithPostDto() {
        UserEntity user = userService.save(createValidUser("user1"));
        postService.save(createValidPost(), user.getId());

        ResponseEntity<TestPage<PostDto>> response = getPostsOfUser(user.getUsername(), new ParameterizedTypeReference<TestPage<PostDto>>() {
        });
        PostDto storedPost = response.getBody().getContent().get(0);
        assertThat(storedPost.user().username()).isEqualTo(user.getUsername());
    }

    @Test
    public void getPostsOfUser_whenUserExistsWithMultiplePosts_receivePageWithMatchingPostsCount() {
        UserEntity user = userService.save(createValidUser("user1"));
        postService.save(createValidPost(), user.getId());
        postService.save(createValidPost(), user.getId());
        postService.save(createValidPost(), user.getId());

        ResponseEntity<TestPage<PostDto>> response = getPostsOfUser(user.getUsername(), new ParameterizedTypeReference<TestPage<PostDto>>() {
        });
        assertThat(response.getBody().getTotalElements()).isEqualTo(3);
    }

    @Test
    public void getPostsOfUser_whenMultipleUserExistsWithMultiplePosts_receivePageWithMatchingPostsCount() {
        UserEntity userWithThreePosts = userService.save(createValidUser("user1"));
        IntStream.rangeClosed(1, 3).forEach(i -> postService.save(createValidPost(), userWithThreePosts.getId()));

        UserEntity userWithFivePosts = userService.save(createValidUser("user2"));
        IntStream.rangeClosed(1, 5).forEach(i -> postService.save(createValidPost(), userWithFivePosts.getId()));


        ResponseEntity<TestPage<PostDto>> response = getPostsOfUser(userWithFivePosts.getUsername(), new ParameterizedTypeReference<TestPage<PostDto>>() {
        });
        assertThat(response.getBody().getTotalElements()).isEqualTo(5);
    }

    @Test
    public void getOldPosts_whenThereAreNoPosts_receiveOk() {
        ResponseEntity<Object> response = getOldPosts(5L, new ParameterizedTypeReference<Object>() {
        });
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void getOldPosts_whenThereArePosts_receivePageWithItemsProvidedId() {
        UserEntity user = userService.save(createValidUser("user1"));
        postService.save(createValidPost(), user.getId());
        postService.save(createValidPost(), user.getId());
        postService.save(createValidPost(), user.getId());
        PostDto fourth = postService.save(createValidPost(), user.getId());
        postService.save(createValidPost(), user.getId());

        ResponseEntity<TestPage<Object>> response = getOldPosts(fourth.id(), new ParameterizedTypeReference<TestPage<Object>>() {
        });
        assertThat(response.getBody().getTotalElements()).isEqualTo(3);
    }

    @Test
    public void getOldPosts_whenThereArePosts_receivePageWithPostDtoBeforeProvidedId() {
        UserEntity user = userService.save(createValidUser("user1"));
        postService.save(createValidPost(), user.getId());
        postService.save(createValidPost(), user.getId());
        postService.save(createValidPost(), user.getId());
        PostDto fourth = postService.save(createValidPost(), user.getId());
        postService.save(createValidPost(), user.getId());

        ResponseEntity<TestPage<PostDto>> response = getOldPosts(fourth.id(), new ParameterizedTypeReference<TestPage<PostDto>>() {
        });
        assertThat(response.getBody().getContent().get(0).timestamp()).isNotNull();
    }

    @Test
    public void getOldPostsOfUser_whenUserExistThereAreNoPosts_receiveOk() {
        UserEntity user = userService.save(createValidUser("user1"));

        ResponseEntity<Object> response = getOldPostsOfUser(5L, user.getUsername(), new ParameterizedTypeReference<Object>() {
        });
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void getOldPostsOfUser_whenUserExistAndThereArePosts_receivePageWithItemsProvidedId() {
        UserEntity user = userService.save(createValidUser("user1"));
        postService.save(createValidPost(), user.getId());
        postService.save(createValidPost(), user.getId());
        postService.save(createValidPost(), user.getId());
        PostDto fourth = postService.save(createValidPost(), user.getId());
        postService.save(createValidPost(), user.getId());

        ResponseEntity<TestPage<PostDto>> response = getOldPostsOfUser(fourth.id(), user.getUsername(), new ParameterizedTypeReference<TestPage<PostDto>>() {
        });
        assertThat(response.getBody().getTotalElements()).isEqualTo(3);
    }

    @Test
    public void getOldPostsOfUser_whenUserDoesNotExistThereAreNoPosts_receiveNotFound() {
        ResponseEntity<Object> response = getOldPostsOfUser(5L, "user1", new ParameterizedTypeReference<Object>() {
        });
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void getOldPostsOfUser_whenUserExistAndThereAreNoPosts_receivePageWithZeroItemsBeforeProvidedId() {
        UserEntity user1 = userService.save(createValidUser("user1"));
        postService.save(createValidPost(), user1.getId());
        postService.save(createValidPost(), user1.getId());
        postService.save(createValidPost(), user1.getId());
        PostDto fourth = postService.save(createValidPost(), user1.getId());
        postService.save(createValidPost(), user1.getId());

        UserEntity user2 = userService.save(createValidUser("user2"));

        ResponseEntity<TestPage<PostDto>> response = getOldPostsOfUser(fourth.id(), user2.getUsername(), new ParameterizedTypeReference<TestPage<PostDto>>() {
        });
        assertThat(response.getBody().getTotalElements()).isEqualTo(0);
    }

    @Test
    public void getNewPosts_whenThereArePosts_receiveListOfItemsAfterProvidedId() {
        UserEntity user = userService.save(createValidUser("user1"));
        postService.save(createValidPost(), user.getId());
        postService.save(createValidPost(), user.getId());
        postService.save(createValidPost(), user.getId());
        PostDto fourth = postService.save(createValidPost(), user.getId());
        postService.save(createValidPost(), user.getId());

        ResponseEntity<List<Object>> response = getNewPosts(fourth.id(), new ParameterizedTypeReference<List<Object>>() {
        });
        assertThat(response.getBody().size()).isEqualTo(1);
    }

    @Test
    public void getNewPosts_whenThereArePosts_receiveListOfPostDtoAfterProvidedId() {
        UserEntity user = userService.save(createValidUser("user1"));
        postService.save(createValidPost(), user.getId());
        postService.save(createValidPost(), user.getId());
        postService.save(createValidPost(), user.getId());
        PostDto fourth = postService.save(createValidPost(), user.getId());
        postService.save(createValidPost(), user.getId());

        ResponseEntity<List<PostDto>> response = getNewPosts(fourth.id(), new ParameterizedTypeReference<List<PostDto>>() {
        });
        assertThat(response.getBody().get(0).timestamp()).isGreaterThan(0);
    }

    @Test
    public void getNewPostsOfUser_whenUserExistThereAreNoPosts_receiveOk() {
        UserEntity user = userService.save(createValidUser("user1"));

        ResponseEntity<Object> response = getNewPostsOfUser(5L, user.getUsername(), new ParameterizedTypeReference<Object>() {
        });
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void getNewPostsOfUser_whenUserExistAndThereArePosts_receiveListWithItemsAfterProvidedId() {
        UserEntity user = userService.save(createValidUser("user1"));
        postService.save(createValidPost(), user.getId());
        postService.save(createValidPost(), user.getId());
        postService.save(createValidPost(), user.getId());
        PostDto fourth = postService.save(createValidPost(), user.getId());
        postService.save(createValidPost(), user.getId());

        ResponseEntity<List<PostDto>> response = getNewPostsOfUser(fourth.id(), user.getUsername(), new ParameterizedTypeReference<List<PostDto>>() {
        });
        assertThat(response.getBody().size()).isEqualTo(1);
    }

    @Test
    public void getNewPostsOfUser_whenUserDoesNotExistThereAreNoPosts_receiveNotFound() {
        ResponseEntity<Object> response = getNewPostsOfUser(5L, "user1", new ParameterizedTypeReference<Object>() {
        });
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void getNewPostsOfUser_whenUserExistAndThereAreNoPosts_receiveListWithZeroItemsAfterProvidedId() {
        UserEntity user1 = userService.save(createValidUser("user1"));
        postService.save(createValidPost(), user1.getId());
        postService.save(createValidPost(), user1.getId());
        postService.save(createValidPost(), user1.getId());
        PostDto fourth = postService.save(createValidPost(), user1.getId());
        postService.save(createValidPost(), user1.getId());

        UserEntity user2 = userService.save(createValidUser("user2"));

        ResponseEntity<List<PostDto>> response = getNewPostsOfUser(fourth.id(), user2.getUsername(), new ParameterizedTypeReference<List<PostDto>>() {
        });
        assertThat(response.getBody().size()).isEqualTo(0);
    }

    @Test
    public void getNewPostCount_whenThereArePosts_receiveCountAfterProvidedId() {
        UserEntity user = userService.save(createValidUser("user1"));
        postService.save(createValidPost(), user.getId());
        postService.save(createValidPost(), user.getId());
        postService.save(createValidPost(), user.getId());
        PostDto fourth = postService.save(createValidPost(), user.getId());
        postService.save(createValidPost(), user.getId());

        ResponseEntity<Map<String, Long>> response = getNewPostCount(fourth.id(), new ParameterizedTypeReference<Map<String, Long>>() {
        });
        assertThat(response.getBody().get("count")).isEqualTo(1);
    }

    @Test
    public void getNewPostCountOfUser_whenThereArePosts_receiveCountAfterProvidedId() {
        UserEntity user = userService.save(createValidUser("user1"));
        postService.save(createValidPost(), user.getId());
        postService.save(createValidPost(), user.getId());
        postService.save(createValidPost(), user.getId());
        PostDto fourth = postService.save(createValidPost(), user.getId());
        postService.save(createValidPost(), user.getId());

        ResponseEntity<Map<String, Long>> response = getNewPostCountOfUser(fourth.id(), user.getUsername(), new ParameterizedTypeReference<Map<String, Long>>() {
        });
        assertThat(response.getBody().get("count")).isEqualTo(1);
    }
    @Test
    public void postPost_whenPostHasFileAttachmentAndUserIsAuthorized_fileAttachmentPostRelationIsUpdatedInDatabase() throws IOException {
        userService.save(createValidUser("user1"));
        authenticate("user1");

        MockMultipartFile file = createFile();

        FileAttachment savedFile = fileService.saveAttachment(file);

        Post post = createValidPost();
        post.setAttachment(savedFile);
        ResponseEntity<PostDto> response = postPost(post, PostDto.class);

        FileAttachment inDB = fileAttachmentRepository.findAll().get(0);
        assertThat(inDB.getPost().getId()).isEqualTo(response.getBody().id());

    }
    @Test
    public void postPost_whenPostHasFileAttachmentAndUserIsAuthorized_fileAttachmentRelationIsUpdatedInDatabase() throws IOException {
        userService.save(createValidUser("user1"));
        authenticate("user1");

        MockMultipartFile file = createFile();

        FileAttachment savedFile = fileService.saveAttachment(file);

        Post post = createValidPost();
        post.setAttachment(savedFile);
        ResponseEntity<PostDto> response = postPost(post, PostDto.class);

        Post inDB = postRepository.findById(response.getBody().id()).get();
        assertThat(inDB.getAttachment().getId()).isEqualTo(savedFile.getId());

    }
    @Test
    public void postPost_whenPostHasFileAttachmentAndUserIsAuthorized_receivePostDtoWithAttachment() throws IOException {
        userService.save(createValidUser("user1"));
        authenticate("user1");

        MockMultipartFile file = createFile();

        FileAttachment savedFile = fileService.saveAttachment(file);

        Post post = createValidPost();
        post.setAttachment(savedFile);
        ResponseEntity<PostDto> response = postPost(post, PostDto.class);


        assertThat(response.getBody().attachment().name()).isEqualTo(savedFile.getName());

    }

    private static MockMultipartFile createFile() throws IOException {
        ClassPathResource imageResource = new ClassPathResource("profile.png");
        byte[] fileAsBytes = FileUtils.readFileToByteArray(imageResource.getFile());
        MockMultipartFile file = new MockMultipartFile("profile.png", fileAsBytes);
        return file;
    }

    private <T> ResponseEntity<T> getNewPostsOfUser(Long postId, String username, ParameterizedTypeReference<T> responseType) {
        String path = API_1_0_USERS + "/" + username + "/posts/" + postId + "?direction=after&sort=id,desc";
        return testRestTemplate.exchange(path, HttpMethod.GET, null, responseType);
    }

    private <T> ResponseEntity<T> getNewPostCount(Long postId, ParameterizedTypeReference<T> responseType) {
        String path = API_1_0_POSTS + "/" + postId + "?direction=after&count=true";
        return testRestTemplate.exchange(path, HttpMethod.GET, null, responseType);
    }

    private <T> ResponseEntity<T> getNewPostCountOfUser(Long postId, String username, ParameterizedTypeReference<T> responseType) {
        String path = API_1_0_USERS + "/" + username + "/posts/" + postId + "?direction=after&count=true";
        return testRestTemplate.exchange(path, HttpMethod.GET, null, responseType);
    }

    private <T> ResponseEntity<T> getPostsOfUser(String username, ParameterizedTypeReference<T> responseType) {
        String path = API_1_0_USERS + "/" + username + "/posts";
        return testRestTemplate.exchange(path, HttpMethod.GET, null, responseType);
    }

    private <T> ResponseEntity<T> getNewPosts(long postId, ParameterizedTypeReference<T> responseType) {
        String path = API_1_0_POSTS + "/" + postId + "?direction=after&sort=id,desc";
        return testRestTemplate.exchange(path, HttpMethod.GET, null, responseType);
    }

    private <T> ResponseEntity<T> getOldPosts(long postId, ParameterizedTypeReference<T> responseType) {
        String path = API_1_0_POSTS + "/" + postId + "?direction=before&page=0&size=5&sort=id,desc";
        return testRestTemplate.exchange(path, HttpMethod.GET, null, responseType);
    }

    private <T> ResponseEntity<T> getOldPostsOfUser(Long postId, String username, ParameterizedTypeReference<T> responseType) {
        String path = API_1_0_USERS + "/" + username + "/posts/" + postId + "?direction=before&page=0&size=5&sort=id,desc";
        return testRestTemplate.exchange(path, HttpMethod.GET, null, responseType);
    }

    private <T> ResponseEntity<T> getPosts(ParameterizedTypeReference<T> responseType) {
        return testRestTemplate.exchange(API_1_0_POSTS, HttpMethod.GET, null, responseType);
    }

    private <T> ResponseEntity<T> postPost(Post post, Class<T> responseType) {
        return testRestTemplate.postForEntity(API_1_0_POSTS, post, responseType);
    }

    private void authenticate(String username) {
        testRestTemplate.getRestTemplate().getInterceptors().add(new BasicAuthenticationInterceptor(username, "P4ssword"));
    }

}
