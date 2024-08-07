package com.playkuround.demo.global.scheduler;

import com.playkuround.demo.domain.email.dto.Mail;
import com.playkuround.demo.domain.email.service.EmailService;
import com.playkuround.demo.domain.report.entity.Report;
import com.playkuround.demo.domain.report.service.ReportService;
import com.playkuround.demo.domain.target.entity.Target;
import com.playkuround.demo.global.scheduler.runnable.DailyReportSaveAndSendEmailRunnable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.lang.reflect.Field;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class DailyReportSaveAndSendEmailRunnableTest {

    @Test
    @DisplayName("dailySaveReport 메서드를 실행하고, 생성된 Report를 바탕으로 이메일을 전송한다.")
    void dailyReportSaveAndSendEmail() throws NoSuchFieldException, IllegalAccessException {
        // given
        Clock clock = Clock.fixed(Instant.now(), ZoneId.systemDefault());
        EmailService emailService = mock(EmailService.class);

        LocalDate yesterday = LocalDate.now(clock).minusDays(1);
        List<Report> reports = List.of(
                new Report(new Target("host1", "healthCheck1"), yesterday),
                new Report(new Target("host2", "healthCheck2"), yesterday)
        );
        setFieldByReflection(reports.get(0), 1, 4, 2);
        setFieldByReflection(reports.get(1), 5, 0, 1);
        ReportService reportService = mock(ReportService.class);
        when(reportService.dailySaveReport(yesterday)).thenReturn(reports);

        String content = "content";
        TemplateEngine templateEngine = mock(TemplateEngine.class);
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn(content);

        DailyReportSaveAndSendEmailRunnable dailyReportSaveAndSendEmailRunnable = new DailyReportSaveAndSendEmailRunnable(clock, emailService, reportService, templateEngine);

        // when
        dailyReportSaveAndSendEmailRunnable.run();

        // then
        verify(reportService, times(1)).dailySaveReport(yesterday);
        verify(emailService, times(1)).sendMailAsync(new Mail("Daily Report", content));

        ArgumentCaptor<Context> contextArgumentCaptor = ArgumentCaptor.forClass(Context.class);
        verify(templateEngine, times(1)).process(anyString(), contextArgumentCaptor.capture());

        Context context = contextArgumentCaptor.getValue();
        List<DailyReportContent> dailyReportContents = (List<DailyReportContent>) context.getVariable("dailyReportContents");
        assertThat(dailyReportContents).containsExactly(
                new DailyReportContent("host1", 1, 4, 2),
                new DailyReportContent("host2", 5, 0, 1)
        );
    }

    private void setFieldByReflection(Report report, int successCount, int failCount, int otherCount) throws NoSuchFieldException, IllegalAccessException {
        Class<Report> reportClass = Report.class;

        Field successCountField = reportClass.getDeclaredField("successCount");
        Field failCountField = reportClass.getDeclaredField("failCount");
        Field otherCountField = reportClass.getDeclaredField("otherCount");

        successCountField.setAccessible(true);
        failCountField.setAccessible(true);
        otherCountField.setAccessible(true);

        successCountField.set(report, successCount);
        failCountField.set(report, failCount);
        otherCountField.set(report, otherCount);
    }

}