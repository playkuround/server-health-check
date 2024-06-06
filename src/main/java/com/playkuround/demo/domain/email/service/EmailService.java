package com.playkuround.demo.domain.email.service;

import com.playkuround.demo.domain.email.dto.Mail;
import com.playkuround.demo.domain.email.entity.Email;
import com.playkuround.demo.domain.email.exception.EmailDuplicationHostException;
import com.playkuround.demo.domain.email.exception.EmailSendFailException;
import com.playkuround.demo.domain.email.repository.EmailRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
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

    @Async("mailExecutor")
    public void sendMailAsync(Mail mail) {
        try {
            MimeMessage message = mailSender.createMimeMessage();

            List<Email> emails = emailRepository.findAll();
            for (Email email : emails) {
                message.addRecipients(MimeMessage.RecipientType.TO, email.getEmail());
            }

            message.setSubject(mail.title());
            message.setText(mail.content(), mail.encoding(), mail.subtype());
            message.setFrom(new InternetAddress(mail.fromAddress(), mail.fromPersonal()));

            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new EmailSendFailException();
        }

    }
}
