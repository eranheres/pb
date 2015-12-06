package com.pb.dao;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

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
}
