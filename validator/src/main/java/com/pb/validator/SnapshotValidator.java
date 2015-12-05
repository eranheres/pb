package com.pb.validator;

import com.pb.dao.Snapshot;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Validates a specific snapshot correctness
 */
public interface SnapshotValidator {
    ValidatorStatus SNAPSHOT_EMPTY = new ValidatorStatus("Snapshot is empty or null");

    ValidatorStatus validate(Snapshot snapshot);
}
