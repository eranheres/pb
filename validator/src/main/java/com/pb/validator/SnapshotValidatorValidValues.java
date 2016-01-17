package com.pb.validator;

import com.pb.dao.Snapshot;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 *  Validates trivial values legality in a single snapshot
 */
@Component
@NoArgsConstructor
public class SnapshotValidatorValidValues implements SnapshotValidator {

    public final static ValidatorStatus INVALID_FIELD_DATATYPE = new ValidatorStatus("Datatype field is invalid");
    public final static ValidatorStatus INVALID_FIELD_ACTION   = new ValidatorStatus("Action field is invalid");
    public final static ValidatorStatus NULL_OR_NEGATIVE_SYMBOL = new ValidatorStatus("Mandatory symbol is null or negative");

    private static final List<String> VALID_DATATYPE_VALUES = Arrays.asList(
            Snapshot.VALUES.HANDRESET,
            Snapshot.VALUES.HEARTBEAT,
            Snapshot.VALUES.SHOWDOWN,
            Snapshot.VALUES.MYTURN,
            Snapshot.VALUES.NEWROUND
    );

    private static final List<String> VALID_ACTION_VALUES = Arrays.asList(
            Snapshot.VALUES.FLOP,
            Snapshot.VALUES.PREFLOP,
            Snapshot.VALUES.TURN,
            Snapshot.VALUES.RIVER
    );

    private static final List<String> MANDATORY_POSITIVE_VAL = Arrays.asList(
            Snapshot.VALUES.SYMBOL_AMOUNT_TO_CALL,
            Snapshot.VALUES.SYMBOL_BALANCE,
            Snapshot.VALUES.SYMBOL_BIG_BLIND,
            Snapshot.VALUES.SYMBOL_OPPONENTS_WITH_HIGHER_STACK
    );

    @Override
    public ValidatorStatus validate(Snapshot snapshot) {
        if (snapshot.getState() == null) {
            return NULL_OR_NEGATIVE_SYMBOL;
        }
        // Check that datatype value is valid
        if (!VALID_DATATYPE_VALUES.contains(snapshot.getState().getDatatype())) {
            return INVALID_FIELD_DATATYPE;
        }
        // Check that if myturn then action is valid
        if ((snapshot.getState().getDatatype().equals(Snapshot.VALUES.MYTURN) &&
            (!VALID_ACTION_VALUES.contains(snapshot.getState().getBetround())))) {
            return INVALID_FIELD_ACTION;
        }
        // Check that all the symbols are not null and positive
        for (String symbol : MANDATORY_POSITIVE_VAL) {
            if (snapshot.getSymbols() == null)
                return NULL_OR_NEGATIVE_SYMBOL;
            Double val =  (snapshot.getSymbols().get(symbol));
            if ((val == null) || (val < 0)) {
                return NULL_OR_NEGATIVE_SYMBOL;
            }
        }
        return ValidatorStatus.OK;
    }


}
