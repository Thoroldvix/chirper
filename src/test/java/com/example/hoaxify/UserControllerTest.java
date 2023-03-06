package com.example.hoaxify;

import com.example.hoaxify.dto.GenericResponse;
import com.example.hoaxify.error.ApiError;
import com.example.hoaxify.persistence.entity.UserEntity;
import com.example.hoaxify.persistence.entity.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;


@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
 class UserControllerTest {

     static final String API_1_0_USERS = "/api/1.0/users";
    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void cleanUp() {
        userRepository.deleteAll();
        testRestTemplate.getRestTemplate().getInterceptors().clear();
    }

    @Test
     void postUser_whenUserIsValid_receiveOk() {
        UserEntity userEntity = TestUtils.createValidUser();
        ResponseEntity<Object> response = postSignup(userEntity, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }



    @Test
     void postUser_whenUserIsValid_receiveSuccessMessage() {
        UserEntity userEntity = TestUtils.createValidUser();
        ResponseEntity<GenericResponse> response = postSignup(userEntity, GenericResponse.class);
        assertThat(Objects.requireNonNull(response.getBody()).getMessage()).isNotNull();
    }

    @Test
     void postUser_whenUserIsValid_userSavedToDatabase() {
        UserEntity userEntity = TestUtils.createValidUser();
        postSignup(userEntity, GenericResponse.class);
        assertThat(userRepository.count()).isEqualTo(1);
    }

     <T> ResponseEntity<T> postSignup(Object request, Class<T> response) {
        return testRestTemplate.postForEntity(API_1_0_USERS, request, response);
    }

    @Test
     void postUser_whenUserHasNullUsername_receiveBadRequest() {
        UserEntity userEntity = TestUtils.createValidUser();
        userEntity.setUsername(null);
        ResponseEntity<Object> response = postSignup(userEntity, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
     void postUser_whenUserHasNullDisplayName_receiveBadRequest() {
        UserEntity userEntity = TestUtils.createValidUser();
        userEntity.setDisplayName(null);
        ResponseEntity<Object> response = postSignup(userEntity, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
     void postUser_whenUserHasNullPassword_receiveBadRequest() {
        UserEntity userEntity = TestUtils.createValidUser();
        userEntity.setPassword(null);
        ResponseEntity<Object> response = postSignup(userEntity, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
     void postUser_whenUserIsValid_passwordIsHashedInDatabase() {
        UserEntity userEntity = TestUtils.createValidUser();
        postSignup(userEntity, GenericResponse.class);
        List<UserEntity> userEntities = userRepository.findAll();
        UserEntity inDB = userEntities.get(0);
        assertThat(inDB.getPassword()).isNotEqualTo(userEntity.getPassword());
    }

    @Test
     void postUser_whenUserHasUsernameWithLessThanRequired_receiveBadRequest() {
        UserEntity userEntity = TestUtils.createValidUser();
        userEntity.setUsername("abc");
        ResponseEntity<Object> response = postSignup(userEntity, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
     void postUser_whenUserHasDisplayNameWithLessThanRequired_receiveBadRequest() {
        UserEntity userEntity = TestUtils.createValidUser();
        userEntity.setDisplayName("abc");
        ResponseEntity<Object> response = postSignup(userEntity, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
     void postUser_whenUserHasPasswordWithLessThanRequired_receiveBadRequest() {
        UserEntity userEntity = TestUtils.createValidUser();
        userEntity.setPassword("P4sswd");
        ResponseEntity<Object> response = postSignup(userEntity, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
     void postUser_whenUserHasUsernameExceedsTheLengthLimit_receiveBadRequest() {
        UserEntity userEntity = TestUtils.createValidUser();
        String valueOf256Chars = IntStream.rangeClosed(1, 256).mapToObj(x -> "a").collect(Collectors.joining());
        userEntity.setUsername(valueOf256Chars);
        ResponseEntity<Object> response = postSignup(userEntity, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
     void postUser_whenUserHasDisplayNameExceedsTheLengthLimit_receiveBadRequest() {
        UserEntity userEntity = TestUtils.createValidUser();
        String valueOf256Chars = IntStream.rangeClosed(1, 256).mapToObj(x -> "a").collect(Collectors.joining());
        userEntity.setDisplayName(valueOf256Chars);
        ResponseEntity<Object> response = postSignup(userEntity, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
     void postUser_whenUserHasPasswordExceedsTheLengthLimit_receiveBadRequest() {
        UserEntity userEntity = TestUtils.createValidUser();
        String valueOf256Chars = IntStream.rangeClosed(1, 256).mapToObj(x -> "a").collect(Collectors.joining());
        userEntity.setPassword(valueOf256Chars + "A1");
        ResponseEntity<Object> response = postSignup(userEntity, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
     void postUser_whenUserHasPasswordWithAllLowerCase_receiveBadRequest() {
        UserEntity userEntity = TestUtils.createValidUser();
        userEntity.setPassword("allowercase");
        ResponseEntity<Object> response = postSignup(userEntity, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
     void postUser_whenUserHasPasswordWithAllUpperCase_receiveBadRequest() {
        UserEntity userEntity = TestUtils.createValidUser();
        userEntity.setPassword("ALLUPPERCASE");
        ResponseEntity<Object> response = postSignup(userEntity, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
     void postUser_whenUserHasPasswordWithAllNumber_receiveBadRequest() {
        UserEntity userEntity = TestUtils.createValidUser();
        userEntity.setPassword("1234567890");
        ResponseEntity<Object> response = postSignup(userEntity, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
     void postUser_whenUserIsInvalid_ReceiveApiError() {
        UserEntity userEntity = new UserEntity();
        ResponseEntity<ApiError> response = postSignup(userEntity, ApiError.class);
        assertThat(Objects.requireNonNull(response.getBody()).getUrl()).isEqualTo(API_1_0_USERS);

    }

    @Test
     void postUser_whenUserIsInvalid_ReceiveApiErrorWithValidationErrors() {
        UserEntity userEntity = new UserEntity();
        ResponseEntity<ApiError> response = postSignup(userEntity, ApiError.class);
        assertThat(Objects.requireNonNull(response.getBody()).getValidationErrors()).hasSize(3);

    }

    @Test
     void postUser_whenUserHasNullUsername_receiveMessageOfNullErrorForUsername() {
        UserEntity userEntity = TestUtils.createValidUser();
        userEntity.setUsername(null);
        ResponseEntity<ApiError> response = postSignup(userEntity, ApiError.class);
        Map<String, String> validationError = Objects.requireNonNull(response.getBody()).getValidationErrors();
        assertThat(validationError.get("username")).isEqualTo("Username cannot be null");
    }

    @Test
     void postUser_whenUserHasNullUsername_receiveGenericMessageOfNullError() {
        UserEntity userEntity = TestUtils.createValidUser();
        userEntity.setPassword(null);
        ResponseEntity<ApiError> response = postSignup(userEntity, ApiError.class);
        Map<String, String> validationError = Objects.requireNonNull(response.getBody()).getValidationErrors();
        assertThat(validationError.get("password")).isEqualTo("Cannot be null");
    }

    @Test
     void postUser_whenUserHasInvalidLengthUsername_receiveGenericMessageOfSizeError() {
        UserEntity userEntity = TestUtils.createValidUser();
        userEntity.setUsername("abc");
        ResponseEntity<ApiError> response = postSignup(userEntity, ApiError.class);
        Map<String, String> validationError = Objects.requireNonNull(response.getBody()).getValidationErrors();
        assertThat(validationError.get("username")).isEqualTo("It must have minimum 4 and maximum 255 characters");
    }

    @Test
     void postUser_whenUserHasInvalidPasswordPattern_receiveMessageOfPasswordPatternError() {
        UserEntity userEntity = TestUtils.createValidUser();
        userEntity.setPassword("alllowercase");
        ResponseEntity<ApiError> response = postSignup(userEntity, ApiError.class);
        Map<String, String> validationError = Objects.requireNonNull(response.getBody()).getValidationErrors();
        assertThat(validationError.get("password")).isEqualTo("Password must have at least one uppercase, one lowercase and one number");
    }

    @Test
     void postUser_WhenAnotherUserHasSameUsername_receiveBadRequest() {
        userRepository.save(TestUtils.createValidUser());

        UserEntity userEntity = TestUtils.createValidUser();
        ResponseEntity<ApiError> response = postSignup(userEntity, ApiError.class);
        Map<String, String> validationError = Objects.requireNonNull(response.getBody()).getValidationErrors();
        assertThat(validationError.get("username")).isEqualTo("This name is in use");
    }
    @Test
     void postUser_WhenAnotherUserHasSameUsername_receiveMessageOfDuplicateUsername() {
        userRepository.save(TestUtils.createValidUser());

        UserEntity userEntity = TestUtils.createValidUser();
        ResponseEntity<Object> response = postSignup(userEntity, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

}
