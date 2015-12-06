package com.pb.dao;

import org.junit.Assert;
import org.junit.Test;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class SnapshotJSONSerializeTest {

    @Test
    public void testFromString() throws Exception {
        SnapshotJSONSerialize snapshotSerialize = new SnapshotJSONSerialize();
        URL url = Thread.currentThread().getContextClassLoader().
                     getResource("SnapshotJSONSerializeTest_Valid.json");
        String str = new String(Files.readAllBytes(Paths.get(url.getPath())));
        // Deserialize
        Snapshot snapshot = snapshotSerialize.deserialize(str.getBytes());

        assertEquals("my_turn", snapshot.getState().getDatatype());
        assertEquals("preflop", snapshot.getState().getBetround());

        assertEquals(10, snapshot.getPots().length);
        assertEquals(5, snapshot.getCards().length);
        assertEquals(408, snapshot.getSymbols().keySet().size());
        assertEquals(668, snapshot.getPpl_symbols().keySet().size());

        // Serialize
        byte[] stream = snapshotSerialize.serialize(snapshot);
        Snapshot snapshot2 = snapshotSerialize.deserialize(stream);
        assertEquals(snapshot, snapshot2);
    }
}