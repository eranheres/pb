package com.pb.validator;

import com.pb.dao.Snapshot;

/**
 * Validate the integerty of hand cards
 */
public class SnapshotValidatorValidCards implements SnapshotValidator {
    @Override
    public ValidatorStatus validate(Snapshot snapshot) {
        return (previous, current) -> {
            if (current.cards.size() != 2)
                return "Hand does not have 2 cards";
            try {
                Card c1 = new Card(current.cards.get(0));
                Card c2 = new Card(current.cards.get(1));
                if (c1.equals(c2))
                    return "Hand cards are equal";
            } catch (IllegalArgumentException ex) {
                return ex.getMessage();
            }
            return PbValidator.STATUS_OK;
        };
        return null;
    }
}
