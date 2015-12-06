package com.pb.dao;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.IOException;

/**
 * Deserialize snapshot from JSON string
 */
public class SnapshotJSONSerialize implements RedisSerializer<Snapshot> {

    @Override
    public byte[] serialize(Snapshot snapshot) throws SerializationException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        //mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            return mapper.writeValueAsBytes(snapshot);
        } catch (IOException e) {
            throw new SerializationException("Failed the serialize snapshot",e);
        }
    }

    @Override
    public Snapshot deserialize(byte[] bytes) throws SerializationException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        //mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            return mapper.readValue(bytes, Snapshot.class);
        } catch (IOException e) {
            throw new SerializationException("Failed the de-serialize snapshot",e);
        }
    }
}
