package com.pb.gateway;

import com.pb.model.TableState;
import com.pb.model.TableStateDao;
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
        TableStateDao dao = mock(TableStateDao.class);
        ValidationQuery query = mock(ValidationQuery.class);
        GatewayController controller = new GatewayController(dao, query);

        when(query.validateHand("zzz")).thenReturn(ValidationQuery.OK);
        assertEquals(ValidationQuery.OK, controller.setSnapshot("zzz", "type1", "body"));
        verify(dao).save(new TableState("zzz", "type1", "body"));
    }
}