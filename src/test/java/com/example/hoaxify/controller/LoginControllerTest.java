package com.example.hoaxify.controller;

import com.example.hoaxify.error.ApiError;
import com.example.hoaxify.persistence.entity.UserEntity;
import com.example.hoaxify.persistence.entity.repository.UserRepository;
import com.example.hoaxify.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;
import java.util.Objects;

import static com.example.hoaxify.TestUtils.createValidUser;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class LoginControllerTest {

    private static final String API_1_0_LOGIN = "/api/1.0/login";

    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @BeforeEach
    void cleanup() {
        userRepository.deleteAll();
        testRestTemplate.getRestTemplate().getInterceptors().clear();
    }

    @Test
    void postLogin_withoutUserCredentials_receiveUnauthorized() {
        ResponseEntity<Object> response = login(Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void postLogin_withoutUserCredentials_receiveApiError() {
        ResponseEntity<ApiError> response = login(ApiError.class);


        assertThat(Objects.requireNonNull(response.getBody()).getUrl()).isEqualTo(API_1_0_LOGIN);
    }



    @Test
    void postLogin_withIncorrectCredentials_receiveUnauthorized() {
        authenticate();
        ResponseEntity<Object> response = login(Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

//    @Test
//    void postLogin_withIncorrectCredentials_receiveUnauthorizedWithoutWWWAuthenticationHeader() {
//        authenticate();
//        ResponseEntity<Object> response = login(Object.class);
//        assertThat(response.getHeaders().containsKey("WWW-Authenticate")).isFalse();
//    }

    @Test
    void postLogin_withValidCredentials_receiveOk() {
        userService.save(createValidUser());
        authenticate();
        ResponseEntity<Object> response = login(Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
    @Test
    void postLogin_withValidCredentials_receiveLoggedInUserId() {
        UserEntity inDB = userService.save(createValidUser());
        authenticate();
        ResponseEntity<Map<String, Object>> response = login(new ParameterizedTypeReference<>() {
        });

        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        Integer id = (Integer) body.get("id");
        assertThat(Long.valueOf(id)).isEqualTo(inDB.getId());
    }
    @Test
    void postLogin_withValidCredentials_receiveLoggedInUsersImage() {
        UserEntity inDB = userService.save(createValidUser());
        authenticate();
        ResponseEntity<Map<String, Object>> response = login(new ParameterizedTypeReference<>() {
        });

        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        String image = (String) body.get("image");
        assertThat(image).isEqualTo(inDB.getImage());
    }
    @Test
    void postLogin_withValidCredentials_receiveLoggedInUsersDisplayName() {
        UserEntity inDB = userService.save(createValidUser());
        authenticate();
        ResponseEntity<Map<String, Object>> response = login(new ParameterizedTypeReference<>() {
        });

        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        String displayName = (String) body.get("displayName");
        assertThat(displayName).isEqualTo(inDB.getDisplayName());
    }
    @Test
    void postLogin_withValidCredentials_receiveLoggedInUsersUserName() {
        UserEntity inDB = userService.save(createValidUser());
        authenticate();
        ResponseEntity<Map<String, Object>> response = login(new ParameterizedTypeReference<>() {
        });

        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        String username = (String) body.get("username");
        assertThat(username).isEqualTo(inDB.getUsername());
    }
    @Test
    void postLogin_withValidCredentials_notReceiveLoggedInUsersPassword() {
        userService.save(createValidUser());
        authenticate();
        ResponseEntity<Map<String, Object>> response = login(new ParameterizedTypeReference<>() {
        });

        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.containsKey("password")).isFalse();
    }

    private void authenticate() {
        testRestTemplate.getRestTemplate().getInterceptors().add(new BasicAuthenticationInterceptor("test-user", "P4ssword"));
    }

    <T> ResponseEntity<T> login(Class<T> responseType) {
        return testRestTemplate.postForEntity(API_1_0_LOGIN, null, responseType);
    }
    <T> ResponseEntity<T> login(ParameterizedTypeReference<T> responseType) {
        return testRestTemplate.exchange(API_1_0_LOGIN, HttpMethod.POST, null, responseType);
    }
}
