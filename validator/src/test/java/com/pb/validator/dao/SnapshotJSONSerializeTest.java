package com.pb.validator.dao;

import org.junit.Test;
import static org.junit.Assert.*;

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

        assertEquals("my_turn", snapshot.getState().getDatatype());
        assertEquals("preflop", snapshot.getState().getBetround());

        assertEquals(10, snapshot.getPots().length);
        assertEquals(5, snapshot.getCards().length);
        assertEquals(408, snapshot.getSymbols().keySet().size());
        assertEquals(668, snapshot.getPpl_symbols().keySet().size());
    }
}