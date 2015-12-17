package com.pb.player;

import com.pb.api.HandValidationException;
import com.pb.api.ValidationQuery;
import com.pb.dao.Hand;
import com.pb.dao.HandDao;
import com.pb.dao.HandId;
import com.pb.dao.PBDataSource;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Random;


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
        return player.play(hand);
    }
}
