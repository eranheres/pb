package com.pb;

import com.pb.dao.Snapshot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

@SpringBootApplication
public class DaoApplication {

    @Bean
    @Primary
    RedisTemplate<String, Snapshot> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Snapshot> template = new RedisTemplate<String, Snapshot>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(template.getStringSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<Snapshot>(Snapshot.class));
        return template;
    }

    public static void main(String[] args) {
        SpringApplication.run(DaoApplication.class, args);
    }
}
