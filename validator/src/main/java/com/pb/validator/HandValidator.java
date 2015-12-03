package com.pb.validator;

import com.pb.dao.Hand;
import lombok.Getter;
import lombok.Setter;

/**
 * Validate correctness of a hand
 */
@Getter
@Setter
public abstract class HandValidator {
    Hand hand;
    public abstract ValidatorStatus validate();
}
