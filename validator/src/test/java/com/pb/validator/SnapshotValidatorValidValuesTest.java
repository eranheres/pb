package com.pb.validator;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pb.dao.Snapshot;
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

        SnapshotValidator validator = new SnapshotValidatorValidValues();

        assertEquals(ValidatorStatus.OK, validator.validate(lu[0]));
        assertEquals(ValidatorStatus.OK, validator.validate(lu[1]));
        assertEquals(ValidatorStatus.OK, validator.validate(lu[2]));
        assertEquals(ValidatorStatus.OK, validator.validate(lu[3]));
        assertEquals(ValidatorStatus.OK, validator.validate(lu[4]));
        assertEquals(ValidatorStatus.OK, validator.validate(lu[5]));
        assertEquals(ValidatorStatus.OK, validator.validate(lu[6]));
        assertEquals(8, lu.length); // Make sure nothing is missed

        // Test invalid values
        url = Thread.currentThread().getContextClassLoader().getResource("SnapshotValidatorValidValuesTest_Invalid.json");
        assertNotNull(url);
        lu = mapper.readValue(new File(url.getPath()), Snapshot[].class);

        assertEquals(SnapshotValidatorValidValues.INVALID_FIELD_DATATYPE, validator.validate(lu[0]));
        assertEquals(SnapshotValidatorValidValues.INVALID_FIELD_ACTION, validator.validate(lu[1]));
        assertTrue(lu.length == 2); // Make sure nothing is missed
    }
}