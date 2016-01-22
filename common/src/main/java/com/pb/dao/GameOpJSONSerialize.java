package com.pb.dao;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.IOException;

/**
 * Deserialize snapshot from JSON string
 */
public class GameOpJSONSerialize implements RedisSerializer<GameOp> {

    @Override
    public byte[] serialize(GameOp gameOp) throws SerializationException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        //mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            return mapper.writeValueAsBytes(gameOp);
        } catch (IOException e) {
            throw new SerializationException("Failed the serialize snapshot",e);
        }
    }

    @Override
    public GameOp deserialize(byte[] bytes) throws SerializationException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        //mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            return mapper.readValue(bytes, GameOp.class);
        } catch (IOException e) {
            throw new SerializationException("Failed the de-serialize snapshot",e);
        }
    }
}
