package com.pb.validator;

import com.google.common.collect.ImmutableMap;
import com.pb.validator.dao.Hand;
import com.pb.validator.dao.Snapshot;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Validate that the sequence of the hand is correct. i.e. reset->preflop->flop->turn->river->showdown
 */
@RequiredArgsConstructor
@Component
public class HandValidatorTurnSeq extends HandValidator {
    @NonNull private Hand hand;

    public final static ValidatorStatus TOO_FEW_SNAPSHOTS       = new ValidatorStatus("dd");
    public final static ValidatorStatus NO_HANDRESET_ON_FIRST   = new ValidatorStatus("Handreset was not found on first snapshot");
    public final static ValidatorStatus HANDRESET_MUST_BE_FIRST = new ValidatorStatus("Handreset not in position 0");
    public final static ValidatorStatus INVALID_VAL_BETROUND    = new ValidatorStatus("invalid value in betround");
    public final static ValidatorStatus BETROUND_OUT_OF_ORDER   = new ValidatorStatus("Betround out of order");
    String str;
    private final static ImmutableMap<String, Integer> betRoundOrder = ImmutableMap.of(
            Snapshot.VALUES.PREFLOP,   1,
            Snapshot.VALUES.FLOP,      2,
            Snapshot.VALUES.TURN,      3,
            Snapshot.VALUES.RIVER,     4);

    @Override
    public ValidatorStatus validate() {
        Snapshot[] snapshots = hand.getSnapshots();
        Integer highestBetRound = 0;
        // Must be at least reset->preflop
        if (snapshots.length < 2) {
            return TOO_FEW_SNAPSHOTS;
        }
        for (int i=0; i<snapshots.length; i++) {
            Snapshot snapshot = snapshots[i];
            // Must have reset on hand start
            if ((0 == i) && (!snapshot.getState().getDatatype().equals(Snapshot.VALUES.HANDRESET))) {
                return NO_HANDRESET_ON_FIRST;
            }
            // Must not have handreset in other position than 0
            if ((i!=0) && (snapshot.getState().getDatatype().equals(Snapshot.VALUES.HANDRESET))) {
                return HANDRESET_MUST_BE_FIRST;
            }
            // Check that the order of betround is ok
            Integer current = betRoundOrder.get(snapshot.getState().getBetround());
            if (current == null) {
                return INVALID_VAL_BETROUND;
            }
            if (current < highestBetRound) {
                return BETROUND_OUT_OF_ORDER;
            }
            highestBetRound = current;
        }
        return ValidatorStatus.OK;
    }
}
