package com.pb.validator;

import com.pb.validator.model.Snapshot;

import java.util.Arrays;
import java.util.List;

/**
 * Created by eranh on 11/22/15.
 */
@lombok.AllArgsConstructor
public class SnapshotValidatorValidValues implements SnapshotValidator {
    private Snapshot snapshot;

    private static final List<String> VALID_DATATYPE_VALUES = Arrays.asList(
            Snapshot.VALUES.HANDRESET,
            Snapshot.VALUES.HEARTBEAT,
            Snapshot.VALUES.SHOWDOWN,
            Snapshot.VALUES.MYTURN
    );

    private static final List<String> VALID_ACTION_VALUES = Arrays.asList(
            Snapshot.VALUES.FLOP,
            Snapshot.VALUES.PREFLOP,
            Snapshot.VALUES.TURN,
            Snapshot.VALUES.RIVER
    );

    @Override
    public boolean isValid() {
        // Check that datatype value is valid
        if (!VALID_DATATYPE_VALUES.contains(snapshot.getState().getDatatype())) {
            return false;
        }
        // Check that if myturn then action is valid
        if ((snapshot.getState().getDatatype().equals(Snapshot.VALUES.MYTURN) &&
            (!VALID_ACTION_VALUES.contains(snapshot.getState().getBetround())))) {
            return false;
        }
        return true;
    }

    @Override
    public String reason() {
        return null;
    }
}
