package com.pb.validator.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Representation of a Hand ID
 */
@AllArgsConstructor
@Getter
public class HandId {
    static HandId of(String id) {
       return new HandId(id);
    }
    String id;
}
