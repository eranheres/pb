package com.pb.model;


import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PbValidatorInitializerTest {

    static PbValidator validator = PbValidatorInitializer.init();

    @Test
    public void testInit() throws Exception {
        assertNotNull(validator);
    }

    @SuppressWarnings("Duplicates")
    @Test
    public void testBasicValidation() throws Exception {
        PbTableData data;
        data = new PbTableData();
        data.getCards().add(0, "As");
        data.getCards().add(1, "As");
        assertEquals(validator.validateAll(null, data), "Hand cards are equals");

        data = new PbTableData();
        data.getCards().add(0, "AS");
        data.getCards().add(1, "AS");
        assertEquals(validator.validateAll(null, data), "Invalid card number or suit");

        data = new PbTableData();
        data.getCards().add(0, "As");
        assertEquals(validator.validateAll(null, data), "Hand must have two cards");
    }
}