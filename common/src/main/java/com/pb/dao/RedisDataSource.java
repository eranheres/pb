package com.pb.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *
 */
@Component
public class RedisDataSource implements PBDataSource {

    @Autowired
    private RedisTemplate<String, Snapshot> snapshotTemplate;

    @Autowired
    private RedisTemplate<String, GameOp> gameOpTemplate;

    @Value("${dao.redisdatasource.ttlmin}")
    private static Integer SNAPSHOT_TTL_MIN;
    @Value("${dao.redisdatasource.ttlmin}")
    private static Integer ACTION_TTL_MIN;


    private String snapshotKey(String id) {
        return "snapshot-"+id;
    }

    @Override
    public void saveSnapshotToList(String id, Snapshot value) {
        String key = snapshotKey(id);
        snapshotTemplate.opsForList().rightPush(key, value);
        if ((SNAPSHOT_TTL_MIN != null) && (SNAPSHOT_TTL_MIN != 0)) {
            snapshotTemplate.expire(key, SNAPSHOT_TTL_MIN, TimeUnit.MINUTES);
        }
    }

    @Override
    public void saveSnapshotToList(String id, String value) {
        Snapshot snapshot = (Snapshot) snapshotTemplate.getValueSerializer().deserialize(value.getBytes());
        saveSnapshotToList(id, snapshot);
    }

    @Override
    public List<Snapshot> getList(String id) {
        return snapshotTemplate.opsForList().range(snapshotKey(id), 0, Long.MAX_VALUE);
    }

    private String gameOpKey(String id, Integer turn) {
        return "gameop-"+id+"-"+turn.toString();
    }

    @Override
    public void saveGameOp(String id, Integer turn, GameOp op) {
        String key = gameOpKey(id, turn);
        gameOpTemplate.opsForValue().setIfAbsent(key, op);
        if (ACTION_TTL_MIN != null) {
            snapshotTemplate.expire(key, ACTION_TTL_MIN, TimeUnit.MINUTES);
        }
    }

    @Override
    public GameOp getGameOp(String id, Integer turn) {
        return gameOpTemplate.opsForValue().get(gameOpKey(id, turn));
    }
}
