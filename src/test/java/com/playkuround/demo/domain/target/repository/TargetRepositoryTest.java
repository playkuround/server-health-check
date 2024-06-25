package com.playkuround.demo.domain.target.repository;

import com.playkuround.demo.domain.RepositoryTest;
import com.playkuround.demo.domain.target.entity.Target;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.tuple;

@RepositoryTest
class TargetRepositoryTest {

    @Autowired
    private TargetRepository targetRepository;

    @Test
    @DisplayName("기존에 존재하는 Host이면 true를 반환한다.")
    void existsByHost() {
        // given
        String host = "test_host.com";
        targetRepository.save(new Target(host, "healthCheckURL"));

        // when
        boolean result = targetRepository.existsByHost(host);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("기존에 존재하는 Host이면 entity가 담겨있는 Optional을 반환한다.")
    void findByHost() {
        // given
        String host = "test_host.com";
        targetRepository.save(new Target(host, "healthCheckURL"));

        // when
        Optional<Target> result = targetRepository.findByHost(host);

        // then
        assertThat(result).isPresent().get()
                .extracting("host")
                .isEqualTo(host);
    }

    @TestFactory
    @DisplayName("모든 target entity의 todaySend를 false로 초기화한다.")
    Collection<DynamicTest> resetSendToday() {
        // given
        Target target1 = new Target("host1", "healthCheckURL1");
        Target target2 = new Target("host2", "healthCheckURL2");
        target2.markTodaySendEmail();
        Target target3 = new Target("host3", "healthCheckURL3");
        target3.markTodaySendEmail();
        targetRepository.saveAll(List.of(target1, target2, target3));

        return List.of(
                DynamicTest.dynamicTest("todaySend가 true인 객체가 존재한다.", () -> {
                    // then
                    List<Target> targets = targetRepository.findAll();
                    assertThat(targets).hasSize(3)
                            .extracting("host", "todaySend")
                            .containsExactlyInAnyOrder(
                                    tuple("host1", false),
                                    tuple("host2", true),
                                    tuple("host3", true)
                            );
                }),
                DynamicTest.dynamicTest("resetSendToday 호출 후에는 모든 entity의 todaySend가 false로 변경된다.", () -> {
                    // when
                    targetRepository.resetSendToday();

                    // then
                    List<Target> targets = targetRepository.findAll();
                    assertThat(targets).hasSize(3)
                            .extracting("todaySend")
                            .containsOnly(false);
                })
        );
    }

}