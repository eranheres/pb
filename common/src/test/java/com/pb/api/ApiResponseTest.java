package com.pb.api;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.omg.CORBA.ObjectHelper;

import static org.junit.Assert.*;

public class ApiResponseTest {

    @Test
    public void testToString() throws Exception {
        ApiResponse apires = new ApiResponse(true, "a", "b");

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        ApiResponse ret = mapper.readValue(apires.toString(), ApiResponse.class);

        assertEquals(apires, ret);
    }
}