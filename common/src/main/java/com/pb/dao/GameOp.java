package com.pb.dao;

import lombok.*;

/**
 * Represent single game operation
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class GameOp {
    public static GameOp OP_CHECK() { return new GameOp("check"); }
    public static GameOp OP_CALL()  { return new GameOp("call"); }
    public static GameOp OP_RAISE() { return new GameOp("raise"); } // Operates as "bet" also
    public static GameOp OP_FOLD()  { return new GameOp("fold"); }
    public static GameOp OP_ALLIN() { return new GameOp("allin"); }

    String op;
    Integer amountInBB;

    public GameOp(String opName) {
        this.op = opName;
        amountInBB = 0;
    }
    public GameOp amountInBB(Integer raiseAmount) {
        this.amountInBB = raiseAmount;
        return this;
    }
}
