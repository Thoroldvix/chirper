package com.example.hoaxify.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

        private final UserRepository userRepository;

        private final BCryptPasswordEncoder passwordEncoder;

        @Autowired
        public UserService(UserRepository userRepository) {
            this.userRepository = userRepository;
            this.passwordEncoder = new BCryptPasswordEncoder();
        }

        public User save(User user) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
           return userRepository.save(user);
        }
}
