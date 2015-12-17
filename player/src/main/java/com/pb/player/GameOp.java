package com.pb.player;

import lombok.*;

/**
 * Represent single game operation
 */
@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class GameOp {
    public static final GameOp OP_CHECK  = new GameOp("check");
    public static final GameOp OP_CALL   = new GameOp("call");
    public static final GameOp OP_RAISE  = new GameOp("raise"); // Operates as "bet" also
    public static final GameOp OP_FOLD   = new GameOp("fold");
    public static final GameOp OP_ALLIN  = new GameOp("allin");

    String opName;
    Integer raiseAmount;

    public GameOp(String opName) {
        this.opName = opName;
        raiseAmount = -1;
    }
}
