package com.pb.validator;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pb.dao.Hand;
import org.junit.Test;

import java.io.File;
import java.net.URL;

import static org.junit.Assert.*;

public class HandValidatorValuesStableTest {

    @Test
    public void testIsValid() throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        URL url = Thread.currentThread().getContextClassLoader(). getResource("HandValidatorValuesStable_Invalid.json");
        assertNotNull(url);
        Hand[] hands = mapper.readValue(new File(url.getPath()), Hand[].class);
        HandValidator validator = new HandValidatorValuesStable();

        assertEquals(HandValidatorValuesStable.CONSTANT_VALUE_CHANGED_IN_HAND, validator.validate(hands[0]));
        assertEquals(HandValidatorValuesStable.CONSTANT_VALUE_CHANGED_IN_HAND, validator.validate(hands[1]));
        assertTrue(hands.length == 2); // Make sure nothing is missed
    }

    @Test
    public void testIsValid2() throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        // Test invalid values
        URL url = Thread.currentThread().getContextClassLoader().getResource("HandValidatorValuesStable_Valid.json");
        assertNotNull(url);
        Hand[] hands = mapper.readValue(new File(url.getPath()), Hand[].class);
        HandValidator validator = new HandValidatorValuesStable();

        assertEquals(ValidatorStatus.OK,   validator.validate(hands[0]));
        assertTrue(hands.length == 1); // Make sure nothing is missed
    }
}