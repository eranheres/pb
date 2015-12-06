package com.pb.dao;

import org.springframework.stereotype.Service;

/**
 * Created by eranh on 11/19/15.
 */
@Service
public interface TableStateDao {
    void save(TableState state);
}
