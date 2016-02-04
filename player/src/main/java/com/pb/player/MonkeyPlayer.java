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

/**
 * A monkey player (randomizer)
 */
@AllArgsConstructor
@NoArgsConstructor
@Component
public class MonkeyPlayer {
    @Autowired
    Random randomizer;

    @Autowired
    PlayOptions playOptions;

    public GameOp play(Hand hand) {
        List<GameOp> ops = playOptions.getValidOps(hand);
        GameOp selected = randomGameOp(ops);
        if (selected.getOp().equals(GameOp.OP_RAISE().getOp())) {
            Integer amount = randomizer.nextInt(playOptions.maxRaiseVal(hand) - playOptions.minRaiseVal(hand));
            amount += playOptions.minRaiseVal(hand);
            Double bblind = hand.latestSnapshot().getSymbols().get(Snapshot.SYMBOLS.BIG_BLIND);
            Integer raiseInBB = Double.valueOf(Math.ceil(amount/bblind)).intValue();
            selected.setAmountInBB(raiseInBB);
        }
        return selected;
    }

    private GameOp randomGameOp(List<GameOp> ops) {
        /*
        for (GameOp op : ops) {
            if (op.getOp().equals(GameOp.OP_CALL().getOp()))
                return op;
        }
        return GameOp.OP_FOLD();
        */
        return ops.get(randomizer.nextInt(ops.size()));
    }
}
