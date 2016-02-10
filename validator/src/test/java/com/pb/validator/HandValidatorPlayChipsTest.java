package com.pb.validator;

import com.google.common.collect.ImmutableMap;
import com.pb.dao.GameOp;
import com.pb.dao.Hand;
import com.pb.dao.PBDataSource;
import com.pb.dao.Snapshot;
import lombok.AllArgsConstructor;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.HashMap;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

public class HandValidatorPlayChipsTest {

    @AllArgsConstructor
    class TestAndExpected {
        public Hand hand;
        public ImmutableMap<Integer, GameOp> ops;
        public ValidatorStatus expected;
    }

    private Snapshot snap(String type, Integer turn, double prevaction, double balance) {
        Snapshot snapshot = new Snapshot();
        snapshot.setSymbols(new HashMap<>());
        snapshot.setState(new Snapshot.State());
        snapshot.getState().setUuid("uuid");
        snapshot.getState().setDatatype(type);
        snapshot.getSymbols().put(Snapshot.SYMBOLS.BALANCE, balance);
        snapshot.getSymbols().put(Snapshot.SYMBOLS.PREVACTION, prevaction);
        snapshot.getSymbols().put(Snapshot.SYMBOLS.BIG_BLIND, 2.0);
        snapshot.getSymbols().put(Snapshot.SYMBOLS.CURRENTBET, 0.0);
        snapshot.getSymbols().put(Snapshot.SYMBOLS.AMOUNT_TO_CALL, 0.0);
        snapshot.getState().setMy_turn_count(turn);
        return snapshot;
    }

    private Hand hand(Snapshot snapshot[]) {
        return new Hand(snapshot);
    }

