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
import static org.testng.Assert.*;

public class HandValidatorPlayActionTest {

    @AllArgsConstructor
    class TestAndExpected {
        public Hand hand;
        public ImmutableMap<Integer, GameOp> ops;
        public ValidatorStatus expected;
    }

    private Snapshot snap(String type, Integer turn, double prevaction) {
        Snapshot snapshot = new Snapshot();
        snapshot.setSymbols(new HashMap<>());
        snapshot.setState(new Snapshot.State());
        snapshot.getState().setUuid("uuid");
        snapshot.getState().setDatatype(type);
        snapshot.getState().setMy_turn_count(turn);
        snapshot.getSymbols().put(Snapshot.SYMBOLS.PREVACTION, prevaction);
        return snapshot;
    }

    private Hand hand(Snapshot snapshot[]) {
        return new Hand(snapshot);
    }

    @DataProvider(name = "testPlayParams")
    public Object[][] parameterProvider3() {
        return new Object[][]{
                // 0. Positive fold before flop
                { new TestAndExpected(
                        hand(new Snapshot[] {
                                snap(Snapshot.VALUES.DATATYPE_HEARTBEAT, -1, Snapshot.VALUES.PREVACTION_PREFOLD),
                                snap(Snapshot.VALUES.DATATYPE_MYTURN,    0,  Snapshot.VALUES.PREVACTION_PREFOLD),
                                snap(Snapshot.VALUES.DATATYPE_HEARTBEAT, 0,  Snapshot.VALUES.PREVACTION_FOLD)}),
                        ImmutableMap.of(0, GameOp.OP_FOLD()),
                        ValidatorStatus.OK) },
                // 1. Positive - fold will be shown at showdown
                { new TestAndExpected(
                        hand(new Snapshot[] {
                                snap(Snapshot.VALUES.DATATYPE_MYTURN,    0,  Snapshot.VALUES.PREVACTION_PREFOLD),
                                snap(Snapshot.VALUES.DATATYPE_SHOWDOWN,  0, Snapshot.VALUES.PREVACTION_FOLD)}),
                        ImmutableMap.of(0, GameOp.OP_FOLD()),
                        ValidatorStatus.OK) },
                // 2. Positive - all actions
                { new TestAndExpected(
                        hand(new Snapshot[] {
                                snap(Snapshot.VALUES.DATATYPE_MYTURN,    0, Snapshot.VALUES.PREVACTION_PREFOLD),
                                snap(Snapshot.VALUES.DATATYPE_MYTURN,    1, Snapshot.VALUES.PREVACTION_CALL),
                                snap(Snapshot.VALUES.DATATYPE_MYTURN,    2, Snapshot.VALUES.PREVACTION_BETRAISE),
                                snap(Snapshot.VALUES.DATATYPE_MYTURN,    3, Snapshot.VALUES.PREVACTION_CHECK),
                                snap(Snapshot.VALUES.DATATYPE_SHOWDOWN,  3, Snapshot.VALUES.PREVACTION_ALLIN)}),
                        ImmutableMap.of(
                                0, GameOp.OP_CALL(),
                                1, GameOp.OP_RAISE(),
                                2, GameOp.OP_CHECK(),
                                3, GameOp.OP_ALLIN() ),
                        ValidatorStatus.OK) },
                // 3. Negative - allin shown at the first time myturn shown
                { new TestAndExpected(
                        hand(new Snapshot[] {
                                snap(Snapshot.VALUES.DATATYPE_HEARTBEAT, -1, Snapshot.VALUES.PREVACTION_PREFOLD),
                                snap(Snapshot.VALUES.DATATYPE_MYTURN,     0, Snapshot.VALUES.PREVACTION_CALL)}),
                        ImmutableMap.of(0, GameOp.OP_CALL()),
                        HandValidatorPlayAction.UNINSTRUCTED_PLAY_ACTION) },
                // 4. Negative - allin shown at myturn
                { new TestAndExpected(
                        hand(new Snapshot[] {
                                snap(Snapshot.VALUES.DATATYPE_HEARTBEAT, -1, Snapshot.VALUES.PREVACTION_PREFOLD),
                                snap(Snapshot.VALUES.DATATYPE_MYTURN,     0, Snapshot.VALUES.PREVACTION_PREFOLD),
                                snap(Snapshot.VALUES.DATATYPE_MYTURN,     1, Snapshot.VALUES.PREVACTION_ALLIN)}),
                        ImmutableMap.of(0, GameOp.OP_ALLIN()),
                        HandValidatorPlayAction.ALLIN_NOT_IN_PLACE) },
                // 5. Negative - fold shown at myturn
                { new TestAndExpected(
                        hand(new Snapshot[] {
                                snap(Snapshot.VALUES.DATATYPE_HEARTBEAT,  -1, Snapshot.VALUES.PREVACTION_PREFOLD),
                                snap(Snapshot.VALUES.DATATYPE_MYTURN,      0, Snapshot.VALUES.PREVACTION_PREFOLD),
                                snap(Snapshot.VALUES.DATATYPE_MYTURN,      1, Snapshot.VALUES.PREVACTION_FOLD)}),
                        ImmutableMap.of(0, GameOp.OP_FOLD()),
                        HandValidatorPlayAction.FOLD_NOT_IN_PLACE) },
                // 6. Negative - play op was not taken at all
                { new TestAndExpected(
                        hand(new Snapshot[] {
                                snap(Snapshot.VALUES.DATATYPE_HEARTBEAT, -1, Snapshot.VALUES.PREVACTION_PREFOLD),
                                snap(Snapshot.VALUES.DATATYPE_MYTURN,     0, Snapshot.VALUES.PREVACTION_PREFOLD),
                                snap(Snapshot.VALUES.DATATYPE_MYTURN,     1, Snapshot.VALUES.PREVACTION_PREFOLD)}),
                        ImmutableMap.of(0, GameOp.OP_CALL()),
                        HandValidatorPlayAction.RECORDED_PLAY_WASNT_PLAYED) },
                // 7. Negative - play op took wrong action
                { new TestAndExpected(
                        hand(new Snapshot[] {
                                snap(Snapshot.VALUES.DATATYPE_HEARTBEAT, -1, Snapshot.VALUES.PREVACTION_PREFOLD),
                                snap(Snapshot.VALUES.DATATYPE_MYTURN,     0, Snapshot.VALUES.PREVACTION_PREFOLD),
                                snap(Snapshot.VALUES.DATATYPE_MYTURN,     1, Snapshot.VALUES.PREVACTION_BETRAISE)}),
                        ImmutableMap.of(0, GameOp.OP_CALL()),
                        HandValidatorPlayAction.WRONG_ACTION_TAKEN) },
                // 8. Negative - action wasn't taken
                { new TestAndExpected(
                        hand(new Snapshot[] {
                                snap(Snapshot.VALUES.DATATYPE_HEARTBEAT, -1, Snapshot.VALUES.PREVACTION_PREFOLD),
                                snap(Snapshot.VALUES.DATATYPE_MYTURN,     0, Snapshot.VALUES.PREVACTION_PREFOLD),
                                snap(Snapshot.VALUES.DATATYPE_SHOWDOWN,   0, Snapshot.VALUES.PREVACTION_BETRAISE),
                                snap(Snapshot.VALUES.DATATYPE_POSTHAND,   0, Snapshot.VALUES.PREVACTION_BETRAISE)
                        }),
                        ImmutableMap.of(0, GameOp.OP_CALL(),
                                        1, GameOp.OP_ALLIN()),
                        HandValidatorPlayAction.WRONG_ACTION_TAKEN) },
                // 9. Negative - action wasn't taken
                { new TestAndExpected(
                        hand(new Snapshot[] {
                                snap(Snapshot.VALUES.DATATYPE_HEARTBEAT, -1, Snapshot.VALUES.PREVACTION_PREFOLD),
                                snap(Snapshot.VALUES.DATATYPE_MYTURN,     0, Snapshot.VALUES.PREVACTION_PREFOLD),
                                snap(Snapshot.VALUES.DATATYPE_SHOWDOWN,   0, Snapshot.VALUES.PREVACTION_CALL)}),
                        ImmutableMap.of(0, GameOp.OP_CALL(),
                                        1, GameOp.OP_ALLIN()),
                        HandValidatorPlayAction.RECORDED_PLAY_WASNT_PLAYED) },
                // 10. Negative - action wasn't taken
                { new TestAndExpected(
                        hand(new Snapshot[] {
                                snap(Snapshot.VALUES.DATATYPE_HEARTBEAT, -1, Snapshot.VALUES.PREVACTION_PREFOLD),
                                snap(Snapshot.VALUES.DATATYPE_MYTURN,     0, Snapshot.VALUES.PREVACTION_PREFOLD),
                                snap(Snapshot.VALUES.DATATYPE_HEARTBEAT,  0, Snapshot.VALUES.PREVACTION_CALL),
                                snap(Snapshot.VALUES.DATATYPE_HEARTBEAT,  0, Snapshot.VALUES.PREVACTION_CALL),
                                snap(Snapshot.VALUES.DATATYPE_HEARTBEAT,  0, Snapshot.VALUES.PREVACTION_CALL),
                                snap(Snapshot.VALUES.DATATYPE_SHOWDOWN,   0, Snapshot.VALUES.PREVACTION_CALL)}),
                        ImmutableMap.of(
                                0, GameOp.OP_CALL(),
                                1, GameOp.OP_ALLIN()),
                        HandValidatorPlayAction.RECORDED_PLAY_WASNT_PLAYED) },
                // 11. Negative - action not recorded
                { new TestAndExpected(
                        hand(new Snapshot[] {
                                snap(Snapshot.VALUES.DATATYPE_HEARTBEAT, -1, Snapshot.VALUES.PREVACTION_PREFOLD),
                                snap(Snapshot.VALUES.DATATYPE_MYTURN,     0, Snapshot.VALUES.PREVACTION_PREFOLD),
                                snap(Snapshot.VALUES.DATATYPE_MYTURN,     1, Snapshot.VALUES.PREVACTION_CALL),
                                snap(Snapshot.VALUES.DATATYPE_MYTURN,     2, Snapshot.VALUES.PREVACTION_BETRAISE),
                                snap(Snapshot.VALUES.DATATYPE_HEARTBEAT,  2, Snapshot.VALUES.PREVACTION_CALL),
                                snap(Snapshot.VALUES.DATATYPE_POSTHAND,   2, Snapshot.VALUES.PREVACTION_CALL)}),
                        ImmutableMap.of(0, GameOp.OP_CALL(),
                                        1, GameOp.OP_RAISE()),
                        HandValidatorPlayAction.ACTION_NOT_RECORDED) },
                // 12. Positive call
                { new TestAndExpected(
                        hand(new Snapshot[] {
                                snap(Snapshot.VALUES.DATATYPE_HEARTBEAT, -1, Snapshot.VALUES.PREVACTION_PREFOLD),
                                snap(Snapshot.VALUES.DATATYPE_MYTURN,     0, Snapshot.VALUES.PREVACTION_PREFOLD),
                                snap(Snapshot.VALUES.DATATYPE_MYTURN,     1, Snapshot.VALUES.PREVACTION_CALL)}),
                        ImmutableMap.of(0, GameOp.OP_CALL()),
                        ValidatorStatus.OK) },
                // 13. Positive - fold preflop and showdown twice
                { new TestAndExpected(
                        hand(new Snapshot[] {
                                snap(Snapshot.VALUES.DATATYPE_HANDRESET, -1, Snapshot.VALUES.PREVACTION_PREFOLD),
                                snap(Snapshot.VALUES.DATATYPE_MYTURN,    0, Snapshot.VALUES.PREVACTION_PREFOLD),
                                snap(Snapshot.VALUES.DATATYPE_SHOWDOWN,  0, Snapshot.VALUES.PREVACTION_FOLD),
                                snap(Snapshot.VALUES.DATATYPE_SHOWDOWN,  0, Snapshot.VALUES.PREVACTION_FOLD),
                                snap(Snapshot.VALUES.DATATYPE_POSTHAND,  0, Snapshot.VALUES.PREVACTION_FOLD)}),
                        ImmutableMap.of(0, GameOp.OP_FOLD()),
                        ValidatorStatus.OK) },
                // 14. Negative - turn count out of order
                { new TestAndExpected(
                        hand(new Snapshot[] {
                                snap(Snapshot.VALUES.DATATYPE_HEARTBEAT, -1, Snapshot.VALUES.PREVACTION_PREFOLD),
                                snap(Snapshot.VALUES.DATATYPE_MYTURN,     0, Snapshot.VALUES.PREVACTION_PREFOLD),
                                snap(Snapshot.VALUES.DATATYPE_MYTURN,     0, Snapshot.VALUES.PREVACTION_CALL),
                                snap(Snapshot.VALUES.DATATYPE_HEARTBEAT,  0, Snapshot.VALUES.PREVACTION_CALL)}),
                        ImmutableMap.of(
                                0, GameOp.OP_CALL(),
                                1, GameOp.OP_CALL()),
                        HandValidatorPlayAction.TURN_COUNT_OUT_OF_ORDER) },
                // 15. hand was not played
                { new TestAndExpected(
                        hand(new Snapshot[] {
                                snap(Snapshot.VALUES.DATATYPE_HANDRESET, -1, Snapshot.VALUES.PREVACTION_PREFOLD),
                                snap(Snapshot.VALUES.DATATYPE_NEWROUND,  -1, Snapshot.VALUES.PREVACTION_PREFOLD),
                                snap(Snapshot.VALUES.DATATYPE_NEWROUND,  -1, Snapshot.VALUES.PREVACTION_PREFOLD),
                                snap(Snapshot.VALUES.DATATYPE_NEWROUND,  -1, Snapshot.VALUES.PREVACTION_PREFOLD),
                                snap(Snapshot.VALUES.DATATYPE_SHOWDOWN,  -1, Snapshot.VALUES.PREVACTION_PREFOLD),
                                snap(Snapshot.VALUES.DATATYPE_POSTHAND,  -1, Snapshot.VALUES.PREVACTION_PREFOLD)}),
                        ImmutableMap.of(),
                        ValidatorStatus.OK) },
                // 15. hand was not played
                { new TestAndExpected(
                        hand(new Snapshot[] {
                                snap(Snapshot.VALUES.DATATYPE_HANDRESET, -1, Snapshot.VALUES.PREVACTION_PREFOLD),
                                snap(Snapshot.VALUES.DATATYPE_NEWROUND,  -1, Snapshot.VALUES.PREVACTION_PREFOLD),
                                snap(Snapshot.VALUES.DATATYPE_SHOWDOWN,  -1, Snapshot.VALUES.PREVACTION_PREFOLD),
                                snap(Snapshot.VALUES.DATATYPE_SHOWDOWN,  -1, Snapshot.VALUES.PREVACTION_PREFOLD)}),
                        ImmutableMap.of(),
                        ValidatorStatus.OK) }
        };
    }

    @Test(dataProvider = "testPlayParams")
    public void testPlay(TestAndExpected param) throws Exception {
        PBDataSource dataSource = mock(PBDataSource.class);
        String id = param.hand.getSnapshots()[0].getState().getUuid();
        Integer lastTurn = null;
        for (Integer turn : param.ops.keySet()) {
            when(dataSource.getGameOp(id, turn)).thenReturn(param.ops.get(turn));
            lastTurn = turn;
        }
        if (param.ops.size() != 0)
            when(dataSource.getGameOp(id, lastTurn+1)).thenReturn(null);
        HandValidator validator = new HandValidatorPlayAction(dataSource);
        ValidatorStatus actual = validator.validate(param.hand);
        assertEquals(actual, param.expected);
    }

}