package com.pb.player;

import com.pb.dao.Hand;
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
        if (selected.equals(GameOp.OP_RAISE)) {
            selected.setRaiseAmount(randomizer.nextInt(playOptions.maxRaiseVal(hand) - playOptions.minRaiseVal(hand)));
        }
        return selected;
    }

    private GameOp randomGameOp(List<GameOp> ops) {
        return ops.get(randomizer.nextInt(ops.size()));
    }
}
