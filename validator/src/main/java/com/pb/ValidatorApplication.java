package com.pb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// TODO: UnitTesting that are missing
// Validate game state (flop, river etc) against the community cards numbers in a snapshot

// TODO: Major milestones
// Complete verifications
// Send "end of hand" notification and save hand statistics to DB (Mongo?)
// Place some graphs or other means to view statistics
// Engage very simple game play bot (based on prwin)
// The real thing...

@SpringBootApplication
public class ValidatorApplication {
    public static void main(String[] args) {
        SpringApplication.run(ValidatorApplication.class, args);
    }
}
