package com.pb.player;

import com.google.common.collect.ImmutableMap;
import com.pb.dao.Hand;
import com.pb.dao.Snapshot;
import lombok.AllArgsConstructor;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

public class PlayOptionsTest {

    @AllArgsConstructor
    private class TestAndExpectedRaise {
        Snapshot snapshot;
        Integer  expected;
    }

    /////////////////////////////////////////////////////////////////////////////////////

    @DataProvider(name = "minRaiseVal-Error-Parameters")
    public Object[][] parameterProvider4() {
        return new Object[][]{
                { new TestAndExpectedRaise(
                        Snapshot.fromSymbols(ImmutableMap.of("DollarsToCall", 100.0, "balance", 5.0)),
                        2) },
                { new TestAndExpectedRaise(
                        Snapshot.fromSymbols(ImmutableMap.of("DollarsToCall", 50.0, "bblind", 2.0, "balance", 0.0)),
                        50) }
        };
    }

    @Test(dataProvider = "minRaiseVal-Error-Parameters", expectedExceptions = IllegalStateException.class)
    public void testMinRaiseValErrors(TestAndExpectedRaise testAndExpected) throws Exception {
        Hand hand = new Hand(new Snapshot[] { testAndExpected.snapshot });
        PlayOptions playOptions = new PlayOptions();
        assertEquals(testAndExpected.expected, playOptions.minRaiseVal(hand));
    }

    @Test(dataProvider = "minRaiseVal-Error-Parameters", expectedExceptions = IllegalStateException.class)
    public void testMaxRaiseValErrors(TestAndExpectedRaise testAndExpected) throws Exception {
        Hand hand = new Hand(new Snapshot[] { testAndExpected.snapshot });
        PlayOptions playOptions = new PlayOptions();
        assertEquals(testAndExpected.expected, playOptions.maxRaiseVal(hand));
    }

    /////////////////////////////////////////////////////////////////////////////////////

    @DataProvider(name = "minRaiseVal-Parameters")
    public Object[][] parameterProvider3() {
        return new Object[][]{
                { new TestAndExpectedRaise(
                        Snapshot.fromSymbols(ImmutableMap.of("DollarsToCall", 0.0, "bblind", 2.0, "balance", 100.0)),
                        2) },
                { new TestAndExpectedRaise(
                        Snapshot.fromSymbols(ImmutableMap.of("DollarsToCall", 50.0, "bblind", 2.0, "balance", 200.0)),
                        50) }
        };
    }

    @Test(dataProvider = "minRaiseVal-Parameters")
    public void testMinRaiseVal(TestAndExpectedRaise testAndExpected) throws Exception {
        Hand hand = new Hand(new Snapshot[] { testAndExpected.snapshot });
        PlayOptions playOptions = new PlayOptions();
        assertEquals(testAndExpected.expected, playOptions.minRaiseVal(hand));
    }
    /////////////////////////////////////////////////////////////////////////////////////

    @DataProvider(name = "maxRaiseVal-Parameters")
    public Object[][] parameterProvider2() {
        return new Object[][]{
                { new TestAndExpectedRaise(
                        Snapshot.fromSymbols(ImmutableMap.of(
                                Snapshot.VALUES.SYMBOL_AMOUNT_TO_CALL, 0.0,
                                "bblind", 2.0,
                                "balance", 100.0)),
                        98) },
                { new TestAndExpectedRaise(
                        Snapshot.fromSymbols(ImmutableMap.of(
                                Snapshot.VALUES.SYMBOL_AMOUNT_TO_CALL, 50.0,
                                "bblind", 2.0,
                                "balance", 100.0)),
                        98) },
                { new TestAndExpectedRaise(
                        Snapshot.fromSymbols(ImmutableMap.of(
                                "DollarsToCall", 50.0,
                                "bblind", 2.0,
                                "balance", 100.0)),
                        98) },
        };
    }

    @Test(dataProvider = "maxRaiseVal-Parameters")
    public void testMaxRaiseVal(TestAndExpectedRaise testAndExpected) throws Exception {
        Hand hand = new Hand(new Snapshot[] { testAndExpected.snapshot });
        PlayOptions playOptions = new PlayOptions();
        assertEquals(testAndExpected.expected, playOptions.maxRaiseVal(hand));
    }

    /////////////////////////////////////////////////////////////////////////////////////

    @AllArgsConstructor
    private class TestAndExpectedOps {
        Snapshot snapshot;
        List<GameOp> expected;
    }

    @DataProvider(name = "GetValidOps-Parameters")
    public Object[][] parameterProvider() {
        return new Object[][]{
                { new TestAndExpectedOps(
                        Snapshot.fromSymbols(ImmutableMap.of("DollarsToCall", 0.0, "balance", 100.0)),
                        Arrays.asList(GameOp.OP_ALLIN, GameOp.OP_CHECK, GameOp.OP_RAISE)) },
                { new TestAndExpectedOps(
                        Snapshot.fromSymbols(ImmutableMap.of("DollarsToCall", 50.0, "balance", 100.0)),
                        Arrays.asList(GameOp.OP_ALLIN, GameOp.OP_CALL, GameOp.OP_RAISE, GameOp.OP_FOLD)) },
                { new TestAndExpectedOps(
                        Snapshot.fromSymbols(ImmutableMap.of("DollarsToCall", 50.0, "balance", 30.0)),
                        Arrays.asList(GameOp.OP_ALLIN, GameOp.OP_FOLD)) }
        };
    }

    @Test(dataProvider = "GetValidOps-Parameters")
    public void testGetValidOps(TestAndExpectedOps testAndExpected) throws Exception {
        Hand hand = new Hand(new Snapshot[] { testAndExpected.snapshot });
        PlayOptions playOptions = new PlayOptions();
        assertEquals(testAndExpected.expected, playOptions.getValidOps(hand));
    }
}