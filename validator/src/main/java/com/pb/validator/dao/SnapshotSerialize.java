package com.pb.validator.dao;

import java.io.IOException;

/**
 * Abstraction layer for serializing a snapshot
 */
public interface SnapshotSerialize {
    Snapshot fromString(String string) throws IOException;
}
