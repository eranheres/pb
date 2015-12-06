package com.pb.api;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

import java.io.IOException;

/**
 * Represents a response
 */
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Getter
public class ApiResponse {
    Boolean success;
    String type;
    String description;

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        //mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "{ 'success': false, 'type' : 'fatal', 'description': 'failed to create response' }";
        }
    }

    public static ApiResponse fromString(String str) {
        ObjectMapper mapper = new ObjectMapper();
        //mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            return mapper.readValue(str, ApiResponse.class);
        } catch (IOException e) {
            return new ApiResponse(false, "API", "failed to parse response:"+str);
        }
    }

}
