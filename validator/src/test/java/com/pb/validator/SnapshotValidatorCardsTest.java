package com.pb.validator;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pb.dao.Snapshot;
import org.junit.Test;

import java.io.File;
import java.net.URL;

import static org.junit.Assert.*;

public class SnapshotValidatorCardsTest {

    @Test
    public void testValidate() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        URL url = Thread.currentThread().getContextClassLoader().getResource("SnapshotValidatorValidCards_Valid.json");
        assertNotNull(url);
        Snapshot[] lu = mapper.readValue(new File(url.getPath()), Snapshot[].class);

        SnapshotValidator validator = new SnapshotValidatorCards();

        assertEquals(ValidatorStatus.OK, validator.validate(lu[0]));
        assertEquals(ValidatorStatus.OK, validator.validate(lu[1]));
        assertEquals(ValidatorStatus.OK, validator.validate(lu[2]));
        assertEquals(4, lu.length); // Make sure nothing is missed

        // Test invalid values
        url = Thread.currentThread().getContextClassLoader().getResource("SnapshotValidatorValidCards_Invalid.json");
        assertNotNull(url);
        lu = mapper.readValue(new File(url.getPath()), Snapshot[].class);

        assertEquals(SnapshotValidatorCards.PLAYER_CARDS_INVALID,      validator.validate(lu[0]));
        assertEquals(SnapshotValidatorCards.PLAYER_CARDS_INVALID,      validator.validate(lu[1]));
        assertEquals(SnapshotValidatorCards.PLAYER_CARDS_INVALID,      validator.validate(lu[2]));
        assertEquals(SnapshotValidatorCards.PLAYER_CARDS_INVALID,      validator.validate(lu[3]));
        assertEquals(SnapshotValidatorCards.DUPLICATE_CARDS_IN_TABLE,  validator.validate(lu[4]));
        assertEquals(SnapshotValidatorCards.DUPLICATE_CARDS_IN_TABLE,  validator.validate(lu[5]));
        assertEquals(SnapshotValidatorCards.DUPLICATE_CARDS_IN_TABLE,  validator.validate(lu[6]));
        assertEquals(SnapshotValidatorCards.DUPLICATE_CARDS_IN_TABLE,  validator.validate(lu[7]));
        assertEquals(SnapshotValidatorCards.DUPLICATE_CARDS_IN_TABLE,  validator.validate(lu[8]));
        assertEquals(SnapshotValidatorCards.WRONG_AMOUNT_PUBLIC_CARDS, validator.validate(lu[9]));
        assertEquals(SnapshotValidatorCards.WRONG_AMOUNT_PUBLIC_CARDS, validator.validate(lu[10]));
        assertEquals(SnapshotValidatorCards.BETROUND_NOT_FIT_CARDS, validator.validate(lu[11]));
        assertEquals(SnapshotValidatorCards.BETROUND_NOT_FIT_CARDS, validator.validate(lu[12]));
        assertEquals(13, lu.length); // Make sure nothing is missed
    }
}