package com.playkuround.demo.domain.email.service;

import com.playkuround.demo.domain.email.entity.Email;
import com.playkuround.demo.domain.email.exception.EmailDuplicationHostException;
import com.playkuround.demo.domain.email.repository.EmailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final EmailRepository emailRepository;

    @Transactional
    public void registerEmail(String email) {
        if (emailRepository.existsByEmail(email)) {
            throw new EmailDuplicationHostException();
        }
        emailRepository.save(new Email(email));
    }

    public List<Email> findAll() {
        return emailRepository.findAll();
    }

    @Transactional
    public void delete(Long emailId) {
        emailRepository.deleteById(emailId);
    }
}
