package com.pb.validator;

/**
 * Validates a specific snapshot correctness
 */
public interface SnapshotValidator {
    boolean isValid();
    String reason();
}
