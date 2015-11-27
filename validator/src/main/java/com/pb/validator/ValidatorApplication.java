package com.pb.validator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

// TODO: UnitTesting that are missing
// Validate game sequence from preflop to river
// Validate syntax of important state info
// Validate consistency of cards throughout the entire hand
// Validate conflicts of cards in a snapshot
// Validate game state (flop, river etc) against the community cards numbers in a snapshot
// Validate OpenPPL Error symbols


@SpringBootApplication
public class ValidatorApplication {
    public static void main(String[] args) {
        SpringApplication.run(ValidatorApplication.class, args);
    }
}
