package eng.lab.distributedLock;

import eng.lab.common.CommonRedisTool;
import eng.lab.distributedLock.util.RedisToolForDistributeLock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;

import java.util.*;

/**
 * Created by wenxueliang on 2018/12/18.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisToolForDistributedLockTests extends CommonRedisTool {

    @Test
    public void testDistributedLock() throws Exception {

        Jedis jedis = new Jedis(IP, PORT);
        String lockKey = "DLock";
        String requestId = UUID.randomUUID().toString();
        int expireTime = 1000000;

        if(!RedisToolForDistributeLock.tryGetDistributedLock(jedis, lockKey, requestId, expireTime)) {
            throw new Exception("get error!");
        }

        Thread.sleep(50000);

        if(!RedisToolForDistributeLock.releaseDistributedLock(jedis, lockKey, requestId)) {
            throw new Exception("release error!");
        }
    }

}
