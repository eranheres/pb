package com.pb.validator.dao;

import lombok.*;

@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Hand {
    @Getter @Setter private Snapshot[] snapshots;
}
