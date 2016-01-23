package com.pb.validator;

import com.pb.dao.GameOp;
import com.pb.dao.Hand;
import com.pb.dao.PBDataSource;
import com.pb.dao.Snapshot;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Validates that the action ordered by the server is actually taken by the client
 */
@AllArgsConstructor
@NoArgsConstructor
@Service
public class HandValidatorPlayAction implements HandValidator {

    public static ValidatorStatus WRONG_ACTION_TAKEN        = new ValidatorStatus("Wrong action taken");
    public static ValidatorStatus PLAY_ACTION_NOT_SUPPORTED = new ValidatorStatus("Play action not supported");
    public static ValidatorStatus UNINSTRUCTED_PLAY_ACTION  = new ValidatorStatus("Uninstructed play action");
    public static ValidatorStatus ALLIN_NOT_IN_PLACE        = new ValidatorStatus("Allin not in place");
    public static ValidatorStatus FOLD_NOT_IN_PLACE         = new ValidatorStatus("Fold not in place");
    public static ValidatorStatus ACTION_NOT_RECORDED       = new ValidatorStatus("Action not recorded");
    public static ValidatorStatus TURN_COUNT_OUT_OF_ORDER   = new ValidatorStatus("Turn count out of order");
    public static ValidatorStatus RECORDED_PLAY_WASNT_PLAYED = new ValidatorStatus("Recorded play wasn't played");

    @Autowired
    PBDataSource dataSource;

    @Override
    public ValidatorStatus validate(Hand hand) {
        Boolean afterMyTurn = false;
        Integer turnCount = -1;
        Snapshot previousSnapshot = null;
        for (Snapshot snapshot : hand.getSnapshots()) {
            Double prevaction = snapshot.getSymbols().get(Snapshot.SYMBOLS.PREVACTION);
            String datatype   = snapshot.getState().getDatatype();
            Integer dataTurnCount = snapshot.getState().getMy_turn_count();
            // Myturn with allin or with fold action is not valid
            if (datatype.equals(Snapshot.VALUES.DATATYPE_MYTURN)) {
                turnCount++;
                if (prevaction == Snapshot.VALUES.PREVACTION_ALLIN)
                    return ALLIN_NOT_IN_PLACE;
                if (prevaction == Snapshot.VALUES.PREVACTION_FOLD)
                    return FOLD_NOT_IN_PLACE;
                // Must not see prevaction on the first myturn
                if ((turnCount == 0) && (prevaction != Snapshot.VALUES.PREVACTION_PREFOLD))
                    return UNINSTRUCTED_PLAY_ACTION;
            }
            if (!dataTurnCount.equals(turnCount))
                return TURN_COUNT_OUT_OF_ORDER.args("dataTurnCount", dataTurnCount, "calc", turnCount);
            if (afterMyTurn) {
                // check the snapshot immediately after my turn and see if it is executed correctly
                Integer dataPrevTurnCount = previousSnapshot.getState().getMy_turn_count();
                ValidatorStatus status = validatePlayActionHistory(dataPrevTurnCount, snapshot);
                if (status != ValidatorStatus.OK) {
                    return status;
                }
            }

            // loops until after my turn
            String dataType = snapshot.getState().getDatatype();
            afterMyTurn = (dataType.equals(Snapshot.VALUES.DATATYPE_MYTURN) || dataType.equals(Snapshot.VALUES.DATATYPE_SHOWDOWN));
            previousSnapshot = snapshot;
        }
        // check that all recorded ops have been verified
        if (dataSource.getGameOp(previousSnapshot.getState().getUuid(), turnCount+1) != null)
            return RECORDED_PLAY_WASNT_PLAYED.args("turnCount", turnCount+1);
        return ValidatorStatus.OK;
    }

    private ValidatorStatus validatePlayActionHistory(Integer turnCount, Snapshot current) {
        double prevAction = current.getSymbols().get(Snapshot.SYMBOLS.PREVACTION);
        String uuid = current.getState().getUuid();
        GameOp op = dataSource.getGameOp(uuid, turnCount);
        if (op == null)
            return ACTION_NOT_RECORDED.args("turnCount", turnCount, "prevaction", prevAction);
        if (prevAction == Snapshot.VALUES.PREVACTION_PREFOLD)
            return RECORDED_PLAY_WASNT_PLAYED.args("expected", op);
        if ((prevAction == Snapshot.VALUES.PREVACTION_ALLIN) && (!op.getOp().equals(GameOp.OP_ALLIN().getOp())))
            return WRONG_ACTION_TAKEN.args("expected", "Allin", "actual", op, "turn", turnCount);
        if ((prevAction == Snapshot.VALUES.PREVACTION_CALL) && (!op.getOp().equals(GameOp.OP_CALL().getOp())))
            return WRONG_ACTION_TAKEN.args("expected", "Call", "actual", op, "turn", turnCount);
        if ((prevAction == Snapshot.VALUES.PREVACTION_CHECK) && (!op.getOp().equals(GameOp.OP_CHECK().getOp())))
            return WRONG_ACTION_TAKEN.args("expected", "Check", "actual", op, "turn", turnCount);
        if ((prevAction == Snapshot.VALUES.PREVACTION_FOLD) && (!op.getOp().equals(GameOp.OP_FOLD().getOp())))
            return WRONG_ACTION_TAKEN.args("expected", "Fold", "actual", op, "turn", turnCount);
        if ((prevAction == Snapshot.VALUES.PREVACTION_RAISE) && (!op.getOp().equals(GameOp.OP_RAISE().getOp())))
            return WRONG_ACTION_TAKEN.args("expected", "Raise", "actual", op, "turn", turnCount);

        return ValidatorStatus.OK;
    }
}
