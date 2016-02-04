package com.pb.player;

import com.pb.dao.GameOp;
import com.pb.dao.Hand;
import com.pb.dao.Snapshot;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Player options
 */
@AllArgsConstructor
@Component
public class PlayOptions {

    public List<GameOp> getValidOps(Hand hand) {
        Snapshot snapshot = hand.latestSnapshot();
        Double amountToCall = snapshot.getSymbols().get(Snapshot.SYMBOLS.AMOUNT_TO_CALL);
        Double balance = snapshot.getSymbols().get(Snapshot.SYMBOLS.BALANCE);
        Double bblind = snapshot.getSymbols().get(Snapshot.SYMBOLS.BIG_BLIND);
        Integer balanceInBB = Double.valueOf(Math.ceil(balance/bblind)).intValue();
        Integer amountToCallInBB = Double.valueOf(Math.ceil(amountToCall/bblind)).intValue();
        // Options when zero amount to call
        if (amountToCall == 0) {
            return Arrays.asList(GameOp.OP_ALLIN().amountInBB(balanceInBB), GameOp.OP_CHECK(), GameOp.OP_RAISE());
        }
        // Option when amount to call is lower than our stack
        if ((amountToCall > 0) && (balance > amountToCall)) {
            return Arrays.asList(GameOp.OP_ALLIN().amountInBB(balanceInBB), GameOp.OP_CALL().amountInBB(amountToCallInBB), GameOp.OP_RAISE(), GameOp.OP_FOLD());
        }
        // Option when amount to call is higher or equal than our stack
        if ((amountToCall > 0) && (balance <= amountToCall)) {
            return Arrays.asList(GameOp.OP_ALLIN().amountInBB(balanceInBB), GameOp.OP_FOLD());
        }
        throw new IllegalStateException("Can't decide on valid options to play");
    }

    public Integer minRaiseVal(Hand hand) {
        if (!getValidOps(hand).contains(GameOp.OP_RAISE()))
            throw new IllegalStateException("Can't evaluate raise value when raise is not an option");

        Map<String, Double> symbols = hand.latestSnapshot().getSymbols();
        Integer amountToCall = symbols.get(Snapshot.SYMBOLS.AMOUNT_TO_CALL).intValue();
        Integer bigBlind = symbols.get(Snapshot.SYMBOLS.BIG_BLIND).intValue();
        if (amountToCall == 0)
            return bigBlind;
        return amountToCall;
    }

    public Integer maxRaiseVal(Hand hand) {
        if (!getValidOps(hand).contains(GameOp.OP_RAISE()))
            throw new IllegalStateException("Can't evaluate raise value when raise is not an option");
        Map<String, Double> symbols = hand.latestSnapshot().getSymbols();
        Integer balance = symbols.get(Snapshot.SYMBOLS.BALANCE).intValue();
        Integer bigBlind = symbols.get(Snapshot.SYMBOLS.BIG_BLIND).intValue();
        return balance-bigBlind;
    }
}
