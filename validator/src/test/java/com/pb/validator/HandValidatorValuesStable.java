package com.pb.validator;

import com.pb.dao.Hand;
import com.pb.dao.Snapshot;

import java.lang.reflect.Field;

/**
 * Validates that the values that should be constant all the hand are not changing
 */
public class HandValidatorValuesStable implements HandValidator {
    public static final ValidatorStatus CONSTANT_VALUE_CHANGED_IN_HAND = new ValidatorStatus("Constant value changed");
    private static final String[] playersFields = {
            "name",
            "balance_known",
            "name_known",
            "fillerbits",
            "fillerbytes",
            "blind",
            "dealt"
    };

    private static final String[] stateFields = {
            "dealer_chair",
            "title",
            "title",
            "room",
            "uuid",
            "fillerbits"
    };


    private boolean equalFields(Object obj1, Object obj2, String[] fields) throws NoSuchFieldException, IllegalAccessException {
        for (String f : fields) {
            Field fi1 = obj1.getClass().getDeclaredField(f);
            Field fi2 = obj2.getClass().getDeclaredField(f);
            fi1.setAccessible(true);
            fi2.setAccessible(true);
            if (!fi1.get(obj1).equals(fi2.get(obj2)))
                return false;
        }
        return true;
    }

    @Override
    public ValidatorStatus validate(Hand hand) {
        try {
        Snapshot previous = null;
        for (Snapshot snapshot : hand.getSnapshots()) {
            if (previous != null) {
                if (!equalFields(previous.getState(), snapshot.getState(), stateFields))
                    return CONSTANT_VALUE_CHANGED_IN_HAND;
                if (snapshot.getPlayers().length != previous.getPlayers().length)
                    return CONSTANT_VALUE_CHANGED_IN_HAND;
                for (int i=0; i<snapshot.getPlayers().length; i++) {
                    Snapshot.Player currentPlayer = snapshot.getPlayers()[i];
                    Snapshot.Player previousPlayer = previous.getPlayers()[i];
                    if (!equalFields(currentPlayer, previousPlayer, playersFields))
                        return CONSTANT_VALUE_CHANGED_IN_HAND;
                }
            }
            previous = snapshot;
        }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return ValidatorStatus.INTERNAL_ERROR;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return ValidatorStatus.INTERNAL_ERROR;
        }
        return ValidatorStatus.OK;
    }
}
