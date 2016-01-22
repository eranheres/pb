package com.pb;

import com.pb.dao.GameOp;
import com.pb.dao.GameOpJSONSerialize;
import com.pb.dao.Snapshot;
import com.pb.dao.SnapshotJSONSerialize;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Random;

@SuppressWarnings("unchecked")
@SpringBootApplication
public class MonoappApplication {

    public static void main(String[] args) {
        SpringApplication.run(MonoappApplication.class, args);
    }

    @Bean
    public Random randomizer() {
        return new Random();
    }

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory();
    }

    @Bean
    public RedisTemplate<String, Snapshot> snapshotTemplate() {
        RedisTemplate template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new SnapshotJSONSerialize());
        return template;
    }

    @Bean
    public RedisTemplate<String, GameOp> gameOpTemplate() {
        RedisTemplate template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GameOpJSONSerialize());
        return template;
    }
}
