package com.pb.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *
 */
@Component
public class RedisDataSource implements PBDataSource {

    @Autowired
    private RedisTemplate<String, Snapshot> redisTemplate;

    @Value("${dao.redisdatasource.ttlmin}")
    private static Integer VALUE_TTL_MIN;

    @Override
    public void saveToList(String id, Snapshot value) {
        redisTemplate.opsForList().rightPush(id, value);
        if ((VALUE_TTL_MIN != null) && (VALUE_TTL_MIN != 0)) {
            redisTemplate.expire(id, VALUE_TTL_MIN, TimeUnit.MINUTES);
        }
    }

    @Override
    public void saveToList(String id, String value) {
        Snapshot snapshot = (Snapshot) redisTemplate.getValueSerializer().deserialize(value.getBytes());
        saveToList(id, snapshot);
    }

    @Override
    public List<Snapshot> getList(String id) {
        return redisTemplate.opsForList().range(id, 0, Long.MAX_VALUE);
    }
}
