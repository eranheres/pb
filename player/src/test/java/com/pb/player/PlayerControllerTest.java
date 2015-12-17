package com.pb.player;

import com.pb.api.HandValidationException;
import com.pb.api.ValidationQuery;
import com.pb.dao.PBDataSource;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PlayerControllerTest {

    // Checks two things:
    // A. Data is validated
    // B. TODO Data is saved
    @Test
    public void testSetSnapshot() throws Exception {
/*
        PBDataSource dao = mock(PBDataSource.class);
        ValidationQuery query = mock(ValidationQuery.class);
        PlayerController controller = new PlayerController(dao, query);

        when(query.validateHand("zzz")).thenReturn(new ValidationQuery.validatorRes("ok", ""));
        controller.complete("zzz");
        verify(query).validateHand("zzz");
        //verify(dao).saveToList("zzz", "body");
        */
    }
/*
    @Test(expected = HandValidationException.class)
    public void testSetSnapshotExeption() throws Exception {
        PBDataSource dao = mock(PBDataSource.class);
        ValidationQuery query = mock(ValidationQuery.class);
        PersistorController controller = new PersistorController(dao, query);

        when(query.validateHand("zzz")).thenReturn(new ValidationQuery.validatorRes("not ok", ""));
        controller.complete("zzz");
        verify(query).validateHand("zzz");
        verify(dao).saveToList("zzz", "body");
    }
    */
}