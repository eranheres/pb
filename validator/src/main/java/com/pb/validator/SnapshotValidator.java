package com.pb.validator;

import com.pb.dao.Snapshot;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Validates a specific snapshot correctness
 */
@Getter
@Setter
@NoArgsConstructor
public abstract class SnapshotValidator {
    public abstract ValidatorStatus isValid(Snapshot snapshot);
}
