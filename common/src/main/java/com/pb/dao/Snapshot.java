package com.pb.dao;

import lombok.*;

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

    @NoArgsConstructor
    @Getter @Setter
    public static class Player {
        Double  currentbet;
        Double  balance;
        String  name;
        Card[]  cards;
        Integer balance_known;
        Integer name_known;
        Integer fillerbits;
        Integer fillerbytes;
        Integer playing;
        Integer blind;
        Integer active;
        Integer dealt;
        Integer cards_shows;
    }
    Card[]   cards;
    Player[] players;
    Double[] pots;
    State    state;
    Map<String, String> symbols;
    Map<String, String> ppl_symbols;
}
