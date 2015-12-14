package com.pb.validator;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pb.dao.Snapshot;
import org.junit.Test;

import java.io.File;
import java.net.URL;

import static org.junit.Assert.*;

public class SnapshotValidatorOpenPPLTest {

    @Test
    public void testValidate() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        URL url = Thread.currentThread().getContextClassLoader().getResource("SnapshotValidatorOpenPPL_Valid.json");
        assertNotNull(url);
        Snapshot[] lu = mapper.readValue(new File(url.getPath()), Snapshot[].class);

        SnapshotValidator validator = new SnapshotValidatorOpenPPL();

        assertEquals(ValidatorStatus.OK, validator.validate(lu[0]));
        assertEquals(1, lu.length); // Make sure nothing is missed

        // Test invalid values
        url = Thread.currentThread().getContextClassLoader().getResource("SnapshotValidatorOpenPPL_Invalid.json");
        assertNotNull(url);
        lu = mapper.readValue(new File(url.getPath()), Snapshot[].class);

        assertEquals(SnapshotValidatorOpenPPL.OPEN_PPL_ERROR,      validator.validate(lu[0]));
        assertEquals(1, lu.length); // Make sure nothing is missed
    }

}