package com.pb.validator;

import com.pb.dao.Hand;
import com.pb.dao.HandDao;
import com.pb.dao.HandId;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Main controller for validation module
 */
@Component
@AllArgsConstructor
@NoArgsConstructor
public class ValidatorController {

    @Autowired
    HandDao dao;

    @Autowired
    ValidatorsManager manager;

    public ValidatorStatus validateSnapshot(String id) throws IOException {
        Hand hand = dao.getHand(HandId.of(id));
        if (hand == null) {
            return ValidatorStatus.NOT_FOUND;
        }
        return manager.validateHandInProgress(hand);
    }

    public ValidatorStatus validatorHand(String id) throws IOException {
        Hand hand = dao.getHand(HandId.of(id));
        if (hand == null) {
            return ValidatorStatus.NOT_FOUND;
        }
        return manager.validateHandFullHand(hand);
    }
}
