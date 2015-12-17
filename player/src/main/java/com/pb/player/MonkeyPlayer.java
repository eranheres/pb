package com.pb.player;

import com.pb.dao.Hand;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Random;

/**
 * A monkey player (random)
 */
@AllArgsConstructor
public class MonkeyPlayer {
    @Autowired
    Random random;

    @Autowired
    PlayOptions playOptions;

    public GameOp play(Hand hand) {
        List<GameOp> ops = playOptions.getValidOps(hand);
        GameOp selected = randomGameOp(ops);
        if (selected.equals(GameOp.OP_RAISE)) {
            selected.setRaiseAmount(random.nextInt(playOptions.maxRaiseVal(hand) - playOptions.minRaiseVal(hand)));
        }
        return selected;
    }

    private GameOp randomGameOp(List<GameOp> ops) {
        return ops.get(random.nextInt(ops.size()));
    }
}
