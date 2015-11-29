package com.pb.validator.dao;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 */
public class HandDaoTest {

    @Test
    public void getHand() throws Exception {
        PBDataSource dataSource = mock(PBDataSource.class);
        SnapshotSerialize snapshotSerialize = mock(SnapshotSerialize.class);

        Snapshot S1 = new Snapshot(); S1.setState(new Snapshot.State("111"));
        Snapshot S2 = new Snapshot(); S2.setState(new Snapshot.State("222"));
        Snapshot S3 = new Snapshot(); S3.setState(new Snapshot.State("333"));

        Snapshot snapshots[] = { S1, S2, S3 };
        Map<String, Snapshot> vals = ImmutableMap.of(
                "v1", S1,
                "v2", S2,
                "v3", S3);
        Hand expected = new Hand(snapshots);
        when(dataSource.getList("test")).thenReturn(new ArrayList<String>(vals.keySet()));
        when(dataSource.getList("test1")).thenReturn(null);

        when(snapshotSerialize.fromString("v1")).thenReturn(vals.get("v1"));
        when(snapshotSerialize.fromString("v2")).thenReturn(vals.get("v2"));
        when(snapshotSerialize.fromString("v3")).thenReturn(vals.get("v3"));
        HandDao dao = new HandDao(dataSource, snapshotSerialize);
        Hand actual = dao.getHand(HandId.of("test"));
        assertEquals(expected, actual);

        assertEquals(null, dao.getHand(HandId.of("test1")));
    }

}