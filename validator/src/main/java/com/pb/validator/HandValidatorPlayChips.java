package com.pb.validator;

import com.pb.dao.GameOp;
import com.pb.dao.Hand;
import com.pb.dao.PBDataSource;
import com.pb.dao.Snapshot;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Validates that the action ordered by the server is actually taken by the client (chips)
 */
@SuppressWarnings("Duplicates")
@AllArgsConstructor
@Service
public class HandValidatorPlayChips implements HandValidator {

    public static ValidatorStatus ACTION_NOT_RECORDED = new ValidatorStatus("Action not recorded");
    public static ValidatorStatus ACTION_CHECK_FOLD_OP_AMOUNT = new ValidatorStatus("Action check/fold while op amount is > 0");
    public static ValidatorStatus ACTION_CHECK_FOLD_BALANCE_CHANGED = new ValidatorStatus("Action check/fold while balance changed");
    public static ValidatorStatus BALANCE_WRONG_AFTER_PLAY = new ValidatorStatus("Balance is wrong after play");

    @Autowired
    PBDataSource dataSource;

    @Override
    public ValidatorStatus validate(Hand hand) {
        Boolean afterMyTurn = false;
        Integer turnCount = 0;
        Snapshot previousSnapshot = null;
        for (Snapshot snapshot : hand.getSnapshots()) {
            if (afterMyTurn) {
                ValidatorStatus status = getValidatorStatus(turnCount, previousSnapshot, snapshot);
                if (status != ValidatorStatus.OK) {
                    return status;
                }
                turnCount++;
            }

            // loops until after my turn
            String dataType = snapshot.getState().getDatatype();
            afterMyTurn = (dataType.equals(Snapshot.VALUES.DATATYPE_MYTURN) || dataType.equals(Snapshot.VALUES.DATATYPE_SHOWDOWN));
            previousSnapshot = snapshot;
        }
        return ValidatorStatus.OK;
    }

    private ValidatorStatus getValidatorStatus(Integer turnCount, Snapshot previous, Snapshot current) {
        String uuid = current.getState().getUuid();
        GameOp op = dataSource.getGameOp(uuid, turnCount);
        if (op == null)
            return ACTION_NOT_RECORDED.args("uuid", uuid, "turnCount", turnCount);
        double prevAction = current.getSymbols().get(Snapshot.SYMBOLS.PREVACTION);
        double prevBalance = previous.getSymbols().get(Snapshot.SYMBOLS.BALANCE);
        double currentBalance = current.getSymbols().get(Snapshot.SYMBOLS.BALANCE);
        if ((prevAction == Snapshot.VALUES.PREVACTION_CHECK) || (prevAction == Snapshot.VALUES.PREVACTION_FOLD)) {
            if (op.getAmount() != 0) {
                return ACTION_CHECK_FOLD_OP_AMOUNT.args("prevaction", prevAction, "op", op);
            }
            if (prevBalance != currentBalance) {
                return ACTION_CHECK_FOLD_BALANCE_CHANGED
                        .args("prevaction", prevAction, "prevbalance", prevBalance, "currentBalance", currentBalance, "op", op);
            }
            return ValidatorStatus.OK;
        }
        if (prevBalance - op.getAmount() != currentBalance) {
            return BALANCE_WRONG_AFTER_PLAY
                    .args("prevaction", prevAction, "prevbalance", prevBalance, "currentBalance", currentBalance, "op", op);
        }
        return ValidatorStatus.OK;
    }
}
