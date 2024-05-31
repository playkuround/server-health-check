package com.playkuround.demo.domain;

import com.playkuround.demo.domain.result.entity.Result;
import com.playkuround.demo.domain.result.repository.ResultRepository;
import com.playkuround.demo.domain.target.entity.Target;
import com.playkuround.demo.domain.target.repository.TargetRepository;
import com.playkuround.demo.domain.user.entity.User;
import com.playkuround.demo.domain.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TestData {

    private final UserRepository userRepository;
    private final TargetRepository targetRepository;
    private final ResultRepository resultRepository;

    @PostConstruct
    public void init() {
        userRepository.save(new User("asdfasdf"));

        Target target1 = new Target("a.com", "a.com/health-check");
        target1.updateStatus(200);
        targetRepository.save(target1);
        resultRepository.save(new Result(target1, 500));
        resultRepository.save(new Result(target1, 201));
        resultRepository.save(new Result(target1, 200));

        Target target2 = new Target("b.com", "b.com/health-check");
        target2.updateStatus(500);
        targetRepository.save(target2);

        Target target3 = new Target("c.com", "c.com/health-check");
        targetRepository.save(target3);
    }
}
