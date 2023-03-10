package com.example.hoaxify.controller;

import com.example.hoaxify.TestUtils;
import com.example.hoaxify.error.ApiError;
import com.example.hoaxify.persistence.entity.Hoax;
import com.example.hoaxify.persistence.entity.repository.HoaxRepository;
import com.example.hoaxify.persistence.entity.repository.UserRepository;
import com.example.hoaxify.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class HoaxControllerTest {

    private  final String API_1_0_HOAXES = "/api/1.0/hoaxes";
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HoaxRepository hoaxRepository;

    @BeforeEach
    public void cleanup() {
        hoaxRepository.deleteAll();
        userRepository.deleteAll();
        testRestTemplate.getRestTemplate().getInterceptors().clear();
    }

    @Test
    public void postHoax_whenHoaxIsValidAndUserIsAuthorized_receiveOk() {
        userService.save(TestUtils.createValidUser("user1"));
        authenticate("user1");

        Hoax hoax = TestUtils.createValidHoax();
        ResponseEntity<Object> response = postHoax(hoax, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
    @Test
    public void postHoax_whenHoaxIsValidAndUserIsUnauthorized_receiveUnauthorized() {


        Hoax hoax = TestUtils.createValidHoax();
        ResponseEntity<Object> response = postHoax(hoax, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
    @Test
    public void postHoax_whenHoaxIsValidAndUserIsUnauthorized_receiveApiError() {


        Hoax hoax = TestUtils.createValidHoax();
        ResponseEntity<ApiError> response = postHoax(hoax, ApiError.class);
        assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }
    @Test
    public void postHoax_whenHoaxIsValidAndUserIsAuthorized_hoaxSavedToDatabase() {
        userService.save(TestUtils.createValidUser("user1"));
        authenticate("user1");

        Hoax hoax = TestUtils.createValidHoax();
        postHoax(hoax, Object.class);
        assertThat(hoaxRepository.count()).isEqualTo(1);
    }
    @Test
    public void postHoax_whenHoaxIsValidAndUserIsAuthorized_hoaxSavedToDatabaseWithTimestamp() {
        userService.save(TestUtils.createValidUser("user1"));
        authenticate("user1");

        Hoax hoax = TestUtils.createValidHoax();
        postHoax(hoax, Object.class);

        Hoax inDB = hoaxRepository.findAll().get(0);
        assertThat(inDB.getCreatedAt()).isNotNull();
    }

    private<T> ResponseEntity<T> postHoax(Hoax hoax, Class<T> responseType) {
        return testRestTemplate.postForEntity(API_1_0_HOAXES, hoax, responseType);
    }

    public void authenticate(String username) {
        testRestTemplate.getRestTemplate().getInterceptors().add(new BasicAuthenticationInterceptor(username, "P4ssword"));
    }

}
