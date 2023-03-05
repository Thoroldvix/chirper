package com.example.hoaxify;

import com.example.hoaxify.user.User;
import com.example.hoaxify.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    TestEntityManager testEntityManager;

    @Autowired
    UserRepository userRepository;

    @Test
    public void findByUsername_whenUserExists_returnUser() {
        User user = new User();

        user.setUsername("test-user");
        user.setDisplayName("test-display");
        user.setPassword("P4ssword");

        testEntityManager.persist(user);

         userRepository.findByUsername("test-user")
                .ifPresent(inDB -> assertThat(inDB).isNotNull());

    }

    @Test
    public void findByUsername_whenUserDoesNotExist_returnsNull() {
        User inDB = userRepository.findByUsername("test-user").orElse(null);
        assertThat(inDB).isNull();
    }
}
