package eng.lab.simpleLimit;

import com.alibaba.fastjson.JSON;
import eng.lab.common.CommonRedisTool;
import eng.lab.delayQueen.entity.Order;
import eng.lab.delayQueen.util.RedisToolForDelayQueen;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalUnit;
import java.util.*;

/**
 * Created by wenxueliang on 2018/12/18.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisToolForSimpleLimitTests extends CommonRedisTool {

    /**
     * 某个用户在10秒内只能回复3次，那么利用Redis如何实现呢。
     */
    @Test
    public void doComment() throws InterruptedException {

        Jedis jedis = new Jedis(IP, PORT);
        int userId = 1;
        int limit = 3;
        //String time = String.valueOf(System.currentTimeMillis());
        //jedis.zadd(userId + "-Reply", Double.valueOf(time), time);
        //Instant lastCommentTime = Instant.now();
        for(int i = 0; i <= limit; i ++) {
            if (canCommentForRedis(jedis, userId, limit)) {
                String time = String.valueOf(System.currentTimeMillis());
                jedis.zadd(userId + "-Reply", Double.valueOf(time), time);
                Thread.sleep(1000L);
                System.out.println("comment success");
            } else {
                System.out.println("limit 10s only " + limit + " comment");
            }
        }
    }

    private boolean canCommentForRedis(Jedis jedis, int userId, int limit) {
        long nowTime = System.currentTimeMillis();
        long beforeTime = nowTime - (10*1000);
        jedis.zremrangeByScore(userId + "-Reply", 0, Double.valueOf(String.valueOf(beforeTime)));
        if(jedis.zcount(userId + "-Reply", 0, Double.valueOf(String.valueOf(nowTime))) < limit) {
            return true;
        }
        return false;
    }

    private boolean canComment(int userId,Instant lastCommentTime) {

        Instant nowTime = Instant.now();

        long seconds = Duration.between(lastCommentTime, nowTime).getSeconds();
        if(seconds <= 10) {
            return false;
        }
        return true;
    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void testHyperLogLog() throws Exception {

        Jedis jedis = new Jedis(IP, PORT);
        for(int i = 0; i < 100; i++) {
            Order order = new Order("id" + i, "user" + i, "trade" + i, RedisToolForDelayQueen.randomDate("2018-10-20", "2018-11-20"));
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
                RedisToolForDelayQueen.pushQueenIfTime(jedis1, nextDate);
                jedis1.close();
            }
        }, 0, 10000);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {

                Jedis jedis2 = new Jedis(IP, PORT);
                Order order = RedisToolForDelayQueen.popQueen(jedis2);
                if(null != order) {
                    RedisToolForDelayQueen.doNotify(order);
                }
                jedis2.close();
            }
        }, 0, 1000);

        while(true) {}
    }

}
