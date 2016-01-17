package com.pb.dao;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Representation of a Hand ID
 */
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class HandId {
    static public HandId of(String id) {
       return new HandId(id);
    }
    private String id;

    @Override
    public String toString() {
        return id;
    }
}
