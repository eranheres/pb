package com.pb.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Created by eranh on 12/2/15.
 */
@Repository
public class ValidatorsFactory {
    @Autowired HandValidator handValidatorTurnSeq;
    @Autowired SnapshotValidator snapshotValidatorValidValues;

    public HandValidator[] handValidators() {
        return new HandValidator[]{ handValidatorTurnSeq };
    }

    public SnapshotValidator[] snapshotValidators() {
        return new SnapshotValidator[] {snapshotValidatorValidValues };
    }

}
