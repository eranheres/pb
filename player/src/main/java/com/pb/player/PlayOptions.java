package com.pb.player;

import com.pb.dao.Hand;
import com.pb.dao.Snapshot;
import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.List;

/**
 * Player options
 */
@AllArgsConstructor
public class PlayOptions {
    private final static String OH_AMOUNT_TO_CALL = "DollarsToCall";
    private final static String OH_BALANCE        = "balance";
    private final static String CHIP_LEADER_STACK = "MaxOpponentStackSizeCalculation";
    private final static String BIG_BLIND         = "bblind";

    public List<GameOp> getValidOps(Hand hand) {
        Snapshot snapshot = hand.latestSnapshot();
        Double amountToCall = snapshot.getSymbols().get(OH_AMOUNT_TO_CALL);
        Double balance = snapshot.getSymbols().get(OH_BALANCE);
        // Options when zero amount to call
        if (amountToCall == 0) {
            return Arrays.asList(GameOp.OP_ALLIN, GameOp.OP_CHECK, GameOp.OP_RAISE);
        }
        // Option when amount to call is lower than our stack
        if ((amountToCall > 0) && (balance > amountToCall)) {
            return Arrays.asList(GameOp.OP_ALLIN, GameOp.OP_CALL, GameOp.OP_RAISE, GameOp.OP_FOLD);
        }
        // Option when amount to call is higher or equal than our stack
        if ((amountToCall > 0) && (balance <= amountToCall)) {
            return Arrays.asList(GameOp.OP_ALLIN, GameOp.OP_FOLD);
        }
        throw new IllegalStateException("Can't decide on valid options to play");
    }

    public Integer minRaiseVal(Hand hand) {
        if (!getValidOps(hand).contains(GameOp.OP_RAISE))
            throw new IllegalStateException("Can't evaluate raise value when raise is not an option");

        Integer amountToCall = hand.latestSnapshot().getSymbols().get(OH_AMOUNT_TO_CALL).intValue();
        Integer bigBlind = hand.latestSnapshot().getSymbols().get(BIG_BLIND).intValue();
        if (amountToCall == 0)
            return bigBlind;
        return amountToCall;
    }

    public Integer maxRaiseVal(Hand hand) {
        if (!getValidOps(hand).contains(GameOp.OP_RAISE))
            throw new IllegalStateException("Can't evaluate raise value when raise is not an option");
        Integer chipLeaderStack = hand.latestSnapshot().getSymbols().get(CHIP_LEADER_STACK).intValue();
        Integer balance = hand.latestSnapshot().getSymbols().get(OH_BALANCE).intValue();
        Integer bigBlind = hand.latestSnapshot().getSymbols().get(BIG_BLIND).intValue();
        if (chipLeaderStack < balance)
            return chipLeaderStack;
        return balance-bigBlind;
    }
}
