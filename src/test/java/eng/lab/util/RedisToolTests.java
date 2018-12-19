package eng.lab.util;

import com.alibaba.fastjson.JSON;
import eng.lab.entity.Order;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by wenxueliang on 2018/12/18.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisToolTests {

    public final static String IP = "172.29.1.122";
    public final static int PORT = 6379;

    @Before
    public void pingRedis() throws Exception {

        Jedis jedis = new Jedis(IP, PORT);
        if(!"PONG".equals(jedis.ping())) {
            throw new Exception("ping error!");
        }
    }

    /**
    互斥性。在任意时刻，只有一个客户端能持有锁。
    不会发生死锁。即使有一个客户端在持有锁的期间崩溃而没有主动解锁，也能保证后续其他客户端能加锁。
    具有容错性。只要大部分的Redis节点正常运行，客户端就可以加锁和解锁。
    解铃还须系铃人。加锁和解锁必须是同一个客户端，客户端自己不能把别人加的锁给解了。
     */
    @Test
    public void testDistributedLock() throws Exception {

        Jedis jedis = new Jedis(IP, PORT);
        String lockKey = "DLock";
        String requestId = UUID.randomUUID().toString();
        int expireTime = 1000000;

        if(!RedisTool.tryGetDistributedLock(jedis, lockKey, requestId, expireTime)) {
            throw new Exception("get error!");
        }

        Thread.sleep(50000);

        if(!RedisTool.releaseDistributedLock(jedis, lockKey, requestId)) {
            throw new Exception("release error!");
        }
    }

    @Test
    public void testDelayQueen() throws Exception {

        Jedis jedis = new Jedis(IP, PORT);
        for(int i = 0; i < 100; i++) {
            Order order = new Order("id" + i, "user" + i, "trade" + i, RedisTool.randomDate("2018-10-20", "2018-11-20"));
            jedis.set("id" + i, JSON.toJSONString(order));
            jedis.zadd("delayQueen", Double.valueOf(String.valueOf(order.getCreateDate().getTime())).doubleValue(), order.getId());
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date start = format.parse("2018-10-15");
        jedis.set("excDate", String.valueOf(start.getTime()));
        jedis.close();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {

                Jedis jedis1 = new Jedis(IP, PORT);
                Date nextDate = getNextDate(jedis1.get("excDate"));
                jedis1.set("excDate", String.valueOf(nextDate.getTime()));
                RedisTool.pushQueenIfTime(jedis1, nextDate);
                jedis1.close();
            }
        }, 0, 10000);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {

                Jedis jedis2 = new Jedis(IP, PORT);
                Order order = RedisTool.popQueen(jedis2);
                if(null != order) {
                    RedisTool.doNotify(order);
                }
                jedis2.close();
            }
        }, 0, 1000);

        while(true) {}
    }

    public Date getNextDate(String excDate) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(Long.valueOf(excDate)));
        c.add(Calendar.DAY_OF_MONTH, 1);
        return c.getTime();
    }

}
