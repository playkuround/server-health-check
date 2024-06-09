package com.playkuround.demo.domain.target.service;

import com.playkuround.demo.domain.report.repository.ReportRepository;
import com.playkuround.demo.domain.result.repository.ResultRepository;
import com.playkuround.demo.domain.target.entity.Target;
import com.playkuround.demo.domain.target.exception.TargetDuplicationHostException;
import com.playkuround.demo.domain.target.exception.TargetNotFoundException;
import com.playkuround.demo.domain.target.repository.TargetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TargetService {

    private final TargetRepository targetRepository;
    private final ResultRepository resultRepository;
    private final ReportRepository reportRepository;

    public List<Target> findAll() {
        return targetRepository.findAll();
    }

    public Target findById(Long targetId) {
        return targetRepository.findById(targetId)
                .orElseThrow(TargetNotFoundException::new);
    }

    @Transactional
    public void addTarget(String host, String healthCheckURL) {
        if (targetRepository.existsByHost(host)) {
            throw new TargetDuplicationHostException();
        }
        Target target = new Target(host, healthCheckURL);
        targetRepository.save(target);
    }

    @Transactional
    public void updateTarget(Long targetId, String host, String healthCheckURL) {
        Target target = targetRepository.findById(targetId)
                .orElseThrow(TargetNotFoundException::new);

        targetRepository.findByHost(host).ifPresent(t -> {
            if (!Objects.equals(t.getId(), targetId)) {
                throw new TargetDuplicationHostException();
            }
        });

        target.updateInfo(host, healthCheckURL);
    }

    @Transactional
    public void deleteTarget(Long targetId) {
        reportRepository.deleteByTargetId(targetId);
        resultRepository.deleteByTargetId(targetId);
        targetRepository.deleteById(targetId);
    }

    @Transactional
    public void resetSendToday() {
        targetRepository.resetSendToday();
    }
}
