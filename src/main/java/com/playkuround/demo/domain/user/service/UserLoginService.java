package com.playkuround.demo.domain.user.service;

import com.playkuround.demo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserLoginService {

    private final UserRepository userRepository;

    public boolean login(String token) {
        return userRepository.existsByToken(token);
    }

}
