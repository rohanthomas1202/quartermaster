package com.ship.weeklycommits;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WeeklyCommitsApplication {
    public static void main(String[] args) {
        SpringApplication.run(WeeklyCommitsApplication.class, args);
    }
}
