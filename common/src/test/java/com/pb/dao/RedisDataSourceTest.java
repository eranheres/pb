package com.pb.dao;

import com.pb.DaoApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(DaoApplication.class)
public class RedisDataSourceTest {

   // @Autowired
   // RedisDataSource dataSource;

    @Test
    // Integration tests that requires Redis server up (disabled by defalut)
    public void testRedisDataSource() {
        Snapshot saved = new Snapshot();
        /*
        saved.setState(new Snapshot.State("datat"));
        dataSource.saveToList("1", saved);
        List<Snapshot> restored = dataSource.getList("1");
        assertEquals(saved, restored);
        */
    }
}