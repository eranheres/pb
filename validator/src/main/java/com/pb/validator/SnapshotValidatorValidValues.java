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

    public final static ValidatorStatus INVALID_FIELD_DATATYPE   = new ValidatorStatus("Datatype field is invalid");
    public final static ValidatorStatus INVALID_FIELD_ACTION     = new ValidatorStatus("Action field is invalid");
    public final static ValidatorStatus NULL_OR_NEGATIVE_SYMBOL  = new ValidatorStatus("Mandatory symbol is negative");
    public final static ValidatorStatus SYMBOLS_NOT_FOUND        = new ValidatorStatus("Symbols not found");
    public final static ValidatorStatus MISSING_MANDATORY_VALUE  = new ValidatorStatus("Mandatory symbol is missing");
    public final static ValidatorStatus VALUE_OUT_OF_BOUNDS      = new ValidatorStatus("Value out of bounds");

    private static final List<String> VALID_DATATYPE_VALUES = Arrays.asList(
            Snapshot.VALUES.DATATYPE_HANDRESET,
            Snapshot.VALUES.DATATYPE_HEARTBEAT,
            Snapshot.VALUES.DATATYPE_SHOWDOWN,
            Snapshot.VALUES.DATATYPE_MYTURN,
            Snapshot.VALUES.DATATYPE_POSTHAND,
            Snapshot.VALUES.DATATYPE_NEWROUND
    );

    private static final List<String> VALID_ACTION_VALUES = Arrays.asList(
            Snapshot.VALUES.FLOP,
            Snapshot.VALUES.PREFLOP,
            Snapshot.VALUES.TURN,
            Snapshot.VALUES.RIVER
    );

    private static final List<String> MANDATORY_POSITIVE_VAL = Arrays.asList(
            Snapshot.SYMBOLS.AMOUNT_TO_CALL,
            Snapshot.SYMBOLS.BALANCE,
            Snapshot.SYMBOLS.BIG_BLIND,
            Snapshot.SYMBOLS.OPPONENTS_WITH_HIGHER_STACK
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
        if ((snapshot.getState().getDatatype().equals(Snapshot.VALUES.DATATYPE_MYTURN) &&
            (!VALID_ACTION_VALUES.contains(snapshot.getState().getBetround())))) {
            return INVALID_FIELD_ACTION;
        }
        // Check that all the symbols are not null and positive
        for (String symbol : MANDATORY_POSITIVE_VAL) {
            if (snapshot.getSymbols() == null)
                return SYMBOLS_NOT_FOUND;
            Double val =  (snapshot.getSymbols().get(symbol));
            if ((val == null) || (val < 0)) {
                return NULL_OR_NEGATIVE_SYMBOL.args(symbol, val==null?"null":val);
            }
        }
        // Check that prevaction is within boundaries
        Double prevAction = snapshot.getSymbols().get(Snapshot.SYMBOLS.PREVACTION);
        if (prevAction == null)
            return MISSING_MANDATORY_VALUE.args("prevaction", "null");
        if (!Arrays.asList(new Double[]{
                Snapshot.VALUES.PREVACTION_PREFOLD,
                Snapshot.VALUES.PREVACTION_FOLD,
                Snapshot.VALUES.PREVACTION_CHECK,
                Snapshot.VALUES.PREVACTION_CALL,
                /* Snapshot.VALUES.PREVACTION_RAISE, */
                Snapshot.VALUES.PREVACTION_BETRAISE,
                Snapshot.VALUES.PREVACTION_ALLIN
            }).contains(prevAction))
            return VALUE_OUT_OF_BOUNDS.args("prevaction", prevAction);
        return ValidatorStatus.OK;
    }


}
