package com.pb.validator;

import com.pb.dao.Snapshot;

/**
 * Validates that there are no errors in open PPL symbols
 */
public class SnapshotValidatorOpenPPL implements SnapshotValidator {
    public final static ValidatorStatus OPEN_PPL_ERROR = new ValidatorStatus("Open PPL Error");

    @Override
    public ValidatorStatus validate(Snapshot snapshot) {
        for (String key : snapshot.getPpl_symbols().keySet()) {
            if ((key.toUpperCase().startsWith("ERROR_")) &&
                (snapshot.getPpl_symbols().get(key) != 0)) {
                    return OPEN_PPL_ERROR;
            }
        }
        return ValidatorStatus.OK;
    }
}
