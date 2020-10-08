package com.statestr.es2splunk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Es2splunkApplication {

    public static void main(String[] args) {
        SpringApplication.run(Es2splunkApplication.class, args);

    }



}
