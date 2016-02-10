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

        public final static String DATATYPE_HANDRESET   = "handreset";
        public final static String DATATYPE_HEARTBEAT   = "heartbeat";
        public final static String DATATYPE_MYTURN      = "my_turn";
        public final static String DATATYPE_SHOWDOWN    = "showdown";
        public final static String DATATYPE_POSTHAND    = "posthand";
        public final static String DATATYPE_NEWROUND    = "new_round";

        public final static double PREVACTION_PREFOLD   = -2.0;
        public final static double PREVACTION_FOLD      = -1.0;
        public final static double PREVACTION_CHECK     = 0.0;
        public final static double PREVACTION_CALL      = 1.0;
        public final static double PREVACTION_RAISE     = 2.0;
        public final static double PREVACTION_BETRAISE  = 3.0;
        public final static double PREVACTION_ALLIN     = 4.0;
    }

    public final class SYMBOLS {
        public final static String AMOUNT_TO_CALL               = "DollarsToCall";
        public final static String BALANCE                      = "balance";
        public final static String OPPONENTS_WITH_HIGHER_STACK  = "OpponentsWithHigherStack";
        public final static String BIG_BLIND                    = "bblind";
        public final static String CURRENTBET                   = "currentbet";
        public final static String PREVACTION                   = "prevaction";
        public final static String USERCHAIR                    = "userchair";
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
        Integer my_turn_count;
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
        public static Player create(Integer playing, Double balance, Double currentBet) {
            Player p = new Player();
            p.playing = playing;
            p.balance = balance;
            p.currentbet = currentBet;
            return p;
        }
    }
    Card[]   cards;
    Player[] players;
    Double[] pots;
    State    state;
    Map<String, Double> symbols;

    static public Snapshot create(Map<String, Double> symbols) {
        Snapshot snapshot = new Snapshot();
        snapshot.setSymbols(symbols);
        return snapshot;
    }

    static public Snapshot create(Map<String, Double> symbols, Player players[]) {
        Snapshot snapshot = new Snapshot();
        snapshot.setSymbols(symbols);
        snapshot.setPlayers(players);
        return snapshot;
    }
}
