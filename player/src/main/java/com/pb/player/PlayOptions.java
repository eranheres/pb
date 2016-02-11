package com.pb.player;

import com.pb.dao.GameOp;
import com.pb.dao.Hand;
import com.pb.dao.Snapshot;
import com.pb.helpers.TableHelper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Player options
 */
@NoArgsConstructor
@Component
public class PlayOptions {

    public List<GameOp> getValidOps(Hand hand) {
        Snapshot snapshot = hand.latestSnapshot();
        Double amountToCall = snapshot.getSymbols().get(Snapshot.SYMBOLS.AMOUNT_TO_CALL);
        Double balance = snapshot.getSymbols().get(Snapshot.SYMBOLS.BALANCE);
        Double bblind = snapshot.getSymbols().get(Snapshot.SYMBOLS.BIG_BLIND);
        Boolean canCheck = true;
        Boolean canRaise = true;
        Boolean canCall  = true;
        Boolean canFold  = true;

        // Options when zero amount to call
        if (amountToCall == 0) {
            canFold = false;
            canCall = false;
        }
        if (amountToCall > 0) {
            canCheck = false;
        }
        if (amountToCall*2 > balance) {
            canRaise = false;
        }
        // Option when amount to call is higher or equal than our stack
        if ((amountToCall > 0) && (balance <= amountToCall)) {
            canCall  = false;
            canRaise = false;
            canCheck = false;
        }
        // When no bet on the table and our balance is lower than bblind, the only option is allin and check
        if ((amountToCall == 0) && (bblind >= balance)) {
            canRaise = false;
        }
        // When allin val is lower than amount to call or big blind then it is not a raise situation
        Double allInVal = allInVal(hand);
        Double amountToCallRoundedToBBlind = Math.ceil(amountToCall/bblind)*bblind;
        if ((amountToCallRoundedToBBlind >= allInVal) || (bblind >= allInVal)) {
            canRaise = false;
        }

        List<GameOp> list = new ArrayList<>();
        list.add(GameOp.OP_ALLIN().amount(balance));
        if (canCall) {
            list.add(GameOp.OP_CALL().amount(amountToCall));
        }
        if (canCheck) {
            list.add(GameOp.OP_CHECK());
        }
        if (canRaise) {
            list.add(GameOp.OP_RAISE());
        }
        if (canFold) {
            list.add(GameOp.OP_FOLD());
        }

        return list;
    }

    /*
    minimum raise value - this is the value to raise on top of previous raise/call if there is
     */
    public Double minRaiseVal(Hand hand) {
        if (!getValidOps(hand).contains(GameOp.OP_RAISE()))
            throw new IllegalStateException("Can't evaluate raise value when raise is not an option");

        Map<String, Double> symbols = hand.latestSnapshot().getSymbols();
        Double amountToCall = symbols.get(Snapshot.SYMBOLS.AMOUNT_TO_CALL);
        Double bigBlind = symbols.get(Snapshot.SYMBOLS.BIG_BLIND);
        Double maxRaise = maxRaiseVal(hand);
        Double minRaise;
        if (amountToCall == 0) {
            minRaise = bigBlind;
        } else {
            minRaise = amountToCall;
        }
        if (minRaise>maxRaise)
            minRaise = maxRaise;
        return minRaise;
    }

    public Double allInVal(Hand hand) {
        Snapshot snapshot = hand.latestSnapshot();
        Map<String, Double> symbols = snapshot.getSymbols();
        Double amountToCall = symbols.get(Snapshot.SYMBOLS.AMOUNT_TO_CALL);
        Double bigBlind = symbols.get(Snapshot.SYMBOLS.BIG_BLIND);
        Integer userChair = symbols.get(Snapshot.SYMBOLS.USERCHAIR).intValue();
        Double myBet = snapshot.getPlayers()[userChair].getCurrentbet();
        Double myBalance = symbols.get(Snapshot.SYMBOLS.BALANCE);
        Double balanceAfterCall = myBalance - amountToCall;
        // if balance after call is less than amount to call then this is not a raise situation
        if ((balanceAfterCall < amountToCall) || (balanceAfterCall < bigBlind))
            return balanceAfterCall;
        // This is the bet size each player should have before counting his stack
        Double betAfterCallBeforeRaise = myBet + amountToCall;

        // looking for the chip leader opp
        Double maxStack = -1.0;
        for (int i = 0; i<snapshot.getPlayers().length; i++) {
            Snapshot.Player opp = snapshot.getPlayers()[i];
            if ((i == userChair) || (opp.getPlaying() == 0)) {
                continue;
            }
            Double oppAmountToCall   = betAfterCallBeforeRaise - opp.getCurrentbet();
            Double oppStackAfterCall = opp.getBalance() - oppAmountToCall;
            if (oppStackAfterCall > maxStack) {
                maxStack = oppStackAfterCall;
            }
        }
        Double allInVal = Math.min(maxStack, balanceAfterCall);
        return allInVal;
    }

    public Double maxRaiseVal(Hand hand) {
        if (!getValidOps(hand).contains(GameOp.OP_RAISE()))
            throw new IllegalStateException("Can't evaluate raise value when raise is not an option");
        Double allInVal = allInVal(hand);
        Double bigBlind = hand.latestSnapshot().getSymbols().get(Snapshot.SYMBOLS.BIG_BLIND);
        if (allInVal < bigBlind*2)
            throw new IllegalStateException("Can't evaluate raise value when raise is not an option");
        return allInVal - bigBlind;
    }
}
