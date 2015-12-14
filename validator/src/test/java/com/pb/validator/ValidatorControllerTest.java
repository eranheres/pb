package com.pb.validator;

import com.pb.dao.Hand;
import com.pb.dao.HandDao;
import com.pb.dao.HandId;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class ValidatorControllerTest {

    @Test
    public void testValidateSnapshot() throws Exception {
        HandDao dao = mock(HandDao.class);
        ValidatorsManager manager = mock(ValidatorsManager.class);
        ValidatorController controller = new ValidatorController(dao, manager);

        // OK
        Hand hand = new Hand();
        when(dao.getHand(HandId.of("zzz"))).thenReturn(hand);
        when(manager.validateHandInProgress(hand)).thenReturn(ValidatorStatus.OK);
        assertEquals(ValidatorStatus.OK, controller.validateSnapshot("zzz"));

        // Hand not found
        hand = new Hand();
        when(dao.getHand(HandId.of("yyy"))).thenReturn(null);
        when(manager.validateHandInProgress(hand)).thenReturn(ValidatorStatus.OK);
        assertEquals(ValidatorStatus.NOT_FOUND, controller.validateSnapshot("yyy"));
    }

    @Test
    public void testValidatorHand() throws Exception {

        HandDao dao = mock(HandDao.class);
        ValidatorsManager manager = mock(ValidatorsManager.class);
        ValidatorController controller = new ValidatorController(dao, manager);

        // OK
        Hand hand = new Hand();
        when(dao.getHand(HandId.of("zzz"))).thenReturn(hand);
        when(manager.validateHandInProgress(hand)).thenReturn(ValidatorStatus.OK);
        assertEquals(ValidatorStatus.OK, controller.validateSnapshot("zzz"));

        // Hand not found
        hand = new Hand();
        when(dao.getHand(HandId.of("yyy"))).thenReturn(null);
        when(manager.validateHandFullHand(hand)).thenReturn(ValidatorStatus.OK);
        assertEquals(ValidatorStatus.NOT_FOUND, controller.validateSnapshot("yyy"));
    }
}