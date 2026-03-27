package com.club;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.club.mapper")
public class ClubManagerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClubManagerApplication.class, args);
    }
}
