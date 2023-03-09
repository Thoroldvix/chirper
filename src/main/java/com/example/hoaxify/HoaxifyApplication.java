package com.example.hoaxify;

import com.example.hoaxify.persistence.entity.UserEntity;
import com.example.hoaxify.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.util.stream.IntStream;

@SpringBootApplication
public class HoaxifyApplication {

    public static void main(String[] args) {
        SpringApplication.run(HoaxifyApplication.class, args);
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
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

}
