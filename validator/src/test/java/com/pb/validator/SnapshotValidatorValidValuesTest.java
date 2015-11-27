package com.pb.validator;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pb.validator.model.Snapshot;
import org.junit.Test;

import java.io.File;
import java.net.URL;

import static org.junit.Assert.*;

/**
 */
public class SnapshotValidatorValidValuesTest {

    @Test
    public void testIsValid() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // Test valid values
        URL url = Thread.currentThread().getContextClassLoader().getResource("SnapshotValidatorValidValuesTest_Valid.json");
        assertNotNull(url);
        Snapshot[] lu = mapper.readValue(new File(url.getPath()), Snapshot[].class);

        assertTrue(new SnapshotValidatorValidValues(lu[0]).isValid());
        assertTrue(new SnapshotValidatorValidValues(lu[1]).isValid());
        assertTrue(new SnapshotValidatorValidValues(lu[2]).isValid());
        assertTrue(new SnapshotValidatorValidValues(lu[3]).isValid());
        assertTrue(new SnapshotValidatorValidValues(lu[4]).isValid());
        assertTrue(new SnapshotValidatorValidValues(lu[5]).isValid());
        assertTrue(new SnapshotValidatorValidValues(lu[6]).isValid());
        assertTrue(lu.length == 7); // Make sure nothing is missed

        // Test invalid values
        url = Thread.currentThread().getContextClassLoader().getResource("SnapshotValidatorValidValuesTest_Invalid.json");
        assertNotNull(url);
        lu = mapper.readValue(new File(url.getPath()), Snapshot[].class);

        assertFalse(new SnapshotValidatorValidValues(lu[0]).isValid());
        assertFalse(new SnapshotValidatorValidValues(lu[1]).isValid());
        assertTrue(lu.length == 2); // Make sure nothing is missed
    }
}