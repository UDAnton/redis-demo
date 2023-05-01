package com.github.udanton.redisdemo.api;

import com.github.udanton.redisdemo.persistence.User;
import com.github.udanton.redisdemo.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private UserService userService;

    @PostMapping
    public User createUser() {
        return userService.createRandomUser();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.findUserById(id);
    }

    @GetMapping("/random")
    public User getRandomUser() {
        long randomId = getRandomNumber();
        log.info("Find user with id={}", randomId);
        return userService.findUserById(randomId);
    }

    @GetMapping("/probabilistic/random")
    public User getUserByProbabilisticApproach() {
        long randomId = getRandomNumber();
        return userService.getUserByProbabilisticApproach(randomId);
    }

    private static long getRandomNumber() {
        return (long) ((Math.random() * (10000 - 1)) + 1);
    }

}
