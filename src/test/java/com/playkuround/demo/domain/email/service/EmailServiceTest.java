package com.playkuround.demo.domain.email.service;

import com.playkuround.demo.domain.IntegrationTest;
import com.playkuround.demo.domain.email.dto.Mail;
import com.playkuround.demo.domain.email.entity.Email;
import com.playkuround.demo.domain.email.exception.EmailDuplicationHostException;
import com.playkuround.demo.domain.email.repository.EmailRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@IntegrationTest
class EmailServiceTest {

    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private EmailService emailService;

    @SpyBean
    private JavaMailSender mailSender;

    @AfterEach
    void tearDown() {
        emailRepository.deleteAllInBatch();
    }

    @Nested
    @DisplayName("이메일 등록")
    class registerEmailTest {

        @Test
        @DisplayName("기존에 존재하지 않던 이메일 주소이면 저장한다.")
        void registerEmail() {
            // given
            String newEmail = "newEmail@test.com";

            // when
            emailService.registerEmail(newEmail);

            // then
            List<Email> emails = emailRepository.findAll();
            assertThat(emails).hasSize(1)
                    .extracting("email")
                    .containsExactly(newEmail);
        }

        @Test
        @DisplayName("기존에 존재하는 이메일 주소이면 예외가 발생한다.")
        void throwsExceptionWhenEmailExists() {
            // given
            String existingEmail = "existingEmail@test.com";
            emailRepository.save(new Email(existingEmail));

            // expected
            assertThatThrownBy(() -> emailService.registerEmail(existingEmail))
                    .isInstanceOf(EmailDuplicationHostException.class)
                    .hasMessage("중복된 이메일 입니다.");
        }
    }

    @Nested
    @DisplayName("이메일 전송")
    class sendMailTest {

        @Test
        @DisplayName("저장된 이메일 주소들에게 메일을 전송한다.")
        void sendMail() throws MessagingException, IOException {
            // given
            doNothing()
                    .when(mailSender)
                    .send(any(MimeMessage.class));

            List<String> addresses = List.of("address1@test.com", "address2@test.com");
            for (String address : addresses) {
                emailRepository.save(new Email(address));
            }
            String title = "title";
            String content = "content";
            Mail mail = new Mail(title, content);

            // when
            emailService.sendMailAsync(mail);

            // then
            ArgumentCaptor<MimeMessage> argument = ArgumentCaptor.forClass(MimeMessage.class);
            verify(mailSender, times(1))
                    .send(argument.capture());

            MimeMessage value = argument.getValue();
            assertThat(value.getSubject()).isEqualTo(title);
            assertThat(value.getContent().toString()).isEqualTo(content);
            assertThat(value.getAllRecipients()).hasSize(2)
                    .extracting("address")
                    .containsExactlyElementsOf(addresses);
        }

        @Test
        @DisplayName("저장된 이메일 주소가 없으면 메일을 전송하지 않는다.")
        void sendMailWhenEmailAddressIsEmpty() {
            // given
            Mail mail = new Mail("title", "content");

            // when
            emailService.sendMailAsync(mail);

            // then
            verify(mailSender, times(0))
                    .send(any(MimeMessage.class));
        }
    }


}