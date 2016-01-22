package com.pb.player;

import com.pb.api.HandValidationException;
import com.pb.api.ValidationQuery;
import com.pb.dao.*;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

public class PlayerControllerTest {

    // Checks:
    // Validation of hand
    // Fetching of hand
    // Playing the hand

    @Test
    public void testSetSnapshot() throws Exception {
        HandDao dao = mock(HandDao.class);
        ValidationQuery query = mock(ValidationQuery.class);
        MonkeyPlayer player = mock(MonkeyPlayer.class);
        PBDataSource dataSource = mock(PBDataSource.class);

        PlayerController controller = new PlayerController(dao, query, player, dataSource);

        Snapshot[] snapshots = { new Snapshot() };
        snapshots[0].setState(new Snapshot.State());
        snapshots[0].getState().setBetround("river");
        snapshots[0].getState().setMy_turn_count(100);
        Hand hand = new Hand(snapshots);
        when(dao.getHand(HandId.of("yyy"))).thenReturn(hand);
        when(query.validateOngoingHand(HandId.of("yyy"))).thenReturn(new ValidationQuery.validatorRes("ok", ""));
        when(player.play(hand)).thenReturn(GameOp.OP_ALLIN);

        assertEquals(controller.play(HandId.of("yyy"), "river"), GameOp.OP_ALLIN);

        verify(dao).getHand(HandId.of("yyy"));
        verify(dataSource).saveGameOp("yyy", 100, GameOp.OP_ALLIN);
        verify(query).validateOngoingHand(HandId.of("yyy"));
    }

    @Test(expectedExceptions = HandValidationException.class)
    public void testException() throws Exception {
        HandDao dao = mock(HandDao.class);
        ValidationQuery query = mock(ValidationQuery.class);
        PlayerController controller = new PlayerController(dao, query, null, null);

        when(query.validateOngoingHand(HandId.of("yyy"))).thenReturn(new ValidationQuery.validatorRes("not ok", ""));
        controller.play(HandId.of("yyy"), "river");
    }

    @Test(expectedExceptions = IOException.class)
    public void testException2() throws Exception {
        HandDao dao = mock(HandDao.class);
        ValidationQuery query = mock(ValidationQuery.class);
        PlayerController controller = new PlayerController(dao, query, null, null);

        when(query.validateOngoingHand(HandId.of("yyy"))).thenReturn(new ValidationQuery.validatorRes("ok", ""));
        when(dao.getHand(HandId.of("yyy"))).thenThrow(new IOException("error"));
        controller.play(HandId.of("yyy"), "river");
    }
}