package com.pb.validator;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pb.dao.Hand;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.net.URL;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;


public class HandValidatorTurnSeqTest {

    @Test
    public void testIsValid() throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        //mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // Test invalid values
        URL url = Thread.currentThread().getContextClassLoader().
                  getResource("HandValidatorTurnSequenceTest_Invalid.json");
        Assert.assertNotNull(url);
        Hand[] hands = mapper.readValue(new File(url.getPath()), Hand[].class);
        HandValidator validator = new HandValidatorTurnSeq();

        assertEquals(HandValidatorTurnSeq.NO_HANDRESET_ON_FIRST,   validator.validate(hands[0]));
        assertEquals(HandValidatorTurnSeq.HANDRESET_MUST_BE_FIRST, validator.validate(hands[1]));
        assertEquals(HandValidatorTurnSeq.BETROUND_OUT_OF_ORDER,   validator.validate(hands[2]));
        assertEquals(HandValidatorTurnSeq.INVALID_VAL_BETROUND,    validator.validate(hands[3]));
        assertTrue(hands.length == 4); // Make sure nothing is missed
    }

    @Test
    public void testIsValid2() throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        //mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // Test invalid values
        URL url = Thread.currentThread().getContextClassLoader().
                getResource("HandValidatorTurnSequenceTest_Valid.json");
        Assert.assertNotNull(url);
        Hand[] hands = mapper.readValue(new File(url.getPath()), Hand[].class);
        HandValidator validator = new HandValidatorTurnSeq();

        assertEquals(ValidatorStatus.OK,   validator.validate(hands[0]));
        assertTrue(hands.length == 1); // Make sure nothing is missed
    }
}