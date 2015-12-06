package com.pb.dao;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Hand {
    @Getter @Setter private Snapshot[] snapshots;
}
