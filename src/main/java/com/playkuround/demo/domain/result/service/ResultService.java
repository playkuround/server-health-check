package com.playkuround.demo.domain.result.service;

import com.playkuround.demo.domain.email.dto.Mail;
import com.playkuround.demo.domain.email.service.EmailService;
import com.playkuround.demo.domain.result.dto.TargetAndStatus;
import com.playkuround.demo.domain.result.entity.Result;
import com.playkuround.demo.domain.result.repository.ResultRepository;
import com.playkuround.demo.domain.target.entity.Target;
import com.playkuround.demo.domain.target.repository.TargetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResultService {

    private final EmailService emailService;
    private final ResultRepository resultRepository;
    private final TargetRepository targetRepository;
    private final String errorEmailTitle = "Health Check Error";

    public List<Result> findByTargetAndDateSorted(Long targetId, LocalDate date) {
        return resultRepository.findByTargetAndDateSorted(targetId, date);
    }

    @Transactional
    public void organizeHealthCheckResult(List<TargetAndStatus> httpResult, LocalDateTime checkedAt) {
        List<Target> targets = new ArrayList<>();
        List<Target> errorTargets = new ArrayList<>();

        for (TargetAndStatus targetAndStatus : httpResult) {
            Target target = targetAndStatus.target();
            int status = targetAndStatus.status();

            resultRepository.save(new Result(target, status, checkedAt));
            target.updateStatus(status, checkedAt);

            if (target.isNeedToSendErrorEmail()) {
                errorTargets.add(target);
            }
            targets.add(target);
        }

        sendErrorEmail(errorTargets);
        targetRepository.saveAll(targets); // update target
    }

    private void sendErrorEmail(List<Target> errorTargets) {
        if (!errorTargets.isEmpty()) {
            String content = createContent(errorTargets);
            Mail mail = new Mail(errorEmailTitle, content);
            emailService.sendMailAsync(mail);
        }
    }

    private String createContent(List<Target> errorTargets) {
        StringBuilder contentBody = new StringBuilder();
        contentBody.append("Health Check Error<br/>");
        for (Target target : errorTargets) {
            contentBody.append(target.getHost())
                    .append(" >> ")
                    .append(target.getHealthCheckURL())
                    .append("<br/>");
            target.markTodaySendEmail();
        }
        return contentBody.toString();
    }

}
