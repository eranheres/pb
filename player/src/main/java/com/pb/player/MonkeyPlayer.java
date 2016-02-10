package com.pb.player;

import com.pb.dao.GameOp;
import com.pb.dao.Hand;
import com.pb.dao.Snapshot;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * A monkey player (randomizer)
 */
@AllArgsConstructor
@NoArgsConstructor
@Component
public class MonkeyPlayer {

    private static final Logger logger = LoggerFactory.getLogger(MonkeyPlayer.class.getName());
    @Autowired
    Random randomizer;

    @Autowired
    PlayOptions playOptions;

    public static Boolean allwaysRais = true;
    public static Boolean allwaysFold = false;
    public static Boolean allwaysCall = false;
    public static Boolean allwaysRaisMin = false;

    public GameOp play(Hand hand) {
        List<GameOp> ops = playOptions.getValidOps(hand);
        Double bblind = hand.latestSnapshot().getSymbols().get(Snapshot.SYMBOLS.BIG_BLIND);
        GameOp selected = randomGameOp(ops);
        if (selected.getOp().equals(GameOp.OP_RAISE().getOp())) {
            if (allwaysRaisMin) {
                Double minRaise = playOptions.minRaiseVal(hand);
                Double roundedUpBigblinds = Math.ceil(minRaise/bblind)*bblind;
                return selected.amount(roundedUpBigblinds);
            }
            Double maxRaise = playOptions.maxRaiseVal(hand);
            Double minRaise = playOptions.minRaiseVal(hand);
            if (maxRaise < minRaise)
                return null; // just to capture the event, should not be here
            Integer range = (maxRaise.intValue() - minRaise.intValue())/bblind.intValue();
            Double raise;
            if (range == 0) {
                raise = maxRaise;
                logger.info("Monkey player raise: min=" + String.valueOf(minRaise) + " max=" + String.valueOf(maxRaise)
                        + " raise=" + String.valueOf(raise) + " bblind=" + bblind);
            } else {
                Double delta = randomizer.nextInt(range) * bblind;
                raise = minRaise + delta;
                logger.info("Monkey player raise: min=" + String.valueOf(minRaise) + " max=" + String.valueOf(maxRaise)
                        + " raise=" + String.valueOf(raise) + " delta=" + String.valueOf(delta)+" bblind=" + bblind);
            }
            selected.setAmount(Math.floor(raise / bblind) * bblind); // round to bblind
        }
        return selected;
    }

    private GameOp randomGameOp(List<GameOp> ops) {
        if (allwaysRais) {
            for (GameOp op : ops) {
                if (op.getOp().equals(GameOp.OP_RAISE().getOp()))
                    return op;
            }
        }
        if (allwaysCall || allwaysRais) {
            for (GameOp op : ops) {
                if (op.getOp().equals(GameOp.OP_CALL().getOp()))
                    return op;
            }
        }
        if (allwaysCall || allwaysRais) {
            for (GameOp op : ops) {
                if (op.getOp().equals(GameOp.OP_CHECK().getOp()))
                    return op;
            }
        }
        if ((allwaysFold) || (allwaysCall) || allwaysRais)
            return GameOp.OP_ALLIN();
        return ops.get(randomizer.nextInt(ops.size()));
    }
}
