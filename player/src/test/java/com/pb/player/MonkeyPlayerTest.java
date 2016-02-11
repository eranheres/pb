package com.pb.player;

import com.google.common.collect.ImmutableMap;
import com.google.common.math.DoubleMath;
import com.pb.dao.GameOp;
import com.pb.dao.Hand;
import com.pb.dao.Snapshot;
import lombok.AllArgsConstructor;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.*;

public class MonkeyPlayerTest {

    @AllArgsConstructor
    class TestAndExpected {
        List<GameOp> opList;
        Integer opIndex;
        Integer maxRaise;
        Integer minRaise;
        GameOp expected;
    }

    @DataProvider(name = "Parameters1")
    public Object[][] parameterProvider3() {
        return new Object[][]{
                { new TestAndExpected(
                        Arrays.asList(GameOp.OP_ALLIN(), GameOp.OP_CHECK(), GameOp.OP_FOLD()),
                        2, -1, -1,
                        GameOp.OP_FOLD()) },
                { new TestAndExpected(
                        Arrays.asList(GameOp.OP_ALLIN(), GameOp.OP_CHECK(), GameOp.OP_FOLD(), GameOp.OP_CALL()),
                        0, -1, -1,
                        GameOp.OP_ALLIN()) },
                { new TestAndExpected(
                        Arrays.asList(GameOp.OP_ALLIN(), GameOp.OP_RAISE(), GameOp.OP_FOLD(), GameOp.OP_CALL()),
                        1, 100, 40,
                        GameOp.OP_RAISE().amount( (40.0 + ((100-40)/2)) )) },
                { new TestAndExpected(
                        Arrays.asList(GameOp.OP_ALLIN(), GameOp.OP_RAISE(), GameOp.OP_FOLD(), GameOp.OP_CALL()),
                        1, 8, 8,
                        GameOp.OP_RAISE().amount( 8.0 )) }
        };
    }

    @Test(dataProvider = "Parameters1")
    public void testPlay(TestAndExpected param) throws Exception {
        Random random = mock(Random.class);
        PlayOptions playOptions = mock(PlayOptions.class);

        Hand hand = new Hand();
        Integer bblind = 2;
        hand.setSnapshots(new Snapshot[] { Snapshot.create(ImmutableMap.of("bblind", Double.valueOf(bblind)))});
        when(playOptions.getValidOps(hand)).thenReturn(param.opList);
        when(playOptions.minRaiseVal(hand)).thenReturn(Double.valueOf(param.minRaise));
        when(playOptions.maxRaiseVal(hand)).thenReturn(Double.valueOf(param.maxRaise));
        when(random.nextInt(param.opList.size())).thenReturn(param.opIndex);
        when(random.nextInt(0)).thenThrow(new IllegalArgumentException()); // verify that there is no invalid randomizer call
        if (param.maxRaise - param.minRaise != 0) {
            when(random.nextInt((param.maxRaise - param.minRaise)/bblind))
                    .thenReturn((param.maxRaise - param.minRaise)/bblind/2);
        }
        MonkeyPlayer player = new MonkeyPlayer(random, playOptions);
        assertEquals(player.play(hand), param.expected);
    }
}