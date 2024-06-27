package com.self_back;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.self_back.mapper")
public class SelfBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(SelfBackApplication.class, args);
    }

}
