package com.playkuround.demo.domain.email.entity;

import com.playkuround.demo.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Email extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @jakarta.validation.constraints.Email
    @Column(nullable = false, unique = true)
    private String email;

    public Email(String email) {
        this.email = email;
    }
}
