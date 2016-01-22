package com.pb.player;

import com.pb.api.HandValidationException;
import com.pb.api.ValidationQuery;
import com.pb.dao.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;


/**
 * Player controller
 */
@Service
@NoArgsConstructor
@AllArgsConstructor
public class PlayerController {

    @Autowired
    HandDao handDao;

    @Autowired
    ValidationQuery query;

    @Autowired
    MonkeyPlayer player;

    @Autowired
    PBDataSource pbDataSource;

    public GameOp play(HandId id, String betround) throws IOException {
        // Validate hand with validator
        ValidationQuery.validatorRes res = query.validateOngoingHand(id);
        if (!res.getValidation().toUpperCase().equals("OK"))
            throw new HandValidationException(res.getReason());
        // Fetch hand
        Hand hand = handDao.getHand(id);
        if (!hand.latestSnapshot().getState().getBetround().equals(betround))
            throw new HandValidationException("Betround of request does not match hand's state betround");
        // Play
        GameOp op = player.play(hand);
        // Track play action for later validation
        pbDataSource.saveGameOp(id.toString(), hand.latestSnapshot().getState().getMy_turn_count(), op);
        return op;
    }
}
