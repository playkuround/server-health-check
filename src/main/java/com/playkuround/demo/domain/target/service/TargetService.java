package com.playkuround.demo.domain.target.service;

import com.playkuround.demo.domain.target.entity.Target;
import com.playkuround.demo.domain.target.exception.TargetNotFoundException;
import com.playkuround.demo.domain.target.repository.TargetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TargetService {

    private final TargetRepository targetRepository;

    public List<Target> findAll() {
        return targetRepository.findAll();
    }

    public Target findById(Long targetId) {
        return targetRepository.findById(targetId)
                .orElseThrow(TargetNotFoundException::new);
    }
}