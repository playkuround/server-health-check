package com.playkuround.demo.domain;

import com.playkuround.demo.domain.email.entity.Email;
import com.playkuround.demo.domain.email.repository.EmailRepository;
import com.playkuround.demo.domain.report.service.ReportService;
import com.playkuround.demo.domain.result.entity.Result;
import com.playkuround.demo.domain.result.repository.ResultRepository;
import com.playkuround.demo.domain.target.entity.Target;
import com.playkuround.demo.domain.target.repository.TargetRepository;
import com.playkuround.demo.domain.user.entity.User;
import com.playkuround.demo.domain.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Profile("local")
public class TestData {

    private final UserRepository userRepository;
    private final TargetRepository targetRepository;
    private final ResultRepository resultRepository;
    private final EmailRepository emailRepository;
    private final ReportService reportService;

    @PostConstruct
    public void init() {
        userRepository.save(new User("asdfasdf"));

        Target target1 = new Target("a.com", "https://playkuround.com/api/system-available");
        target1.updateStatus(200);
        targetRepository.save(target1);
        resultRepository.save(new Result(target1, 500));
        resultRepository.save(new Result(target1, 201));
        resultRepository.save(new Result(target1, 200));

        Target target2 = new Target("b.com", "https://playkuround.com/api/system-available");
        target2.updateStatus(500);
        targetRepository.save(target2);

        Target target3 = new Target("c.com", "https://playkuround.com/api/system-available");
        targetRepository.save(target3);

        emailRepository.save(new Email("hsk4991149@naver.com"));
        //emailRepository.save(new Email("test@gmail.com"));
        reportService.dailySaveReport(LocalDate.now());
    }
}
