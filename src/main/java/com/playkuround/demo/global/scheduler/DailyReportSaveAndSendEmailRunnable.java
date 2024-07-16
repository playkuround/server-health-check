package com.playkuround.demo.global.scheduler;

import com.playkuround.demo.domain.email.dto.Mail;
import com.playkuround.demo.domain.email.service.EmailService;
import com.playkuround.demo.domain.report.entity.Report;
import com.playkuround.demo.domain.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class DailyReportSaveAndSendEmailRunnable implements Runnable {

    private final Clock clock;
    private final EmailService emailService;
    private final ReportService reportService;
    private final TemplateEngine templateEngine;

    private final String title = "Daily Report";
    private final String templatePath = "email/dailyReport-template";
    private final String attributeName = "dailyReportContents";

    @Override
    public void run() {
        LocalDate yesterday = LocalDate.now(clock).minusDays(1);
        Collection<Report> reports = reportService.dailySaveReport(yesterday);

        sendDailyReportEmail(reports);
    }

    private void sendDailyReportEmail(Collection<Report> reports) {
        String content = createContent(reports);
        Mail mail = new Mail(title, content);
        emailService.sendMailAsync(mail);
    }

    private String createContent(Collection<Report> reports) {
        List<DailyReportContent> dailyReportContents = reports.stream()
                .map(report ->
                        new DailyReportContent(
                                report.getTarget().getHost(),
                                report.getSuccessCount(),
                                report.getFailCount(),
                                report.getOtherCount()
                        )
                )
                .toList();

        Context context = new Context();
        context.setVariable(attributeName, dailyReportContents);
        return templateEngine.process(templatePath, context);
    }

}
