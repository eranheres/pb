package com.pb.dao;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * Representation of a single game snapshot
 */
@Getter @Setter
@EqualsAndHashCode
public class Snapshot {
    public final class VALUES {
        public final static String PREFLOP   = "preflop";
        public final static String FLOP      = "flop";
        public final static String TURN      = "turn";
        public final static String RIVER     = "river";

        public final static String HANDRESET = "handreset";
        public final static String HEARTBEAT = "heartbeat";
        public final static String MYTURN    = "my_turn";
        public final static String SHOWDOWN  = "showdown";
        public final static String NEWROUND  = "new_round";
    }

    @EqualsAndHashCode
    @NoArgsConstructor
    @Getter @Setter
    public static class State {
        public State(String datatype) {
            this.datatype = datatype;
        }
        Integer dealer_chair;
        String  datatype;
        String  title;
        String  room;
        Integer handcount;
        String  uuid;
        Integer is_playing;
        String  betround;
        Integer is_posting;
        Integer fillerbits;
    }
    String[] cards;
    Object[] players;
    Double[] pots;
    State state;
    Map<String, String> symbols;
    Map<String, String> ppl_symbols;
}