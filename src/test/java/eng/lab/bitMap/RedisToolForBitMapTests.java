package eng.lab.bitMap;

import eng.lab.common.CommonRedisTool;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;

/**
 * Created by wenxueliang on 2018/12/18.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisToolForBitMapTests extends CommonRedisTool {

    /**
     * 互斥性。在任意时刻，只有一个客户端能持有锁。
     * 不会发生死锁。即使有一个客户端在持有锁的期间崩溃而没有主动解锁，也能保证后续其他客户端能加锁。
     * 具有容错性。只要大部分的Redis节点正常运行，客户端就可以加锁和解锁。
     * 解铃还须系铃人。加锁和解锁必须是同一个客户端，客户端自己不能把别人加的锁给解了。
     */
    @Test
    public void testDistributedLock() throws Exception {
        Jedis jedis = new Jedis(IP, PORT);
    }

}
