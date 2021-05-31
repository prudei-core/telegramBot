package ru.home.telegram_bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NailBotApplication {

    public static void main(String[] args) {

        SpringApplication.run(NailBotApplication.class, args);
    }

}
