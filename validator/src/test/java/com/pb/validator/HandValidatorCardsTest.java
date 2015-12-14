package com.pb.validator;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pb.dao.Hand;
import org.junit.Test;

import java.io.File;
import java.net.URL;

import static org.junit.Assert.*;

public class HandValidatorCardsTest {

    @Test
    public void testIsValid() throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        URL url = Thread.currentThread().getContextClassLoader(). getResource("HandValidatorCards_Invalid.json");
        assertNotNull(url);
        Hand[] hands = mapper.readValue(new File(url.getPath()), Hand[].class);
        HandValidator validator = new HandValidatorCards();

        assertEquals(HandValidatorCards.CARD_CHANGED_IN_HAND, validator.validate(hands[0]));
        assertEquals(HandValidatorCards.CARD_CHANGED_IN_HAND, validator.validate(hands[1]));
        assertTrue(hands.length == 2); // Make sure nothing is missed
    }

    @Test
    public void testIsValid2() throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        //mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // Test invalid values
        URL url = Thread.currentThread().getContextClassLoader().
                getResource("HandValidatorCards_Valid.json");
        assertNotNull(url);
        Hand[] hands = mapper.readValue(new File(url.getPath()), Hand[].class);
        HandValidator validator = new HandValidatorCards();

        assertEquals(ValidatorStatus.OK,   validator.validate(hands[0]));
        assertTrue(hands.length == 1); // Make sure nothing is missed
    }
}