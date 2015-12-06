package com.pb.dao;

import org.junit.Assert;
import org.junit.Test;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 *
 */
public class SnapshotJSONSerializeTest {

    @Test
    public void testFromString() throws Exception {
        SnapshotSerialize snapshotSerialize = new SnapshotJSONSerialize();
        URL url = Thread.currentThread().getContextClassLoader().
                     getResource("SnapshotJSONSerializeTest_Valid.json");
        String str = new String(Files.readAllBytes(Paths.get(url.getPath())));
        Snapshot snapshot = snapshotSerialize.fromString(str);

        Assert.assertEquals("my_turn", snapshot.getState().getDatatype());
        Assert.assertEquals("preflop", snapshot.getState().getBetround());

        Assert.assertEquals(10, snapshot.getPots().length);
        Assert.assertEquals(5, snapshot.getCards().length);
        Assert.assertEquals(408, snapshot.getSymbols().keySet().size());
        Assert.assertEquals(668, snapshot.getPpl_symbols().keySet().size());
    }
}