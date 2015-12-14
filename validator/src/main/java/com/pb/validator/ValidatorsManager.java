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

    private HandValidator handInProgressValidators[];
    private HandValidator handFullValidators[];
    private SnapshotValidator snapshotValidators[];

    @Autowired
    public ValidatorsManager(ValidatorsFactory validatorsFactory) {
        handInProgressValidators = validatorsFactory.getHandInProgressValidators();
        snapshotValidators = validatorsFactory.getSnapshotValidators();
        handFullValidators = validatorsFactory.getHandFullValidators();
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
    public ValidatorStatus validateHandInProgress(@NotNull Hand hand) {
        ValidatorStatus status;
        if (hand == null || hand.getSnapshots() == null)
            return HandValidator.EMPTY_HAND;
        for (Snapshot snapshot : hand.getSnapshots()) {
            status = validateSnapshot(snapshot);
            if (!status.equals(ValidatorStatus.OK)) {
                return status;
            }
        }

        for (HandValidator handValidator : handInProgressValidators) {
            status = handValidator.validate(hand);
            if (!status.equals(ValidatorStatus.OK)) {
                return status;
            }

        }

        return ValidatorStatus.OK;
    }

    @NonNull
    public ValidatorStatus validateHandFullHand(@NonNull Hand hand) {
        ValidatorStatus status;
        if (hand == null || hand.getSnapshots() == null)
            return HandValidator.EMPTY_HAND;
        for (Snapshot snapshot : hand.getSnapshots()) {
            status = validateSnapshot(snapshot);
            if (!status.equals(ValidatorStatus.OK)) {
                return status;
            }
        }

        for (HandValidator handValidator : handFullValidators) {
            status = handValidator.validate(hand);
            if (!status.equals(ValidatorStatus.OK)) {
                return status;
            }

        }

        return ValidatorStatus.OK;

    }
}
