package com.playkuround.demo.domain.learning;

import com.playkuround.demo.domain.target.entity.Target;
import com.playkuround.demo.domain.target.repository.TargetRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Disabled
@SpringBootTest
@ActiveProfiles("test")
public class TransactionalLearningTest {

    @Autowired
    private PlatformTransactionManager txManager;

    @Autowired
    private TargetRepository targetRepository;

    @AfterEach
    void tearDown() {
        targetRepository.deleteAllInBatch();
    }

    private final AtomicBoolean lock = new AtomicBoolean(false);

    @Test
    @DisplayName("특정 row에 트랜잭션이 중첩해 있다면, 빠른 트랜잭션 번호의 변경사항이 반영된다.")
    void transactionTest() {
        // given
        targetRepository.save(new Target("host", "healthCheck"));

        // when
        Thread thread1 = new Thread(transaction1());
        Thread thread2 = new Thread(transaction2());

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // then
        Target target = targetRepository.findAll().get(0);
        log.info("target's host = {}", target.getHost());
    }

    private Runnable transaction1() {
        return () -> {
            TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
            log.info("transaction1 start. isNewTransaction = {}", status.isNewTransaction());
            Target target = targetRepository.findAll().get(0);
            lock.set(true);
            try {
                target.updateInfo("t1_host", "healthCheck1");

                while (lock.get()) ;
                log.info("transaction1 commit");
                txManager.commit(status);
            } catch (Exception e) {
                txManager.rollback(status);
            }
        };
    }

    private Runnable transaction2() {
        return () -> {
            while (!lock.get()) ;
            TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
            log.info("transaction2 start. isNewTransaction = {}", status.isNewTransaction());
            Target target = targetRepository.findAll().get(0);
            try {
                target.updateInfo("t2_host", "healthCheck2");

                log.info("transaction2 commit");
                txManager.commit(status);
            } catch (Exception e) {
                txManager.rollback(status);
            }
            lock.set(false);
        };
    }

}
