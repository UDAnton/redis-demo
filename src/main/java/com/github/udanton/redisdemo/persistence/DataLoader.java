package com.github.udanton.redisdemo.persistence;

import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader implements ApplicationRunner {

    private final UserRepository userRepository;

    @Override
    public void run(ApplicationArguments args) {
        for (int i = 0; i < 10000; i++) {
            String randomString = RandomStringUtils.random(10, true, false);
            User user = new User();
            user.setName(randomString);
            user.setEmail(String.format("%s.gmail.com", randomString));
            user.setBirthYear(new Date(ThreadLocalRandom.current().nextInt() * 1000L));
            User savedUser = userRepository.save(user);
            log.info("User was created in DB: {}", savedUser);
        }
    }

}
