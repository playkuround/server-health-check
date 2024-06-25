package com.playkuround.demo.domain.user.repository;

import com.playkuround.demo.domain.RepositoryTest;
import com.playkuround.demo.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("인자로 주어진 토큰이 저장되어 있다면 true를 반환한다.")
    void existsByToken1() {
        // given
        String token = "test_token";
        userRepository.save(new User(token));

        // when
        boolean result = userRepository.existsByToken(token);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("인자로 주어진 토큰이 저장되어 있지 않다면 false를 반환한다.")
    void existsByToken2() {
        // when
        boolean result = userRepository.existsByToken("notFound");

        // then
        assertThat(result).isFalse();
    }

}