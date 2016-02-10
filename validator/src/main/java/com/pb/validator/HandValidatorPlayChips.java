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
 * Validates that the action ordered by the server is actually taken by the client (chips)
 */
@SuppressWarnings("Duplicates")
@AllArgsConstructor
@NoArgsConstructor
@Service
public class HandValidatorPlayChips implements HandValidator {

    public static ValidatorStatus ACTION_NOT_RECORDED = new ValidatorStatus("Action not recorded while checking chips");
    public static ValidatorStatus ACTION_CHECK_FOLD_OP_AMOUNT = new ValidatorStatus("Action check/fold while op amount is > 0");
    public static ValidatorStatus ACTION_CHECK_FOLD_BALANCE_CHANGED = new ValidatorStatus("Action check/fold while balance changed");
    public static ValidatorStatus BALANCE_WRONG_AFTER_PLAY = new ValidatorStatus("Balance is wrong after play");
    public static ValidatorStatus UNKNOWN_ACTION = new ValidatorStatus("Unknown action");

    @Autowired
    PBDataSource dataSource;

    @Override
    public ValidatorStatus validate(Hand hand) {
        Snapshot previousSnapshot = null;
        for (Snapshot snapshot : hand.getSnapshots()) {
            String dataType = snapshot.getState().getDatatype();
            if (!dataType.equals(Snapshot.VALUES.DATATYPE_MYTURN) &&
                !dataType.equals(Snapshot.VALUES.DATATYPE_POSTHAND))
                continue;
            if (previousSnapshot != null) {
                ValidatorStatus status = getValidatorStatus(previousSnapshot, snapshot);
                if (status != ValidatorStatus.OK) {
                    return status;
                }
            }
            previousSnapshot = snapshot;
        }
        return ValidatorStatus.OK;
    }

    private ValidatorStatus getValidatorStatus(Snapshot previous, Snapshot current) {
        String uuid = current.getState().getUuid();

        Integer turnCount = previous.getState().getMy_turn_count();
        GameOp op = dataSource.getGameOp(uuid, turnCount);
        if (op == null)
            return ACTION_NOT_RECORDED.args("turnCount", turnCount);
        double prevAction = current.getSymbols().get(Snapshot.SYMBOLS.PREVACTION);
        if (prevAction == Snapshot.VALUES.PREVACTION_CHECK)
            return validatePrevCheck(turnCount, op, current, previous);
        if (prevAction == Snapshot.VALUES.PREVACTION_FOLD)
            return validatePrevFold(turnCount, op, current, previous);
        if (prevAction == Snapshot.VALUES.PREVACTION_ALLIN)
            return validatePrevAllin(turnCount, op, current, previous);
        if (prevAction == Snapshot.VALUES.PREVACTION_CALL)
            return validatePrevCall(turnCount, op, current, previous);
        if (prevAction == Snapshot.VALUES.PREVACTION_BETRAISE)
            return validatePrevBetraise(turnCount, op, current, previous);
        if ((prevAction == Snapshot.VALUES.PREVACTION_PREFOLD) &&
            (current.getState().getDatatype().equals(Snapshot.VALUES.DATATYPE_POSTHAND)))
            return ValidatorStatus.OK;
        return UNKNOWN_ACTION.args("prevaction", prevAction);
    }

    private ValidatorStatus validatePrevCheck(Integer turnCount, GameOp op, Snapshot current, Snapshot previous) {
        if (op.getAmount() != 0) {
            return ACTION_CHECK_FOLD_OP_AMOUNT.args("turnCount", turnCount, "op", op);
        }
        double prevBalance = previous.getSymbols().get(Snapshot.SYMBOLS.BALANCE);
        double currentBalance = current.getSymbols().get(Snapshot.SYMBOLS.BALANCE);
        if (prevBalance != currentBalance) {
            return ACTION_CHECK_FOLD_BALANCE_CHANGED.args("turnCount", turnCount,
                    "prevbalance", prevBalance, "currentBalance", currentBalance, "op", op);
        }
        return ValidatorStatus.OK;
    }

    private ValidatorStatus validatePrevFold(Integer turnCount, GameOp op, Snapshot current, Snapshot previous) {
        if (op.getAmount() != 0) {
            return ACTION_CHECK_FOLD_OP_AMOUNT.args("turnCount", turnCount, "op", op);
        }
        double prevBalance = previous.getSymbols().get(Snapshot.SYMBOLS.BALANCE);
        double currentBalance = current.getSymbols().get(Snapshot.SYMBOLS.BALANCE);
        if (prevBalance != currentBalance) {
            return ACTION_CHECK_FOLD_BALANCE_CHANGED.args("turnCount", turnCount,
                    "prevbalance", prevBalance, "currentBalance", currentBalance, "op", op);
        }
        return ValidatorStatus.OK;
    }
    private ValidatorStatus validatePrevAllin(Integer turnCount, GameOp op, Snapshot current, Snapshot previous) {
        double prevBalance = previous.getSymbols().get(Snapshot.SYMBOLS.BALANCE);
        double currentBalance = current.getSymbols().get(Snapshot.SYMBOLS.BALANCE);
        // TODO - calculate remaining balance more correctly
        if (currentBalance >= prevBalance) {
            return BALANCE_WRONG_AFTER_PLAY
                    .args("prevaction", "allin",
                          "prevbalance", prevBalance,
                          "currentBalance", currentBalance,
                          "turn", turnCount,
                          "op", op);
        }
        return ValidatorStatus.OK;
    }

    private  ValidatorStatus validatePrevCall(Integer turnCount, GameOp op, Snapshot current, Snapshot previous) {
        if (current.getState().getDatatype().equals(Snapshot.VALUES.DATATYPE_POSTHAND))
            return ValidatorStatus.OK;
        double prevBalance = previous.getSymbols().get(Snapshot.SYMBOLS.BALANCE);
        double currentBalance = current.getSymbols().get(Snapshot.SYMBOLS.BALANCE);
        if (prevBalance - op.getAmount() != currentBalance) {
            return BALANCE_WRONG_AFTER_PLAY
                    .args("prevaction", "call",
                            "prevbalance", prevBalance,
                            "currentBalance", currentBalance,
                            "turn", turnCount,
                            "op", op);
        }
        return ValidatorStatus.OK;
    }

    private  ValidatorStatus validatePrevBetraise(Integer turnCount, GameOp op, Snapshot current, Snapshot previous) {
        if (current.getState().getDatatype().equals(Snapshot.VALUES.DATATYPE_POSTHAND))
            return ValidatorStatus.OK;
        double prevBalance = previous.getSymbols().get(Snapshot.SYMBOLS.BALANCE);
        double currentBalance = current.getSymbols().get(Snapshot.SYMBOLS.BALANCE);
        double amountToCall = previous.getSymbols().get(Snapshot.SYMBOLS.AMOUNT_TO_CALL);
        if (prevBalance - op.getAmount() - amountToCall != currentBalance) {
            return BALANCE_WRONG_AFTER_PLAY
                    .args("prevaction", "betraise",
                            "prevbalance", prevBalance,
                            "currentBalance", currentBalance,
                            "turn", turnCount,
                            "op", op);
        }
        return ValidatorStatus.OK;
    }
}


