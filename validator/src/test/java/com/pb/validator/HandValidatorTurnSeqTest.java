package com.pb.validator;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pb.validator.dao.Hand;
import org.junit.Test;

import java.io.File;
import java.net.URL;

import static org.junit.Assert.*;

public class HandValidatorTurnSeqTest {

    @Test
    public void testIsValid() throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        //mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // Test invalid values
        URL url = Thread.currentThread().getContextClassLoader().
                  getResource("HandValidatorTurnSequenceTest_Invalid.json");
        assertNotNull(url);
        Hand[] hands = mapper.readValue(new File(url.getPath()), Hand[].class);

        assertEquals(HandValidatorTurnSeq.NO_HANDRESET_ON_FIRST,   new HandValidatorTurnSeq(hands[0]).validate());
        assertEquals(HandValidatorTurnSeq.HANDRESET_MUST_BE_FIRST, new HandValidatorTurnSeq(hands[1]).validate());
        assertEquals(HandValidatorTurnSeq.BETROUND_OUT_OF_ORDER,   new HandValidatorTurnSeq(hands[2]).validate());
        assertEquals(HandValidatorTurnSeq.INVALID_VAL_BETROUND,    new HandValidatorTurnSeq(hands[3]).validate());
        assertTrue(hands.length == 4); // Make sure nothing is missed
    }

}