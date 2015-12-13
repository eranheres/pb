package com.pb.validator;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Repository of all hand and snapshot validators
 */
@Repository
public class ValidatorsFactory {
    @Autowired HandValidator handValidatorTurnSeq;
    @Autowired HandValidator handValidatorCards;
    @Autowired HandValidator handValidatorValuesStable;
    @Autowired SnapshotValidator snapshotValidatorValidValues;
    @Autowired SnapshotValidator snapshotValidatorValidCards;

    @Autowired HandValidator handValidatorFullHand;

    public HandValidator[] getHandInProgressValidators() {
        return new HandValidator[]{
                handValidatorTurnSeq,
                handValidatorCards,
                handValidatorValuesStable
        };
    }

    public HandValidator[] getHandFullValidators() {
        return new HandValidator[]{
                handValidatorTurnSeq,
                handValidatorFullHand
        };
    }

    public SnapshotValidator[] getSnapshotValidators() {
        return new SnapshotValidator[] {
                snapshotValidatorValidValues,
                snapshotValidatorValidCards
        };
    }

}
