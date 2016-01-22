package com.pb.persistor;

import com.pb.api.HandValidationException;
import com.pb.api.ValidationQuery;
import com.pb.dao.HandId;
import com.pb.dao.PBDataSource;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PersistorControllerTest {

    // Checks two things:
    // A. Data is validated
    // B. TODO Data is saved
    @Test
    public void testSetSnapshot() throws Exception {

        PBDataSource dao = mock(PBDataSource.class);
        ValidationQuery query = mock(ValidationQuery.class);
        PersistorController controller = new PersistorController(dao, query);

        when(query.validateHand(HandId.of("zzz"))).thenReturn(new ValidationQuery.validatorRes("ok", ""));
        controller.complete(HandId.of("zzz"));
        verify(query).validateHand(HandId.of("zzz"));
        //verify(dao).saveSnapshotToList("zzz", "body");
    }

    @Test(expectedExceptions = HandValidationException.class)
    public void testSetSnapshotExeption() throws Exception {
        PBDataSource dao = mock(PBDataSource.class);
        ValidationQuery query = mock(ValidationQuery.class);
        PersistorController controller = new PersistorController(dao, query);

        when(query.validateHand(HandId.of("zzz"))).thenReturn(new ValidationQuery.validatorRes("not ok", ""));
        controller.complete(HandId.of("zzz"));
        verify(query).validateHand(HandId.of("zzz"));
        verify(dao).saveSnapshotToList("zzz", "body");
    }
}