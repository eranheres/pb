package com.pb.validator;

import com.pb.validator.dao.Hand;
import com.pb.validator.dao.Snapshot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 *
 */
@Repository
public class ValidatorsRepo {

    @Autowired HandValidator handValidatorTurnSeq;
    @Autowired SnapshotValidator snapshotValidatorValidValues;

    public ValidatorStatus validateHand(Hand hand) {
        ValidatorStatus status;
        for (Snapshot snapshot : hand.getSnapshots()) {
            snapshotValidatorValidValues.setSnapshot(snapshot);
            status = snapshotValidatorValidValues.isValid();
            if (!status.equals(ValidatorStatus.OK))
                return status;
        }

        handValidatorTurnSeq.setHand(hand);
        return handValidatorTurnSeq.validate();
    }
}
