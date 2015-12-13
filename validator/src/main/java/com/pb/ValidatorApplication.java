package com.pb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// TODO: UnitTesting that are missing
// Validate syntax of important state info
// Validate consistency of cards throughout the entire hand
// Validate conflicts of cards in a snapshot
// Validate game state (flop, river etc) against the community cards numbers in a snapshot
// Validate OpenPPL Error symbols
// Hand validator that checks constant remains the same in the hand (player name etc.)

// TODO: Next steps
// Return error code and upload (or keep localy) images upon errors
// Add more testings (see above)
// Hand validator that checks constant remains the same in the hand (player name etc.)

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
