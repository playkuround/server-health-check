package com.playkuround.demo.domain.user.repository;

import com.playkuround.demo.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByToken(String token);

}
