package com.pb.dao;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Created by eranh on 11/29/15.
 */
public class SnapshotJSONSerialize implements SnapshotSerialize {
    @Override
    public Snapshot fromString(String string) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        //mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(string, Snapshot.class);
    }
}
