package com.pb.validator;

import com.pb.dao.Hand;
import com.pb.dao.Snapshot;

/**
 * Validator that validates that the hand is complete
 */
public class HandValidatorFullHand implements HandValidator {
    public final static ValidatorStatus TOO_FEW_SNAPSHOTS       = new ValidatorStatus("Too few snapshots in hand");

    @Override
    public ValidatorStatus validate(Hand hand) {
        Snapshot[] snapshots = hand.getSnapshots();
        Integer highestBetRound = 0;
        // Must be at least reset->preflop
        if (snapshots.length < 2) {
            return TOO_FEW_SNAPSHOTS;
        }
        return ValidatorStatus.OK;
    }
}