    @DataProvider(name = "testPlayParams")
    public Object[][] parameterProvider3() {
        return new Object[][]{
                // 0. Positive - call raise check allin
                {new TestAndExpected(
                        hand(new Snapshot[]{
                                snap(Snapshot.VALUES.DATATYPE_HEARTBEAT,  -1, Snapshot.VALUES.PREVACTION_PREFOLD, 300),
                                snap(Snapshot.VALUES.DATATYPE_MYTURN,     0,  Snapshot.VALUES.PREVACTION_PREFOLD, 300),
                                snap(Snapshot.VALUES.DATATYPE_MYTURN,     1,  Snapshot.VALUES.PREVACTION_CALL, 250),
                                snap(Snapshot.VALUES.DATATYPE_MYTURN,     2,  Snapshot.VALUES.PREVACTION_BETRAISE, 150),
                                snap(Snapshot.VALUES.DATATYPE_HEARTBEAT,  2,  Snapshot.VALUES.PREVACTION_BETRAISE, 150),
                                snap(Snapshot.VALUES.DATATYPE_MYTURN,     3,  Snapshot.VALUES.PREVACTION_CHECK, 150),
                                snap(Snapshot.VALUES.DATATYPE_SHOWDOWN,   3,  Snapshot.VALUES.PREVACTION_ALLIN, 0),
                                snap(Snapshot.VALUES.DATATYPE_POSTHAND,   3,  Snapshot.VALUES.PREVACTION_PREFOLD, 0)}),
                        ImmutableMap.of(
                                0, GameOp.OP_CALL().amount(25.0*2),
                                1, GameOp.OP_RAISE().amount(50.0*2),
                                2, GameOp.OP_CHECK(),
                                3, GameOp.OP_ALLIN().amount(75.0*2)),
                        ValidatorStatus.OK)},
                // 1. Positive - fold
                {new TestAndExpected(
                        hand(new Snapshot[]{
                                snap(Snapshot.VALUES.DATATYPE_MYTURN,   0, Snapshot.VALUES.PREVACTION_PREFOLD, 500),
                                snap(Snapshot.VALUES.DATATYPE_SHOWDOWN, 0, Snapshot.VALUES.PREVACTION_FOLD, 500)}),
                        ImmutableMap.of(
                                0, GameOp.OP_FOLD().amount(0.0)),
                        ValidatorStatus.OK)},
                // 2. Negative - action check/fold and balanced changed
                {new TestAndExpected(
                        hand(new Snapshot[]{
                                snap(Snapshot.VALUES.DATATYPE_MYTURN,   0, Snapshot.VALUES.PREVACTION_PREFOLD, 500),
                                snap(Snapshot.VALUES.DATATYPE_POSTHAND, 0, Snapshot.VALUES.PREVACTION_FOLD, 10)}),
                        ImmutableMap.of(
                                0, GameOp.OP_FOLD().amount(0.0)),
                        HandValidatorPlayChips.ACTION_CHECK_FOLD_BALANCE_CHANGED)},
                // 3. Negative - op is fold and op amount is >0
                {new TestAndExpected(
                        hand(new Snapshot[]{
                                snap(Snapshot.VALUES.DATATYPE_MYTURN,   0, Snapshot.VALUES.PREVACTION_PREFOLD, 500),
                                snap(Snapshot.VALUES.DATATYPE_POSTHAND, 0, Snapshot.VALUES.PREVACTION_FOLD, 500)}),
                        ImmutableMap.of(
                                0, GameOp.OP_FOLD().amount(5.0*2)),
                        HandValidatorPlayChips.ACTION_CHECK_FOLD_OP_AMOUNT)},
                // 4. Negative - op is check and op amount is >0
                {new TestAndExpected(
                        hand(new Snapshot[]{
                                snap(Snapshot.VALUES.DATATYPE_MYTURN,   0, Snapshot.VALUES.PREVACTION_PREFOLD, 500),
                                snap(Snapshot.VALUES.DATATYPE_POSTHAND, 0, Snapshot.VALUES.PREVACTION_FOLD, 500)}),
                        ImmutableMap.of(
                                0, GameOp.OP_CHECK().amount(5.0*2)),
                        HandValidatorPlayChips.ACTION_CHECK_FOLD_OP_AMOUNT)},
                // 5. Negative - play call for 50 and balance is wrong
                {new TestAndExpected(
                        hand(new Snapshot[]{
                                snap(Snapshot.VALUES.DATATYPE_MYTURN,    0, Snapshot.VALUES.PREVACTION_PREFOLD, 500),
                                snap(Snapshot.VALUES.DATATYPE_MYTURN,    0, Snapshot.VALUES.PREVACTION_CALL, 400),
                                snap(Snapshot.VALUES.DATATYPE_POSTHAND,  0, Snapshot.VALUES.PREVACTION_CALL, 500)}),
                        ImmutableMap.of(
                                0, GameOp.OP_CALL().amount(25.0*2)),
                        HandValidatorPlayChips.BALANCE_WRONG_AFTER_PLAY)},
                // 6. Negative - play raise for 100 and balance is wrong
                {new TestAndExpected(
                        hand(new Snapshot[]{
                                snap(Snapshot.VALUES.DATATYPE_MYTURN,    0, Snapshot.VALUES.PREVACTION_PREFOLD,  500),
                                snap(Snapshot.VALUES.DATATYPE_MYTURN,    0, Snapshot.VALUES.PREVACTION_BETRAISE, 500),
                                snap(Snapshot.VALUES.DATATYPE_POSTHAND,  0, Snapshot.VALUES.PREVACTION_BETRAISE, 500)}),
                        ImmutableMap.of(
                                0, GameOp.OP_RAISE().amount(25.0*2)),
                        HandValidatorPlayChips.BALANCE_WRONG_AFTER_PLAY)},
                // 7. Negative - play allin, stack must be reduced
                {new TestAndExpected(
                        hand(new Snapshot[]{
                                snap(Snapshot.VALUES.DATATYPE_MYTURN,    0, Snapshot.VALUES.PREVACTION_PREFOLD, 500),
                                snap(Snapshot.VALUES.DATATYPE_HEARTBEAT, 0, Snapshot.VALUES.PREVACTION_ALLIN,   500),
                                snap(Snapshot.VALUES.DATATYPE_POSTHAND,  0, Snapshot.VALUES.PREVACTION_ALLIN,   500)}),
                        ImmutableMap.of(
                                0, GameOp.OP_ALLIN().amount(250.0*2)),
                        HandValidatorPlayChips.BALANCE_WRONG_AFTER_PLAY)},
                // 8. positive - play allin, when caller is with lower stack
                {new TestAndExpected(
                        hand(new Snapshot[]{
                                snap(Snapshot.VALUES.DATATYPE_MYTURN,    0, Snapshot.VALUES.PREVACTION_PREFOLD, 500),
                                snap(Snapshot.VALUES.DATATYPE_HEARTBEAT, 0, Snapshot.VALUES.PREVACTION_ALLIN,   50),
                                snap(Snapshot.VALUES.DATATYPE_SHOWDOWN,  0, Snapshot.VALUES.PREVACTION_ALLIN,   50)}),
                        ImmutableMap.of(
                                0, GameOp.OP_ALLIN().amount(250.0*2)),
                        ValidatorStatus.OK)}
        };
    }

    @Test(dataProvider = "testPlayParams")
    public void testPlay(TestAndExpected param) throws Exception {
        PBDataSource dataSource = mock(PBDataSource.class);
        String id = param.hand.getSnapshots()[0].getState().getUuid();
        for (Integer turn : param.ops.keySet()) {
            when(dataSource.getGameOp(id, turn)).thenReturn(param.ops.get(turn));
        }
        HandValidator validator = new HandValidatorPlayChips(dataSource);
        ValidatorStatus actual = validator.validate(param.hand);
        assertEquals(actual, param.expected);
    }

}