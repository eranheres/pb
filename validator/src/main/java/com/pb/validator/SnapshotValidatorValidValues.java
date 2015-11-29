package com.pb.validator;

import com.pb.validator.dao.Snapshot;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 *  Validates trivial values legality in a single snapshot
 */
@Component
public class SnapshotValidatorValidValues extends SnapshotValidator {

    public final static ValidatorStatus INVALID_FIELD_DATATYPE = new ValidatorStatus("Datatype field is invalid");
    public final static ValidatorStatus INVALID_FIELD_ACTION   = new ValidatorStatus("Action field is invalid");

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

    public SnapshotValidatorValidValues(Snapshot snapshot) {
        super(snapshot);
    }
    @Override
    public ValidatorStatus isValid() {
        // Check that datatype value is valid
        if (!VALID_DATATYPE_VALUES.contains(snapshot.getState().getDatatype())) {
            return INVALID_FIELD_DATATYPE;
        }
        // Check that if myturn then action is valid
        if ((snapshot.getState().getDatatype().equals(Snapshot.VALUES.MYTURN) &&
            (!VALID_ACTION_VALUES.contains(snapshot.getState().getBetround())))) {
            return INVALID_FIELD_ACTION;
        }
        return ValidatorStatus.OK;
    }
}
