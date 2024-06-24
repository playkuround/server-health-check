package com.playkuround.demo.domain.user.service;

import com.playkuround.demo.domain.user.entity.User;
import com.playkuround.demo.domain.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class UserLoginServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserLoginService userLoginService;

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
    }

    @DisplayName("DB에 저장되어 있는 토큰이라면 true를 반환한다.")
    @Test
    void login1() {
        // given
        String token = "test_token";
        userRepository.save(new User(token));

        // when
        boolean result = userLoginService.login(token);

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("DB에 없는 토큰이라면 false를 반환한다.")
    @Test
    void login2() {
        // when
        boolean result = userLoginService.login("notFound");

        // then
        assertThat(result).isFalse();
    }

}