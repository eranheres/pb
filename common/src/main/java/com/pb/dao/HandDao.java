package com.pb.dao;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Hand Data Access Object
 */
@AllArgsConstructor
@NoArgsConstructor
@Service
public class HandDao {

    PBDataSource dataSource;
    SnapshotSerialize snapshotSerialize;

    public Hand getHand(HandId handId) throws IOException {
        List<Snapshot> snapshots = new ArrayList<Snapshot>();
        List<String> data = dataSource.getList(handId.getId());
        if (data == null)
            return null;
        for (String str : data) {
            Snapshot snapshot = snapshotSerialize.fromString(str);
            snapshots.add(snapshot);
        }
        Snapshot[] snapshotsArray = new Snapshot[snapshots.size()];
        snapshots.toArray(snapshotsArray);
        return new Hand(snapshotsArray);
    }
}
