package com.pb.player;

import com.google.common.collect.ImmutableMap;
import com.pb.dao.GameOp;
import com.pb.dao.Hand;
import com.pb.dao.Snapshot;
import com.pb.helpers.TableHelper;
import lombok.AllArgsConstructor;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
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
                        Snapshot.create(
                                ImmutableMap.of(
                                        Snapshot.SYMBOLS.AMOUNT_TO_CALL, 100.0,
                                        Snapshot.SYMBOLS.BIG_BLIND, 2.0,
                                        Snapshot.SYMBOLS.BALANCE, 6.0,
                                        Snapshot.SYMBOLS.USERCHAIR, 3.0),
                                new Snapshot.Player[] {
                                        Snapshot.Player.create(1, 1000.0, 0.0),
                                        Snapshot.Player.create(0, 10.0,   0.0),
                                        Snapshot.Player.create(0, 10.0,   0.0),
                                        Snapshot.Player.create(1, 1000.0, 0.0)
                                }),
                        50 ) },
                { new TestAndExpectedRaise(
                        Snapshot.create(
                                ImmutableMap.of(
                                        Snapshot.SYMBOLS.AMOUNT_TO_CALL, 50.0,
                                        Snapshot.SYMBOLS.BIG_BLIND, 2.0,
                                        Snapshot.SYMBOLS.BALANCE, 0.0,
                                        Snapshot.SYMBOLS.USERCHAIR, 3.0),
                                new Snapshot.Player[] {
                                        Snapshot.Player.create(1, 1000.0, 0.0),
                                        Snapshot.Player.create(0, 10.0,   0.0),
                                        Snapshot.Player.create(0, 10.0,   0.0),
                                        Snapshot.Player.create(1, 1000.0, 0.0)
                                }),
                        25 ) }
        };
    }

    @Test(dataProvider = "minRaiseVal-Error-Parameters", expectedExceptions = IllegalStateException.class)
    public void testMinRaiseValErrors(TestAndExpectedRaise testAndExpected) throws Exception {
        Hand hand = new Hand(new Snapshot[] { testAndExpected.snapshot });
        PlayOptions playOptions = new PlayOptions();
        assertEquals(playOptions.minRaiseVal(hand), Double.valueOf(testAndExpected.expected));
    }

    @Test(dataProvider = "minRaiseVal-Error-Parameters", expectedExceptions = IllegalStateException.class)
    public void testMaxRaiseValErrors(TestAndExpectedRaise testAndExpected) throws Exception {
        Hand hand = new Hand(new Snapshot[] { testAndExpected.snapshot });
        PlayOptions playOptions = new PlayOptions();
        assertEquals(playOptions.maxRaiseVal(hand), Double.valueOf(testAndExpected.expected));
    }

    /////////////////////////////////////////////////////////////////////////////////////

    @DataProvider(name = "minRaiseVal-Parameters")
    public Object[][] parameterProvider3() {
        return new Object[][]{
                { new TestAndExpectedRaise(
                        Snapshot.create(
                                ImmutableMap.of(
                                        Snapshot.SYMBOLS.AMOUNT_TO_CALL, 0.0,
                                        Snapshot.SYMBOLS.BIG_BLIND, 2.0,
                                        Snapshot.SYMBOLS.BALANCE, 100.0,
                                        Snapshot.SYMBOLS.USERCHAIR, 3.0),
                                new Snapshot.Player[] {
                                        Snapshot.Player.create(1, 1000.0, 0.0),
                                        Snapshot.Player.create(0, 10.0,   0.0),
                                        Snapshot.Player.create(0, 10.0,   0.0),
                                        Snapshot.Player.create(1, 1000.0, 0.0)
                                }),
                        2) },
                { new TestAndExpectedRaise(
                        Snapshot.create(
                                ImmutableMap.of(
                                        Snapshot.SYMBOLS.AMOUNT_TO_CALL, 115.0,
                                        Snapshot.SYMBOLS.BIG_BLIND, 15.0,
                                        Snapshot.SYMBOLS.BALANCE, 1360.0,
                                        Snapshot.SYMBOLS.USERCHAIR, 3.0),
                                new Snapshot.Player[] {
                                        Snapshot.Player.create(1, 1000.0, 0.0),
                                        Snapshot.Player.create(0, 10.0,   0.0),
                                        Snapshot.Player.create(0, 10.0,   0.0),
                                        Snapshot.Player.create(1, 1000.0, 0.0)
                                }),
                        115) },
                { new TestAndExpectedRaise(
                        Snapshot.create(
                                ImmutableMap.of(
                                        Snapshot.SYMBOLS.AMOUNT_TO_CALL, 50.0,
                                        Snapshot.SYMBOLS.BIG_BLIND, 2.0,
                                        Snapshot.SYMBOLS.BALANCE, 200.0,
                                        Snapshot.SYMBOLS.USERCHAIR, 3.0),
                                new Snapshot.Player[] {
                                        Snapshot.Player.create(1, 1000.0, 0.0),
                                        Snapshot.Player.create(0, 10.0,   0.0),
                                        Snapshot.Player.create(0, 10.0,   0.0),
                                        Snapshot.Player.create(1, 1000.0, 0.0)
                                }),
                        50) }
        };
    }

    @Test(dataProvider = "minRaiseVal-Parameters")
    public void testMinRaiseVal(TestAndExpectedRaise testAndExpected) throws Exception {
        Hand hand = new Hand(new Snapshot[] { testAndExpected.snapshot });
        PlayOptions playOptions = new PlayOptions();
        assertEquals(playOptions.minRaiseVal(hand), Double.valueOf(testAndExpected.expected));
    }
    /////////////////////////////////////////////////////////////////////////////////////

    @DataProvider(name = "maxRaiseVal-Parameters")
    public Object[][] parameterProvider2() {
        return new Object[][]{
                { new TestAndExpectedRaise(
                        Snapshot.create(
                                ImmutableMap.of(
                                    Snapshot.SYMBOLS.AMOUNT_TO_CALL, 0.0,
                                    Snapshot.SYMBOLS.BIG_BLIND, 2.0,
                                    Snapshot.SYMBOLS.BALANCE, 100.0,
                                    Snapshot.SYMBOLS.USERCHAIR, 3.0),
                                new Snapshot.Player[] {
                                    Snapshot.Player.create(1, 1000.0, 0.0),
                                    Snapshot.Player.create(0, 10.0,   0.0),
                                    Snapshot.Player.create(0, 10.0,   0.0),
                                    Snapshot.Player.create(1, 1000.0, 0.0)
                                }),
                        98 ) }
        };
    }

    @Test(dataProvider = "maxRaiseVal-Parameters")
    public void testMaxRaiseVal(TestAndExpectedRaise testAndExpected) throws Exception {
        Hand hand = new Hand(new Snapshot[] { testAndExpected.snapshot });
        PlayOptions playOptions = new PlayOptions();
        assertEquals(playOptions.maxRaiseVal(hand), Double.valueOf(testAndExpected.expected));
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
                // 0
                { new TestAndExpectedOps(
                        Snapshot.create(
                            ImmutableMap.of(
                                Snapshot.SYMBOLS.AMOUNT_TO_CALL, 0.0,
                                Snapshot.SYMBOLS.BIG_BLIND, 2.0,
                                Snapshot.SYMBOLS.BALANCE, 100.0,
                                Snapshot.SYMBOLS.USERCHAIR, 1.0),
                            new Snapshot.Player[] {
                                Snapshot.Player.create(1, 0.0,    425.0),
                                Snapshot.Player.create(1, 9675.0, 0.0),
                                Snapshot.Player.create(1, 875.0,  50.0),
                                Snapshot.Player.create(1, 475.0,  100.0)
                        }),
                        Arrays.asList(GameOp.OP_ALLIN().amount(100.0), GameOp.OP_CHECK(), GameOp.OP_RAISE())) },
                // 1
                { new TestAndExpectedOps(
                        Snapshot.create(
                            ImmutableMap.of(
                                Snapshot.SYMBOLS.AMOUNT_TO_CALL, 50.0,
                                Snapshot.SYMBOLS.BIG_BLIND, 4.0,
                                Snapshot.SYMBOLS.BALANCE, 201.0,
                                Snapshot.SYMBOLS.USERCHAIR, 1.0),
                            new Snapshot.Player[] {
                                Snapshot.Player.create(1, 200.0,  0.0),
                                Snapshot.Player.create(1, 9675.0, 0.0),
                                Snapshot.Player.create(1, 875.0,  0.0),
                                Snapshot.Player.create(1, 475.0,  0.0)
                             }),
                        Arrays.asList(GameOp.OP_ALLIN().amount(201.0), GameOp.OP_CALL().amount(50.0), GameOp.OP_RAISE(), GameOp.OP_FOLD())) },
                // 2 - call + min raise is larger than stack. raise is not an option
                { new TestAndExpectedOps(
                        Snapshot.create(
                            ImmutableMap.of(
                                Snapshot.SYMBOLS.AMOUNT_TO_CALL, 450.0,
                                Snapshot.SYMBOLS.BIG_BLIND, 4.0,
                                Snapshot.SYMBOLS.BALANCE, 500.0,
                                Snapshot.SYMBOLS.USERCHAIR, 1.0),
                            new Snapshot.Player[] {
                                Snapshot.Player.create(1, 0.0,    425.0),
                                Snapshot.Player.create(1, 9675.0, 0.0),
                                Snapshot.Player.create(1, 875.0,  50.0),
                                Snapshot.Player.create(1, 475.0,  100.0)
                        }),
                        Arrays.asList(GameOp.OP_ALLIN().amount(500.0), GameOp.OP_CALL().amount(450.0), GameOp.OP_FOLD())) },
                // 3 - when bet is an option but stack is lower than bblind
                { new TestAndExpectedOps(
                        Snapshot.create(
                                ImmutableMap.of(
                                        Snapshot.SYMBOLS.AMOUNT_TO_CALL, 0.0,
                                        Snapshot.SYMBOLS.BIG_BLIND, 50.0,
                                        Snapshot.SYMBOLS.BALANCE, 40.0,
                                        Snapshot.SYMBOLS.USERCHAIR, 1.0),
                                new Snapshot.Player[] {
                                        Snapshot.Player.create(1, 0.0,    425.0),
                                        Snapshot.Player.create(1, 9675.0, 0.0),
                                        Snapshot.Player.create(1, 875.0,  50.0),
                                        Snapshot.Player.create(1, 475.0,  100.0)
                                }),
                        Arrays.asList(GameOp.OP_ALLIN().amount(40.0), GameOp.OP_CHECK())) },
                // 4 - when max opponent stack size is smaller that amount to call
                { new TestAndExpectedOps(
                        Snapshot.create(
                                ImmutableMap.of(
                                        Snapshot.SYMBOLS.AMOUNT_TO_CALL, 100.0,
                                        Snapshot.SYMBOLS.BIG_BLIND, 50.0,
                                        Snapshot.SYMBOLS.BALANCE, 300.0,
                                        Snapshot.SYMBOLS.USERCHAIR, 1.0),
                                new Snapshot.Player[] {
                                        Snapshot.Player.create(1, 0.0,    0.0),
                                        Snapshot.Player.create(1, 9675.0, 0.0),
                                        Snapshot.Player.create(1, 30.0,  50.0),
                                        Snapshot.Player.create(1, 40.0,  10.0)
                                }),
                        Arrays.asList(GameOp.OP_ALLIN().amount(300.0), GameOp.OP_CALL().amount(100.0), GameOp.OP_FOLD())) },
                // 5 - when max opponent stack size is smaller than bblind
                { new TestAndExpectedOps(
                        Snapshot.create(
                                ImmutableMap.of(
                                        Snapshot.SYMBOLS.AMOUNT_TO_CALL, 0.0,
                                        Snapshot.SYMBOLS.BIG_BLIND, 50.0,
                                        Snapshot.SYMBOLS.BALANCE, 300.0,
                                        Snapshot.SYMBOLS.USERCHAIR, 1.0),
                                new Snapshot.Player[] {
                                        Snapshot.Player.create(1, 25.0,    0.0),
                                        Snapshot.Player.create(1, 9675.0, 0.0),
                                        Snapshot.Player.create(0, 875.0,  50.0),
                                        Snapshot.Player.create(0, 475.0,  100.0)
                                }),
                        Arrays.asList(GameOp.OP_ALLIN().amount(300.0), GameOp.OP_CHECK())) },
                // 6
                { new TestAndExpectedOps(
                        Snapshot.create(
                                ImmutableMap.of(
                                        Snapshot.SYMBOLS.AMOUNT_TO_CALL, 50.0,
                                        Snapshot.SYMBOLS.BIG_BLIND, 10.0,
                                        Snapshot.SYMBOLS.BALANCE, 30.0,
                                        Snapshot.SYMBOLS.USERCHAIR, 1.0),
                                new Snapshot.Player[] {
                                        Snapshot.Player.create(1, 0.0,    425.0),
                                        Snapshot.Player.create(1, 9675.0, 0.0),
                                        Snapshot.Player.create(1, 875.0,  50.0),
                                        Snapshot.Player.create(1, 475.0,  100.0)
                                }),
                        Arrays.asList(GameOp.OP_ALLIN().amount(30.0), GameOp.OP_FOLD())) },
                // 7
                { new TestAndExpectedOps(
                        Snapshot.create(
                                ImmutableMap.of(
                                        Snapshot.SYMBOLS.AMOUNT_TO_CALL, 425.0,
                                        Snapshot.SYMBOLS.BIG_BLIND, 100.0,
                                        Snapshot.SYMBOLS.BALANCE, 9675.0,
                                        Snapshot.SYMBOLS.USERCHAIR, 1.0),
                                new Snapshot.Player[] {
                                        Snapshot.Player.create(1, 0.0,    425.0),
                                        Snapshot.Player.create(1, 9675.0, 0.0),
                                        Snapshot.Player.create(1, 875.0,  50.0),
                                        Snapshot.Player.create(1, 475.0,  100.0)
                                }),
                        Arrays.asList(GameOp.OP_ALLIN().amount(9675.0), GameOp.OP_CALL().amount(425.0), GameOp.OP_FOLD())) }
        };
    }

    @Test(dataProvider = "GetValidOps-Parameters")
    public void testGetValidOps(TestAndExpectedOps testAndExpected) throws Exception {
        Hand hand = new Hand(new Snapshot[] { testAndExpected.snapshot });
        PlayOptions playOptions = new PlayOptions();
        assertEquals(playOptions.getValidOps(hand), testAndExpected.expected);
    }
}