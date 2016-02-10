package com.pb.helpers;

import com.pb.dao.Snapshot;
import org.testng.annotations.Test;

import java.util.DoubleSummaryStatistics;
import java.util.HashMap;

import static org.testng.Assert.*;

/**
 *
 */
public class TableHelperTest {

    @Test
    public void testUserChair() throws Exception {
        Snapshot snapshot = new Snapshot();
        snapshot.setSymbols(new HashMap<>());
        snapshot.getSymbols().put(Snapshot.SYMBOLS.USERCHAIR, 4.0);
        TableHelper tableHelper = new TableHelper();
        assertEquals(tableHelper.userChair(snapshot), 4);
    }

    private Snapshot.Player p(Integer balance, Boolean playing) {
        Snapshot.Player player = new Snapshot.Player();
        player.setBalance(Double.valueOf(balance));
        player.setPlaying(playing?1:0);
        return player;
    }
    private Snapshot.Player p(Integer balance, Integer bet, Boolean playing) {
        Snapshot.Player player = new Snapshot.Player();
        player.setBalance(Double.valueOf(balance));
        player.setCurrentbet(Double.valueOf(bet));
        player.setPlaying(playing?1:0);
        return player;
    }

    @Test
    public void testMaxPlayingOpponentPosition() throws Exception {
        Snapshot snapshot = new Snapshot();
        snapshot.setSymbols(new HashMap<>());
        snapshot.getSymbols().put(Snapshot.SYMBOLS.USERCHAIR, 4.0);
        Snapshot.Player[] players = {
                p(20, true),
                p(99, false),
                p(20, true),
                p(88, false),
                p(77, true),
                p(20, true),
                p(20, true),
                p(66, true),
                p(20, true) };
        snapshot.setPlayers(players);
        TableHelper tableHelper = new TableHelper();
        assertEquals(tableHelper.maxPlayingOpponentPosition(snapshot), 7);
    }

    @Test
    public void testMaxPlayingOpponentStackSize() throws Exception {
        Snapshot snapshot = new Snapshot();
        snapshot.setSymbols(new HashMap<>());
        snapshot.getSymbols().put(Snapshot.SYMBOLS.USERCHAIR, 4.0);
        Snapshot.Player[] players = {
                p(71, true),
                p(99, false),
                p(20, true),
                p(88, false),
                p(77, true),
                p(20, true),
                p(20, true),
                p(66, true),
                p(20, true) };
        snapshot.setPlayers(players);
        TableHelper tableHelper = new TableHelper();
        assertEquals(tableHelper.maxPlayingOpponentStackSize(snapshot), 71.0);
    }

    @Test
    public void testMaxBalanceBetPlayingOpponentPosition() throws Exception {
        Snapshot snapshot = new Snapshot();
        snapshot.setSymbols(new HashMap<>());
        snapshot.getSymbols().put(Snapshot.SYMBOLS.USERCHAIR, 4.0);
        Snapshot.Player[] players = {
                p(20, 0,  true),
                p(99, 10, false),
                p(20, 0,  true),
                p(88, 0,  false),
                p(77, 0,  true),
                p(20, 20, true),
                p(20, 47, true),
                p(66, 0,  true),
                p(20, 0,  true) };
        snapshot.setPlayers(players);
        TableHelper tableHelper = new TableHelper();
        assertEquals(tableHelper.maxStackBetPlayingOpponentPosition(snapshot), 6);
    }

    @Test
    public void testMaxStackSizePlayingOpponentStackSize() throws Exception {
        Snapshot snapshot = new Snapshot();
        snapshot.setSymbols(new HashMap<>());
        snapshot.getSymbols().put(Snapshot.SYMBOLS.USERCHAIR, 4.0);
        Snapshot.Player[] players = {
                p(71, 0,  true),
                p(99, 0,  false),
                p(20, 53, true),
                p(88, 0,  false),
                p(77, 0,  true),
                p(20, 0,  true),
                p(20, 0,  true),
                p(66, 0,  true),
                p(20, 0,  true) };
        snapshot.setPlayers(players);
        TableHelper tableHelper = new TableHelper();
        assertEquals(tableHelper.maxStackBetPlayingOpponentStackBetSize(snapshot), 73.0);

    }
}
