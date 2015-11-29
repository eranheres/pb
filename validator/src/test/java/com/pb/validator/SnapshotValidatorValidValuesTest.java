package com.pb.validator;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pb.validator.dao.Snapshot;
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

        assertEquals(ValidatorStatus.OK, new SnapshotValidatorValidValues(lu[0]).isValid());
        assertEquals(ValidatorStatus.OK, new SnapshotValidatorValidValues(lu[1]).isValid());
        assertEquals(ValidatorStatus.OK, new SnapshotValidatorValidValues(lu[2]).isValid());
        assertEquals(ValidatorStatus.OK, new SnapshotValidatorValidValues(lu[3]).isValid());
        assertEquals(ValidatorStatus.OK, new SnapshotValidatorValidValues(lu[4]).isValid());
        assertEquals(ValidatorStatus.OK, new SnapshotValidatorValidValues(lu[5]).isValid());
        assertEquals(ValidatorStatus.OK, new SnapshotValidatorValidValues(lu[6]).isValid());
        assertEquals(7, lu.length); // Make sure nothing is missed

        // Test invalid values
        url = Thread.currentThread().getContextClassLoader().getResource("SnapshotValidatorValidValuesTest_Invalid.json");
        assertNotNull(url);
        lu = mapper.readValue(new File(url.getPath()), Snapshot[].class);

        assertEquals(SnapshotValidatorValidValues.INVALID_FIELD_DATATYPE, new SnapshotValidatorValidValues(lu[0]).isValid());
        assertEquals(SnapshotValidatorValidValues.INVALID_FIELD_ACTION, new SnapshotValidatorValidValues(lu[1]).isValid());
        assertTrue(lu.length == 2); // Make sure nothing is missed
    }
}