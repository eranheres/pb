package com.pb.dao;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * Hand Data Access Object
 */
@AllArgsConstructor
@NoArgsConstructor
@Service
public class HandDao {

    @Autowired
    PBDataSource dataSource;

    public Hand getHand(HandId handId) throws IOException {
        List<Snapshot> data = dataSource.getList(handId.getId());
        if ((data == null) || (data.size() == 0))
            return null;
        Snapshot[] snapshotsArray = new Snapshot[data.size()];
        data.toArray(snapshotsArray);
        return new Hand(snapshotsArray);
    }
}
