package com.pb.validator;

import com.pb.dao.Hand;
import lombok.Getter;
import lombok.Setter;

/**
 * Validate correctness of a hand
 */
public interface HandValidator {
    ValidatorStatus EMPTY_HAND = new ValidatorStatus("Hand has zero snapshots");

    ValidatorStatus validate(Hand hand);
}
