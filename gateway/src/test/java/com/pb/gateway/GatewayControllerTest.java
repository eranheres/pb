package com.pb.gateway;

import com.pb.api.HandValidationException;
import com.pb.api.ValidationQuery;
import com.pb.dao.HandId;
import com.pb.dao.PBDataSource;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("Duplicates")
public class GatewayControllerTest {

    // Checks two things:
    // A. Data is saved
    // B. Data is validated
    @Test
    public void testSetSnapshot() throws Exception {
        PBDataSource dao = mock(PBDataSource.class);
        ValidationQuery query = mock(ValidationQuery.class);
        GatewayController controller = new GatewayController(dao, query);

        when(query.validateOngoingHand(HandId.of("zzz"))).thenReturn(new ValidationQuery.validatorRes("ok", ""));
        controller.setSnapshot("zzz", "type1", "body");
        verify(query).validateOngoingHand(HandId.of("zzz"));
        verify(dao).saveToList("zzz", "body");
    }

    @Test(expected = HandValidationException.class)
    public void testSetSnapshotExeption() throws Exception {
        PBDataSource dao = mock(PBDataSource.class);
        ValidationQuery query = mock(ValidationQuery.class);
        GatewayController controller = new GatewayController(dao, query);

        when(query.validateOngoingHand(HandId.of("zzz"))).thenReturn(new ValidationQuery.validatorRes("not ok", ""));
        controller.setSnapshot("zzz", "type1", "body");
        verify(query).validateOngoingHand(HandId.of("zzz"));
        verify(dao).saveToList("zzz", "body");
    }
}