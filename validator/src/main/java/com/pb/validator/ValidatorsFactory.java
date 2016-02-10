package com.pb.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Repository of all hand and snapshot validators
 */
@Repository
public class ValidatorsFactory {
    @Autowired HandValidator handValidatorTurnSeq;
    @Autowired HandValidator handValidatorCards;
    @Autowired HandValidator handValidatorFullHand;
    @Autowired HandValidator handValidatorPlayAction;
    @Autowired HandValidator handValidatorPlayChips;
    @Autowired SnapshotValidator snapshotValidatorValidValues;
    @Autowired SnapshotValidator snapshotValidatorValidCards;
    @Autowired SnapshotValidator snapshotValidatorOpenPPL;


    public HandValidator[] getHandInProgressValidators() {
        return new HandValidator[]{
                handValidatorTurnSeq,
                handValidatorCards,
                handValidatorPlayAction,
                handValidatorPlayChips
        };
    }

    public HandValidator[] getHandFullValidators() {
        return new HandValidator[]{
                handValidatorTurnSeq,
                handValidatorPlayChips,
                handValidatorPlayAction,
                handValidatorFullHand
        };
    }

    public SnapshotValidator[] getSnapshotValidators() {
        return new SnapshotValidator[] {
                snapshotValidatorValidValues,
                snapshotValidatorValidCards,
                snapshotValidatorOpenPPL
        };
    }

}
