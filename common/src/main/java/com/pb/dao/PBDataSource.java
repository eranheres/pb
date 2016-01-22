package com.pb.dao;

import java.util.List;

/**
 * Abstract layer to access PB data from a generic data source
 */
public interface PBDataSource {
    // Snapshot DB
    void saveSnapshotToList(String id, Snapshot value);
    void saveSnapshotToList(String id, String value);
    List<Snapshot> getList(String id);

    // Server play actions
    void saveGameOp(String id, Integer turnCount, GameOp op);
    GameOp getGameOp(String id, Integer turnCount);
}
