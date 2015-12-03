package com.pb.validator;

import com.pb.dao.Hand;
import com.pb.dao.Snapshot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

/**
 *
 */
@Component
public class ValidatorsRepo {

    private HandValidator handValidators[];
    private SnapshotValidator snapshotValidators[];

    @Autowired
    public ValidatorsRepo(ValidatorsFactory validatorsFactory) {
        handValidators = validatorsFactory.handValidators();
        snapshotValidators = validatorsFactory.snapshotValidators();
    }

    public ValidatorStatus validateSnapshot(Snapshot snapshot) {
        ValidatorStatus status;
        for (SnapshotValidator validator : snapshotValidators) {
            status = validator.isValid(snapshot);
            if (!status.equals(ValidatorStatus.OK)) {
                return status;
            }
        }
        return ValidatorStatus.OK;
    }

    public ValidatorStatus validateHand(Hand hand) {
        ValidatorStatus status;
        for (Snapshot snapshot : hand.getSnapshots()) {
            status = validateSnapshot(snapshot);
            if (!status.equals(ValidatorStatus.OK)) {
                return status;
            }
        }

        for (HandValidator handValidator : handValidators) {
            status = validateHand(hand);
            if (!status.equals(ValidatorStatus.OK)) {
                return status;
            }

        }

        return ValidatorStatus.OK;
    }
}
