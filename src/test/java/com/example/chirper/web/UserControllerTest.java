package com.example.chirper.web;

import com.example.chirper.TestPage;
import com.example.chirper.TestUtils;
import com.example.chirper.config.AppConfiguration;
import com.example.chirper.dto.GenericResponse;
import com.example.chirper.dto.UserDto;
import com.example.chirper.dto.UserUpdateDto;
import com.example.chirper.error.ApiError;
import com.example.chirper.persistence.entity.UserEntity;
import com.example.chirper.persistence.entity.repository.UserRepository;
import com.example.chirper.service.UserService;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.example.chirper.TestUtils.createValidUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class UserControllerTest {

    public static final String API_1_0_USERS = "/api/1.0/users";
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    public AppConfiguration appConfiguration;


    @BeforeEach
    public  void cleanUp() throws IOException {
        userRepository.deleteAll();
        FileUtils.cleanDirectory(new File(appConfiguration.getFullProfileImagesPath()));
        FileUtils.cleanDirectory(new File(appConfiguration.getFullAttachmentsPath()));
        testRestTemplate.getRestTemplate().getInterceptors().clear();
    }

    @Test
    public void postUser_whenUserIsValid_receiveOk() {
        UserEntity userEntity = createValidUser();
        ResponseEntity<Object> response = postSignup(userEntity, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }


    @Test
    public void postUser_whenUserIsValid_receiveSuccessMessage() {
        UserEntity userEntity = createValidUser();
        ResponseEntity<GenericResponse> response = postSignup(userEntity, GenericResponse.class);
        assertThat(Objects.requireNonNull(response.getBody()).message()).isNotNull();
    }

    @Test
    public void postUser_whenUserIsValid_userSavedToDatabase() {
        UserEntity userEntity = createValidUser();
        postSignup(userEntity, GenericResponse.class);
        assertThat(userRepository.count()).isEqualTo(1);
    }

    <T> ResponseEntity<T> postSignup(Object request, Class<T> response) {
        return testRestTemplate.postForEntity(API_1_0_USERS, request, response);
    }

    @Test
    public void postUser_whenUserHasNullUsername_receiveBadRequest() {
        UserEntity userEntity = createValidUser();
        userEntity.setUsername(null);
        ResponseEntity<Object> response = postSignup(userEntity, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasNullDisplayName_receiveBadRequest() {
        UserEntity userEntity = createValidUser();
        userEntity.setDisplayName(null);
        ResponseEntity<Object> response = postSignup(userEntity, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasNullPassword_receiveBadRequest() {
        UserEntity userEntity = createValidUser();
        userEntity.setPassword(null);
        ResponseEntity<Object> response = postSignup(userEntity, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserIsValid_passwordIsHashedInDatabase() {
        UserEntity userEntity = createValidUser();
        postSignup(userEntity, GenericResponse.class);
        List<UserEntity> userEntities = userRepository.findAll();
        UserEntity inDB = userEntities.get(0);
        assertThat(inDB.getPassword()).isNotEqualTo(userEntity.getPassword());
    }

    @Test
    public void postUser_whenUserHasUsernameWithLessThanRequired_receiveBadRequest() {
        UserEntity userEntity = createValidUser();
        userEntity.setUsername("abc");
        ResponseEntity<Object> response = postSignup(userEntity, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasDisplayNameWithLessThanRequired_receiveBadRequest() {
        UserEntity userEntity = createValidUser();
        userEntity.setDisplayName("abc");
        ResponseEntity<Object> response = postSignup(userEntity, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasPasswordWithLessThanRequired_receiveBadRequest() {
        UserEntity userEntity = createValidUser();
        userEntity.setPassword("P4sswd");
        ResponseEntity<Object> response = postSignup(userEntity, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasUsernameExceedsTheLengthLimit_receiveBadRequest() {
        UserEntity userEntity = createValidUser();
        String valueOf256Chars = IntStream.rangeClosed(1, 256).mapToObj(x -> "a").collect(Collectors.joining());
        userEntity.setUsername(valueOf256Chars);
        ResponseEntity<Object> response = postSignup(userEntity, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasDisplayNameExceedsTheLengthLimit_receiveBadRequest() {
        UserEntity userEntity = createValidUser();
        String valueOf256Chars = IntStream.rangeClosed(1, 256).mapToObj(x -> "a").collect(Collectors.joining());
        userEntity.setDisplayName(valueOf256Chars);
        ResponseEntity<Object> response = postSignup(userEntity, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasPasswordExceedsTheLengthLimit_receiveBadRequest() {
        UserEntity userEntity = createValidUser();
        String valueOf256Chars = IntStream.rangeClosed(1, 256).mapToObj(x -> "a").collect(Collectors.joining());
        userEntity.setPassword(valueOf256Chars + "A1");
        ResponseEntity<Object> response = postSignup(userEntity, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasPasswordWithAllLowerCase_receiveBadRequest() {
        UserEntity userEntity = createValidUser();
        userEntity.setPassword("allowercase");
        ResponseEntity<Object> response = postSignup(userEntity, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasPasswordWithAllUpperCase_receiveBadRequest() {
        UserEntity userEntity = createValidUser();
        userEntity.setPassword("ALLUPPERCASE");
        ResponseEntity<Object> response = postSignup(userEntity, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserHasPasswordWithAllNumber_receiveBadRequest() {
        UserEntity userEntity = createValidUser();
        userEntity.setPassword("1234567890");
        ResponseEntity<Object> response = postSignup(userEntity, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void postUser_whenUserIsInvalid_ReceiveApiError() {
        UserEntity userEntity = new UserEntity();
        ResponseEntity<ApiError> response = postSignup(userEntity, ApiError.class);
        assertThat(Objects.requireNonNull(response.getBody()).getUrl()).isEqualTo(API_1_0_USERS);

    }

    @Test
    public void postUser_whenUserIsInvalid_ReceiveApiErrorWithValidationErrors() {
        UserEntity userEntity = new UserEntity();
        ResponseEntity<ApiError> response = postSignup(userEntity, ApiError.class);
        assertThat(Objects.requireNonNull(response.getBody()).getValidationErrors()).hasSize(3);

    }

    @Test
    public void postUser_whenUserHasNullUsername_receiveMessageOfNullErrorForUsername() {
        UserEntity userEntity = createValidUser();
        userEntity.setUsername(null);
        ResponseEntity<ApiError> response = postSignup(userEntity, ApiError.class);
        Map<String, String> validationError = Objects.requireNonNull(response.getBody()).getValidationErrors();
        assertThat(validationError.get("username")).isEqualTo("Username cannot be null");
    }

    @Test
    public void postUser_whenUserHasNullUsername_receiveGenericMessageOfNullError() {
        UserEntity userEntity = createValidUser();
        userEntity.setPassword(null);
        ResponseEntity<ApiError> response = postSignup(userEntity, ApiError.class);
        Map<String, String> validationError = Objects.requireNonNull(response.getBody()).getValidationErrors();
        assertThat(validationError.get("password")).isEqualTo("Cannot be null");
    }

    @Test
    public void postUser_whenUserHasInvalidLengthUsername_receiveMessageOfSizeError() {
        UserEntity userEntity = createValidUser();
        userEntity.setUsername("abc");
        ResponseEntity<ApiError> response = postSignup(userEntity, ApiError.class);
        Map<String, String> validationError = Objects.requireNonNull(response.getBody()).getValidationErrors();
        assertThat(validationError.get("username")).isEqualTo("It must have minimum 4 and maximum 255 characters");
    }

    @Test
    public void postUser_whenUserHasInvalidPasswordPattern_receiveMessageOfPasswordPatternError() {
        UserEntity userEntity = createValidUser();
        userEntity.setPassword("alllowercase");
        ResponseEntity<ApiError> response = postSignup(userEntity, ApiError.class);
        Map<String, String> validationError = Objects.requireNonNull(response.getBody()).getValidationErrors();
        assertThat(validationError.get("password")).isEqualTo("Password must have at least one uppercase, one lowercase and one number");
    }

    @Test
    public void postUser_WhenAnotherUserHasSameUsername_receiveBadRequest() {
        userRepository.save(createValidUser());

        UserEntity userEntity = createValidUser();
        ResponseEntity<ApiError> response = postSignup(userEntity, ApiError.class);
        Map<String, String> validationError = Objects.requireNonNull(response.getBody()).getValidationErrors();
        assertThat(validationError.get("username")).isEqualTo("This name is in use");
    }

    @Test
    public void postUser_WhenAnotherUserHasSameUsername_receiveMessageOfDuplicateUsername() {
        userRepository.save(createValidUser());

        UserEntity userEntity = createValidUser();
        ResponseEntity<Object> response = postSignup(userEntity, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void getUsers_whenThereAreNoUsersInDB_receiveOK() {
        ResponseEntity<Object> response = getUsers(new ParameterizedTypeReference<Object>() {
        });
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void getUsers_whenThereIsAUserInDB_receivePageWithUser() {
        userRepository.save(createValidUser());
        ResponseEntity<TestPage<Object>> response =
                getUsers(new ParameterizedTypeReference<TestPage<Object>>() {
                });
        assertThat(Objects.requireNonNull(response.getBody()).getNumberOfElements()).isEqualTo(1);
    }

    @Test
    public void getUsers_whenThereIsAUserInDB_receiveUserWithoutPassword() {
        userRepository.save(createValidUser());
        ResponseEntity<TestPage<Map<String, Object>>> response =
                getUsers(new ParameterizedTypeReference<TestPage<Map<String, Object>>>() {
                });
        Map<String, Object> entity = Objects.requireNonNull(response.getBody()).getContent().get(0);
        assertThat(entity.containsKey("password")).isFalse();
    }

    @Test
    public void getUsers_whenThereAreNoUsersInDB_receivePageWithZeroItems() {
        ResponseEntity<TestPage<Object>> response = getUsers(new ParameterizedTypeReference<TestPage<Object>>() {
        });
        assertThat(Objects.requireNonNull(response.getBody()).getTotalElements()).isEqualTo(0);
    }

    @Test
    public void getUsers_whenPageIsRequestedFor3ItemsPerPageWhereTheDatabaseHas20Users_receive3Users() {
        IntStream.rangeClosed(1, 20).mapToObj(i -> "test-user-" + i).map(TestUtils::createValidUser)
                .forEach(userRepository::save);

        String path = API_1_0_USERS + "?page=0&size=3";
        ResponseEntity<TestPage<Object>> response = getUsers(path, new ParameterizedTypeReference<TestPage<Object>>() {
        });
        assertThat(Objects.requireNonNull(response.getBody()).getContent().size()).isEqualTo(3);

    }

    @Test
    public void getUsers_whenPageSizeNotProvided_receivePageSizeAs10() {
        ResponseEntity<TestPage<Object>> response = getUsers(new ParameterizedTypeReference<TestPage<Object>>() {
        });
        assertThat(Objects.requireNonNull(response.getBody()).getSize()).isEqualTo(10);
    }

    @Test
    public void getUsers_whenPageSizeIsGreaterThan100_receivePageSizeAs100() {
        String path = API_1_0_USERS + "?size=500";
        ResponseEntity<TestPage<Object>> response = getUsers(path, new ParameterizedTypeReference<TestPage<Object>>() {
        });
        assertThat(Objects.requireNonNull(response.getBody()).getSize()).isEqualTo(100);
    }

    @Test
    public void getUsers_whenPageSizeIsNegative_receivePageSizeAs10() {
        String path = API_1_0_USERS + "?size=-5";
        ResponseEntity<TestPage<Object>> response = getUsers(path, new ParameterizedTypeReference<TestPage<Object>>() {
        });
        assertThat(Objects.requireNonNull(response.getBody()).getSize()).isEqualTo(10);
    }

    @Test
    public void getUsers_whenPageIsNegative_receiveFirstPage() {
        String path = API_1_0_USERS + "?page=-5";
        ResponseEntity<TestPage<Object>> response = getUsers(path, new ParameterizedTypeReference<TestPage<Object>>() {
        });
        assertThat(Objects.requireNonNull(response.getBody()).getNumber()).isEqualTo(0);
    }

    @Test
    public void getUsers_whenUserLoggedIn_receivePageWithoutLoggedInUser() {
        userService.save(createValidUser("user1"));
        userService.save(createValidUser("user2"));
        userService.save(createValidUser("user3"));

        authenticate("user1");

        ResponseEntity<TestPage<Object>> response = getUsers(new ParameterizedTypeReference<TestPage<Object>>() {
        });
        assertThat(Objects.requireNonNull(response.getBody()).getTotalElements()).isEqualTo(2);

    }

    @Test
    public void getUserByUsername_whenUserExist_receiveOk() {
        String username = "test-user";
        userService.save(createValidUser(username));
        ResponseEntity<Object> response = getUser(username, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void getUserByUsername_whenUserExist_receiveUserWithoutPassword() {
        String username = "test-user";
        userService.save(createValidUser(username));
        ResponseEntity<String> response = getUser(username, String.class);
        assertThat(Objects.requireNonNull(response.getBody()).contains("password")).isFalse();
    }

    @Test
    public void getUserByUsername_whenUserDoesNotExist_receiveNotFound() {

        ResponseEntity<String> response = getUser("username", String.class);
        assertThat(Objects.requireNonNull(response.getStatusCode())).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void getUserByUsername_whenUserDoesNotExist_receiveApiError() {

        ResponseEntity<ApiError> response = getUser("username", ApiError.class);
        assertThat(Objects.requireNonNull(response.getBody()).getMessage().contains("username")).isTrue();
    }

    @Test
    public void putUser_whenUnauthorizedUserSendsTheRequest_receiveUnauthorized() {
        ResponseEntity<Object> response = putUser(123, null, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void putUser_whenUnauthorizedUserSendsTheRequest_receiveApiError() {
        ResponseEntity<ApiError> response = putUser(123, null, ApiError.class);
        assertThat(Objects.requireNonNull(response.getBody()).getUrl()).contains("users/123");
    }

    @Test
    public void putUser_whenAuthorizedUserSendsUpdateForAnotherUser_receiveForbidden() {
        UserEntity user = userService.save(createValidUser("user1"));
        authenticate(user.getUsername());

        long anotherUserId = user.getId() + 123;
        ResponseEntity<Object> response = putUser(anotherUserId, null, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void putUser_whenAuthorizedUserSendsUpdateForAnotherUser_receiveApiError() {
        UserEntity user = userService.save(createValidUser("user1"));
        authenticate(user.getUsername());

        long anotherUserId = user.getId() + 123;
        ResponseEntity<ApiError> response = putUser(anotherUserId, null, ApiError.class);
        assertThat(Objects.requireNonNull(response.getBody()).getUrl()).contains("users/" + anotherUserId);
    }

    @Test
    public void putUser_whenValidRequestBodyFromAuthorizedUser_receiveOk() {
        UserEntity user = userService.save(createValidUser("user1"));
        authenticate(user.getUsername());
        UserUpdateDto updatedUser = new UserUpdateDto(user.getDisplayName(), null);

        HttpEntity<UserUpdateDto> requestEntity = new HttpEntity<>(updatedUser);
        ResponseEntity<Object> response = putUser(user.getId(), requestEntity, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void putUser_whenValidRequestBodyFromAuthorizedUser_displayNameUpdated() {
        UserEntity user = userService.save(createValidUser("user1"));
        authenticate(user.getUsername());
        UserUpdateDto updatedUser = new UserUpdateDto(user.getDisplayName(), null);

        Optional<UserEntity> userInDB = userRepository.findByUsername(user.getUsername());
        assertTrue(userInDB.isPresent());
        assertThat(userInDB.get().getDisplayName()).isEqualTo(updatedUser.displayName());
    }

    @Test
    public void putUser_whenValidRequestBodyFromAuthorizedUser_receiveUserDtoWithUpdatedDisplayName() {
        UserEntity user = userService.save(createValidUser("user1"));
        authenticate(user.getUsername());
        UserUpdateDto updatedUser = new UserUpdateDto(user.getDisplayName(), null);

        HttpEntity<UserUpdateDto> requestEntity = new HttpEntity<>(updatedUser);
        ResponseEntity<UserDto> response = putUser(user.getId(), requestEntity, UserDto.class);

        assertThat(Objects.requireNonNull(response.getBody()).displayName()).isEqualTo(updatedUser.displayName());

    }

    @Test
    public void putUser_withValidRequestBodyWithSupportedImageFromAuthorizedUser_receiveUserDtoWithRandomImageName() throws IOException {
        UserEntity user = userService.save(createValidUser("user1"));
        authenticate(user.getUsername());




        String imageString = readFileToBase64("profile.png");
        UserUpdateDto updatedUser = new UserUpdateDto(user.getDisplayName(), imageString);


        HttpEntity<UserUpdateDto> requestEntity = new HttpEntity<>(updatedUser);
        ResponseEntity<UserDto> response = putUser(user.getId(), requestEntity, UserDto.class);

        assertThat(Objects.requireNonNull(response.getBody()).image()).isNotEqualTo("profile-image.png");

    }

    @Test
    public void putUser_withValidRequestBodyWithSupportedImageFromAuthorizedUser_imageIsStoredUnderProfileFolder() throws IOException {
        UserEntity user = userService.save(createValidUser("user1"));
        authenticate(user.getUsername());




        String imageString = readFileToBase64("profile.png");
        UserUpdateDto updatedUser = new UserUpdateDto(user.getDisplayName(), imageString);


        HttpEntity<UserUpdateDto> requestEntity = new HttpEntity<>(updatedUser);
        ResponseEntity<UserDto> response = putUser(user.getId(), requestEntity, UserDto.class);

        String storedImageName = response.getBody().image();

        String profilePicturePath = appConfiguration.getFullProfileImagesPath() + "/" + storedImageName;

        File storedImage = new File(profilePicturePath);

        assertThat(storedImage.exists()).isTrue();

    }
    @Test
    public void putUser_withValidRequestBodyWithJPGImageFromAuthorizedUser_receiveOk() throws IOException {
        UserEntity user = userService.save(createValidUser("user1"));
        authenticate(user.getUsername());



        String imageString = readFileToBase64("test-jpg.jpg");
        UserUpdateDto updatedUser = new UserUpdateDto(user.getDisplayName(), imageString);


        HttpEntity<UserUpdateDto> requestEntity = new HttpEntity<>(updatedUser);
        ResponseEntity<UserDto> response = putUser(user.getId(), requestEntity, UserDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    }
    @Test
    public void putUser_withValidRequestBodyWithGIFImageFromAuthorizedUser_receiveBadRequest() throws IOException {
        UserEntity user = userService.save(createValidUser("user1"));
        authenticate(user.getUsername());



        String imageString = readFileToBase64("test-gif.gif");
        UserUpdateDto updatedUser = new UserUpdateDto(user.getDisplayName(), imageString);

        HttpEntity<UserUpdateDto> requestEntity = new HttpEntity<>(updatedUser);
        ResponseEntity<Object> response = putUser(user.getId(), requestEntity, Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    }
    @Test
    public void putUser_withValidRequestBodyWithTXTImageFromAuthorizedUser_receiveValidationErrorForProfileImage() throws IOException {
        UserEntity user = userService.save(createValidUser("user1"));
        authenticate(user.getUsername());




        String imageString = readFileToBase64("test-txt.txt");
        UserUpdateDto updatedUser = new UserUpdateDto(user.getDisplayName(), imageString);


        HttpEntity<UserUpdateDto> requestEntity = new HttpEntity<>(updatedUser);
        ResponseEntity<ApiError> response = putUser(user.getId(), requestEntity, ApiError.class);
        Map<String, String> validationErrors = response.getBody().getValidationErrors();

        assertThat(validationErrors.get("image")).isEqualTo("Only PNG and JPG files are allowed");

    }
    @Test
    public void putUser_withInvalidRequestBodyWithNullDisplayNameFromAuthorizedUser_receiveBadRequest() throws IOException {
        UserEntity user = userService.save(createValidUser("user1"));
        authenticate(user.getUsername());
        UserUpdateDto updatedUser = new UserUpdateDto(null, null);



        HttpEntity<UserUpdateDto> requestEntity = new HttpEntity<>(updatedUser);
        ResponseEntity<UserDto> response = putUser(user.getId(), requestEntity, UserDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    }
    @Test
    public void putUser_withInvalidRequestBodyWithLessThanMinSizeForDisplayNameFromAuthorizedUser_receiveBadRequest() throws IOException {
        UserEntity user = userService.save(createValidUser("user1"));
        authenticate(user.getUsername());

        UserUpdateDto updatedUser = new UserUpdateDto("a", null);




        HttpEntity<UserUpdateDto> requestEntity = new HttpEntity<>(updatedUser);
        ResponseEntity<UserDto> response = putUser(user.getId(), requestEntity, UserDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    }
    @Test
    public void putUser_withInvalidRequestBodyWithMoreThanMaxSizeForDisplayNameFromAuthorizedUser_receiveBadRequest() throws IOException {
        UserEntity user = userService.save(createValidUser("user1"));
        authenticate(user.getUsername());

        String valueOf256Chars = IntStream.rangeClosed(1, 256).mapToObj(i -> "a").collect(Collectors.joining());
        UserUpdateDto updatedUser =  new UserUpdateDto(user.getDisplayName(), valueOf256Chars);



        HttpEntity<UserUpdateDto> requestEntity = new HttpEntity<>(updatedUser);
        ResponseEntity<UserDto> response = putUser(user.getId(), requestEntity, UserDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    }
    @Test
    public void putUser_withValidRequestBodyWithJPGImageForUserWhoHasImage_removesOldImageFromStorage() throws IOException {
        UserEntity user = userService.save(createValidUser("user1"));
        authenticate(user.getUsername());



        String imageString = readFileToBase64("test-jpg.jpg");
        UserUpdateDto updatedUser = new UserUpdateDto(user.getDisplayName(), imageString);


        HttpEntity<UserUpdateDto> requestEntity = new HttpEntity<>(updatedUser);
        ResponseEntity<UserDto> response = putUser(user.getId(), requestEntity, UserDto.class);

        putUser(user.getId(), requestEntity, UserDto.class);

        String storedImageName = response.getBody().image();
        String profilePicturePath = appConfiguration.getFullProfileImagesPath() + "/" + storedImageName;
        File storedImage = new File(profilePicturePath);
        assertThat(storedImage.exists()).isFalse();

    }
    private String readFileToBase64(String fileName) throws IOException {
        ClassPathResource imageResource = new ClassPathResource(fileName);
        byte[] imageArr = FileUtils.readFileToByteArray(imageResource.getFile());
        return Base64.getEncoder().encodeToString(imageArr);
    }


    public <T> ResponseEntity<T> getUsers(ParameterizedTypeReference<T> responseType) {
        return testRestTemplate.exchange(API_1_0_USERS, HttpMethod.GET, null, responseType);
    }

    public <T> ResponseEntity<T> getUsers(String path, ParameterizedTypeReference<T> responseType) {
        return testRestTemplate.exchange(path, HttpMethod.GET, null, responseType);
    }

    public void authenticate(String username) {
        testRestTemplate.getRestTemplate().getInterceptors().add(new BasicAuthenticationInterceptor(username, "P4ssword"));
    }

    public <T> ResponseEntity<T> getUser(String username, Class<T> responseType) {
        String path = API_1_0_USERS + "/" + username;
        return testRestTemplate.getForEntity(path, responseType);
    }

    public <T> ResponseEntity<T> putUser(long id, HttpEntity<?> requestEntity, Class<T> responseType) {
        String path = API_1_0_USERS + "/" + id;
        return testRestTemplate.exchange(path, HttpMethod.PUT, requestEntity, responseType);
    }



}
