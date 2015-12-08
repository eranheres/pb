package com.pb.gateway;

import com.pb.dao.PBDataSource;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GatewayControllerTest {

    // Checks two things:
    // A. Data is saved
    // B. Data is validated
    @Test
    public void testSetSnapshot() throws Exception {
        PBDataSource dao = mock(PBDataSource.class);
        ValidationQuery query = mock(ValidationQuery.class);
        GatewayController controller = new GatewayController(dao, query);

        controller.setSnapshot("zzz", "type1", "body");
        verify(query).validateHand("zzz");
        verify(dao).saveToList("zzz", "body");
    }
}