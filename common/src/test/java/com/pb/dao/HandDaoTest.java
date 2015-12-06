package com.pb.dao;

import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Map;

/**
 *
 */
public class HandDaoTest {

    @Test
    public void getHand() throws Exception {
        PBDataSource dataSource = Mockito.mock(PBDataSource.class);
        SnapshotSerialize snapshotSerialize = Mockito.mock(SnapshotSerialize.class);

        Snapshot S1 = new Snapshot(); S1.setState(new Snapshot.State("111"));
        Snapshot S2 = new Snapshot(); S2.setState(new Snapshot.State("222"));
        Snapshot S3 = new Snapshot(); S3.setState(new Snapshot.State("333"));

        Snapshot snapshots[] = { S1, S2, S3 };
        Map<String, Snapshot> vals = ImmutableMap.of(
                "v1", S1,
                "v2", S2,
                "v3", S3);
        Hand expected = new Hand(snapshots);
        Mockito.when(dataSource.getList("test")).thenReturn(new ArrayList<String>(vals.keySet()));
        Mockito.when(dataSource.getList("test1")).thenReturn(null);

        Mockito.when(snapshotSerialize.fromString("v1")).thenReturn(vals.get("v1"));
        Mockito.when(snapshotSerialize.fromString("v2")).thenReturn(vals.get("v2"));
        Mockito.when(snapshotSerialize.fromString("v3")).thenReturn(vals.get("v3"));
        HandDao dao = new HandDao(dataSource, snapshotSerialize);
        Hand actual = dao.getHand(HandId.of("test"));
        Assert.assertEquals(expected, actual);

        Assert.assertEquals(null, dao.getHand(HandId.of("test1")));
    }

}