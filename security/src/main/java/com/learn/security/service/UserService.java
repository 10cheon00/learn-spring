package com.learn.security.service;

import com.learn.security.dto.LoginDto;
import com.learn.security.entity.User;
import com.learn.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).get();
    }

    public Optional<User> join(LoginDto loginDto) {
        // save password with Bcrypt encoding
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(loginDto.getPassword());

        User user = new User(loginDto.getUsername(), encodedPassword);
        return Optional.of(userRepository.save(user));
    }
}
