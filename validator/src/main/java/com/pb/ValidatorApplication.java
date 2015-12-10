package com.pb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// TODO: UnitTesting that are missing
// Validate game sequence from preflop to river
// Validate syntax of important state info
// Validate consistency of cards throughout the entire hand
// Validate conflicts of cards in a snapshot
// Validate game state (flop, river etc) against the community cards numbers in a snapshot
// Validate OpenPPL Error symbols

// TODO: Next steps
// Complete parsing of Hand json
// Build validator as a server and connect to gateway
// Return error code and upload (or keep localy) images upon errors
// Add more testings (see above)


@SpringBootApplication
public class ValidatorApplication {
    public static void main(String[] args) {
        SpringApplication.run(ValidatorApplication.class, args);
    }
}