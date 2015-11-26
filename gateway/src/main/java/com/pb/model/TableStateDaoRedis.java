package com.pb.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class TableStateDaoRedis implements TableStateDao {

    private StringRedisTemplate template;
    private static final int VALUE_TTL_MIN = 0;

    @Autowired
    public TableStateDaoRedis(StringRedisTemplate template) {
        this.template = template;
    }

    public void save(TableState state) {
        template.opsForList().leftPush(state.getId(),state.getData());
        if (VALUE_TTL_MIN != 0) {
            template.expire(state.getId(), VALUE_TTL_MIN, TimeUnit.MINUTES);
        }
    }
}
