package com.pb.dao;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Abstract layer to access PB data from a generic data source
 */
public interface PBDataSource {
    void saveToList(String id, Snapshot value);
    void saveToList(String id, String value);
    List<Snapshot> getList(String id);
}
