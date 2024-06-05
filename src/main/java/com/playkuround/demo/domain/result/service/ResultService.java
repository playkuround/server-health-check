package com.playkuround.demo.domain.result.service;

import com.playkuround.demo.domain.result.entity.Result;
import com.playkuround.demo.domain.result.repository.ResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResultService {

    private final ResultRepository resultRepository;

    public List<Result> findByTargetAndDateSorted(Long targetId, LocalDate date) {
        return resultRepository.findByTargetAndDateSorted(targetId, date);
    }
}
