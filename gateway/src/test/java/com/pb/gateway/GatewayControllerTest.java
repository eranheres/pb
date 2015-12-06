package com.pb.gateway;

import com.pb.api.ApiResponse;
import com.pb.dao.PBDataSource;
import com.pb.dao.TableState;
import com.pb.dao.TableStateDao;
import org.junit.Test;

import static org.junit.Assert.*;
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

        ApiResponse apires = new ApiResponse(true, "bla", "bla");

        when(query.validateHand("zzz")).thenReturn(apires);
        assertEquals(apires, controller.setSnapshot("zzz", "type1", "body"));
        verify(dao).saveToList("zzz", "body");
    }
}