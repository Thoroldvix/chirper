package com.example.chirper;

import com.example.chirper.maper.PostMapper;
import com.example.chirper.persistence.entity.UserEntity;
import com.example.chirper.service.UserService;
import org.apache.tika.Tika;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.util.stream.IntStream;

@SpringBootApplication
public class ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationRunner.class, args);
    }

    @Bean
    @Profile("dev")
    public CommandLineRunner run(UserService userService) {
        return (args) -> IntStream.rangeClosed(1, 15)
                .mapToObj(i -> {
                    UserEntity user = new UserEntity();
                    user.setUsername("user" + i);
                    user.setDisplayName("display" + i);
                    user.setPassword("P4ssword");
                    return user;
                })
                .forEach(userService::save);
    }



    @Bean
    public Tika tika() {
        return new Tika();
    }
}
