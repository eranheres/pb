package com.pb.validator;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pb.dao.Snapshot;
import com.pb.dao.SnapshotJSONSerialize;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class SnapshotValidatorValidCardsTest {

    @Test
    public void testValidate() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        URL url = Thread.currentThread().getContextClassLoader().getResource("SnapshotValidatorValidCards_Valid.json");
        assertNotNull(url);
        Snapshot[] lu = mapper.readValue(new File(url.getPath()), Snapshot[].class);

        SnapshotValidator validator = new SnapshotValidatorValidCards();

        assertEquals(ValidatorStatus.OK, validator.validate(lu[0]));
        assertEquals(ValidatorStatus.OK, validator.validate(lu[1]));
        assertEquals(ValidatorStatus.OK, validator.validate(lu[2]));
        assertEquals(4, lu.length); // Make sure nothing is missed

        // Test invalid values
        url = Thread.currentThread().getContextClassLoader().getResource("SnapshotValidatorValidCards_Invalid.json");
        assertNotNull(url);
        lu = mapper.readValue(new File(url.getPath()), Snapshot[].class);

        assertEquals(SnapshotValidatorValidCards.PLAYER_CARDS_INVALID,      validator.validate(lu[0]));
        assertEquals(SnapshotValidatorValidCards.PLAYER_CARDS_INVALID,      validator.validate(lu[1]));
        assertEquals(SnapshotValidatorValidCards.PLAYER_CARDS_INVALID,      validator.validate(lu[2]));
        assertEquals(SnapshotValidatorValidCards.PLAYER_CARDS_INVALID,      validator.validate(lu[3]));
        assertEquals(SnapshotValidatorValidCards.DUPLICATE_CARDS_IN_TABLE,  validator.validate(lu[4]));
        assertEquals(SnapshotValidatorValidCards.DUPLICATE_CARDS_IN_TABLE,  validator.validate(lu[5]));
        assertEquals(SnapshotValidatorValidCards.DUPLICATE_CARDS_IN_TABLE,  validator.validate(lu[6]));
        assertEquals(SnapshotValidatorValidCards.DUPLICATE_CARDS_IN_TABLE,  validator.validate(lu[7]));
        assertEquals(SnapshotValidatorValidCards.DUPLICATE_CARDS_IN_TABLE,  validator.validate(lu[8]));
        assertEquals(SnapshotValidatorValidCards.WRONG_AMOUNT_PUBLIC_CARDS, validator.validate(lu[9]));
        assertEquals(SnapshotValidatorValidCards.WRONG_AMOUNT_PUBLIC_CARDS, validator.validate(lu[10]));
        assertEquals(11, lu.length); // Make sure nothing is missed
    }
}