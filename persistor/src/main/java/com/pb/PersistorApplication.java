package com.pb;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan
@SpringBootApplication
public class PersistorApplication {

    public static void main(String[] args) {
        SpringApplication.run(PersistorApplication.class, args);
    }
}
