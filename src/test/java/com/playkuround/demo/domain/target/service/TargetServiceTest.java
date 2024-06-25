package com.playkuround.demo.domain.target.service;

import com.playkuround.demo.domain.IntegrationTest;
import com.playkuround.demo.domain.target.entity.Target;
import com.playkuround.demo.domain.target.exception.TargetDuplicationHostException;
import com.playkuround.demo.domain.target.exception.TargetNotFoundException;
import com.playkuround.demo.domain.target.repository.TargetRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@IntegrationTest
class TargetServiceTest {

    @Autowired
    private TargetRepository targetRepository;

    @Autowired
    private TargetService targetService;

    @AfterEach
    void tearDown() {
        targetRepository.deleteAllInBatch();
    }

    @Nested
    @DisplayName("Target 추가")
    class addTarget {

        @Test
        @DisplayName("중복되지 않은 Host면 target이 저장된다.")
        void success() {
            // when
            String host = "test_host.com";
            String healthCheckURL = "healthCheckURL";
            targetService.addTarget(host, healthCheckURL);

            // then
            List<Target> targets = targetRepository.findAll();
            assertThat(targets).hasSize(1)
                    .extracting("host", "healthCheckURL")
                    .contains(tuple(host, healthCheckURL));
        }

        @Test
        @DisplayName("이미 존재하는 Host이면 exception이 발생한다.")
        void error() {
            // given
            Target target = new Target("test_host.com", "healthCheckURL");
            targetRepository.save(target);

            // expected
            assertThatThrownBy(() -> targetService.addTarget(target.getHost(), target.getHealthCheckURL()))
                    .isInstanceOf(TargetDuplicationHostException.class)
                    .hasMessage("중복된 host 입니다.");
        }
    }

    @Nested
    @DisplayName("Target 수정")
    class updateTarget {

        @Test
        @DisplayName("중복되지 않는 host로 수정이 가능하다.")
        void success1() {
            // given
            Target target = new Target("test_host.com", "healthCheckURL");
            targetRepository.save(target);

            // when
            String updateHost = "new_host.com";
            targetService.updateTarget(target.getId(), updateHost, target.getHealthCheckURL());

            // then
            List<Target> targets = targetRepository.findAll();
            assertThat(targets).hasSize(1)
                    .extracting("id", "host", "healthCheckURL")
                    .contains(tuple(target.getId(), updateHost, target.getHealthCheckURL()));
        }

        @Test
        @DisplayName("Host는 변경하지 않으면서, healthCheckURL만 변경할 수 있다.")
        void success2() {
            // given
            Target target = new Target("test_host.com", "healthCheckURL");
            targetRepository.save(target);

            // when
            String updateHealthCheckURL = "new_healthCheckURL";
            targetService.updateTarget(target.getId(), target.getHost(), updateHealthCheckURL);

            // then
            List<Target> targets = targetRepository.findAll();
            assertThat(targets).hasSize(1)
                    .extracting("id", "host", "healthCheckURL")
                    .contains(tuple(target.getId(), target.getHost(), updateHealthCheckURL));
        }

        @Test
        @DisplayName("변경하고자 하는 target id가 존재하지 않는다면 exception이 발생한다.")
        void error1() {
            // expected
            assertThatThrownBy(() -> targetService.updateTarget(1L, "test_host.com", "healthCheckURL"))
                    .isInstanceOf(TargetNotFoundException.class)
                    .hasMessage("해당 Target을 찾을 수 없습니다.");
        }

        @Test
        @DisplayName("변경하고자 하는 Host가 이미 존재한다면 exception이 발생한다.")
        void error2() {
            // given
            Target target = new Target("test_host_1.com", "healthCheckURL_1");
            Target update = new Target("test_host_2.com", "healthCheckURL_2");
            targetRepository.saveAll(List.of(target, update));

            // expected
            assertThatThrownBy(() -> targetService.updateTarget(update.getId(), target.getHost(), update.getHealthCheckURL()))
                    .isInstanceOf(TargetDuplicationHostException.class)
                    .hasMessage("중복된 host 입니다.");
        }
    }

}