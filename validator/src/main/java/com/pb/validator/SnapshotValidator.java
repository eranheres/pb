package com.pb.validator;

import com.pb.validator.dao.Snapshot;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Validates a specific snapshot correctness
 */
@Getter
@Setter
@AllArgsConstructor
public abstract class SnapshotValidator {
    protected Snapshot snapshot;
    public abstract ValidatorStatus isValid();
}
