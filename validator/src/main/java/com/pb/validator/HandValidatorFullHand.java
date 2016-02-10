package com.pb.validator;

import com.pb.dao.Hand;
import com.pb.dao.Snapshot;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Validator that validates that the hand is complete
 */
@Component
@NoArgsConstructor
public class HandValidatorFullHand implements HandValidator {
    public final static ValidatorStatus TOO_FEW_SNAPSHOTS       = new ValidatorStatus("Too few snapshots in hand");

    @Override
    public ValidatorStatus validate(Hand hand) {
        Snapshot[] snapshots = hand.getSnapshots();
        // Must be at least reset->preflop
        if (snapshots.length < 2) {
            return TOO_FEW_SNAPSHOTS;
        }
        return ValidatorStatus.OK;
    }
}
