package com.pb.validator;

import com.pb.dao.Hand;
import com.pb.dao.Snapshot;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

/**
 *
 */
@Component
public class ValidatorsManager {

    private HandValidator handValidators[];
    private SnapshotValidator snapshotValidators[];

    @Autowired
    public ValidatorsManager(ValidatorsFactory validatorsFactory) {
        handValidators = validatorsFactory.getHandValidators();
        snapshotValidators = validatorsFactory.getSnapshotValidators();
    }

    public ValidatorStatus validateSnapshot(Snapshot snapshot) {
        ValidatorStatus status;
        if (snapshot == null)
            return SnapshotValidator.SNAPSHOT_EMPTY;
        for (SnapshotValidator validator : snapshotValidators) {
            status = validator.validate(snapshot);
            if (!status.equals(ValidatorStatus.OK)) {
                return status;
            }
        }
        return ValidatorStatus.OK;
    }

    @NonNull
    public ValidatorStatus validateHand(@NotNull Hand hand) {
        ValidatorStatus status;
        if (hand == null || hand.getSnapshots() == null)
            return HandValidator.EMPTY_HAND;
        for (Snapshot snapshot : hand.getSnapshots()) {
            status = validateSnapshot(snapshot);
            if (!status.equals(ValidatorStatus.OK)) {
                return status;
            }
        }

        for (HandValidator handValidator : handValidators) {
            status = handValidator.validate(hand);
            if (!status.equals(ValidatorStatus.OK)) {
                return status;
            }

        }

        return ValidatorStatus.OK;
    }
}
