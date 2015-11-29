package com.pb.validator.dao;

import java.util.List;

/**
 * Abstract layer to access PB data from a generic data source
 */
public interface PBDataSource {
    List<String> getList(String id);
}
