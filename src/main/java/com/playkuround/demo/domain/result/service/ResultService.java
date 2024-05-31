package com.playkuround.demo.domain.result.service;

import com.playkuround.demo.domain.result.entity.Result;
import com.playkuround.demo.domain.result.repository.ResultRepository;
import com.playkuround.demo.domain.target.entity.Target;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResultService {

    private final ResultRepository resultRepository;

    public List<Result> findByTargetSorted(Target target) {
        return resultRepository.findByTargetOrderByCreatedAtDesc(target);
    }
}
