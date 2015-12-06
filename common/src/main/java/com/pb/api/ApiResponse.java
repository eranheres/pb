package com.pb.api;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

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
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        //mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "{ 'success': false, 'type' : 'fatal', 'description': 'failed to create response' }";
        }
    }
}
