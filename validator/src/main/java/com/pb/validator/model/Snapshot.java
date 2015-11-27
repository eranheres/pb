package com.pb.validator.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Representation of a single game snapshot
 */
public class Snapshot {
    public final class VALUES {
        public final static String PREFLOP   = "preflop";
        public final static String FLOP      = "flop";
        public final static String TURN      = "turn";
        public final static String RIVER     = "river";

        public final static String HANDRESET = "handreset";
        public final static String HEARTBEAT = "heartbeat";
        public final static String MYTURN    = "myturn";
        public final static String SHOWDOWN  = "showdown";
    }
    public class State {
        @Getter @Setter String datatype;
        @Getter @Setter String betround;
    }
    @Getter @Setter State state;
}
