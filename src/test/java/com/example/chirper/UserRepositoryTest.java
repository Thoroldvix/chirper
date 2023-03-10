package com.example.chirper;

import com.example.chirper.persistence.entity.UserEntity;
import com.example.chirper.persistence.entity.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static com.example.chirper.TestUtils.createValidUser;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
 class UserRepositoryTest {

    @Autowired
    TestEntityManager testEntityManager;

    @Autowired
    UserRepository userRepository;

    @Test
     void findByUsername_whenUserExists_returnUser() {

        testEntityManager.persist(createValidUser());

         userRepository.findByUsername("test-user")
                .ifPresent(inDB -> assertThat(inDB).isNotNull());

    }

    @Test
     void findByUsername_whenUserDoesNotExist_returnsNull() {
        UserEntity inDB = userRepository.findByUsername("test-user").orElse(null);
        assertThat(inDB).isNull();
    }
}
