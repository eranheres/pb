package com.pb.dao;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class CardTest {

    @Test
    public void testToString() throws Exception {
        assertEquals(new Card("As").toString(), "As");
        assertEquals(new Card("2d").toString(), "2d");
        assertEquals(new Card("Td").toString(), "Td");
        assertEquals(new Card("Jh").toString(), "Jh");
        assertEquals(new Card("Kd").toString(), "Kd");
        assertEquals(new Card("Qd").toString(), "Qd");
        assertEquals(new Card("9c").toString(), "9c");
        assertTrue(!new Card("9c").getEmpty());
        assertEquals(new Card("").toString(), "--");
        assertTrue(new Card("").getEmpty());
        assertEquals(new Card("--").toString(), "--");
        assertTrue(new Card("--").getEmpty());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testInvalidConstruction1() { new Card("0d"); }

    @Test(expectedExceptions = Exception.class)
    public void testInvalidConstruction2() {
        new Card("3f");
    }

    @Test(expectedExceptions = Exception.class)
    public void testInvalidConstruction3() { new Card("Gd"); }

    @Test(expectedExceptions = Exception.class)
    public void testInvalidConstruction5() { new Card("Ts0"); }

    @Test
    public void testJsnoSerialize() throws JsonProcessingException {
        Card card = new Card("As");

        ObjectMapper mapper = new ObjectMapper();
        String str = mapper.writeValueAsString(card);
        assertEquals(str, "{\"number\":14,\"suit\":\"Spade\",\"empty\":false}");
    }
}
