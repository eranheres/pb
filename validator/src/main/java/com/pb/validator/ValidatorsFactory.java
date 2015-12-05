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
    @Autowired SnapshotValidator snapshotValidatorValidValues;

    public HandValidator[] getHandValidators() {
        return new HandValidator[]{ handValidatorTurnSeq };
    }

    public SnapshotValidator[] getSnapshotValidators() {
        return new SnapshotValidator[] { snapshotValidatorValidValues };
    }

}
