package eng.lab.hyperLogLog;

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
import java.util.*;

/**
 * Created by wenxueliang on 2018/12/18.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisToolForHyperLogLogTests extends CommonRedisTool {

    /**
     * 在使用新闻客户端看新闻时，它会给我们不停地推荐新的内容，它每次推荐时要去重，去掉那些已经看过的内容
     */
    /**
     * 1000,100,1
     * 10000,1000,25
     * 50000,5000,558
     * 80000,8000,1169
     * 100000,10000,1478
     * 1000000,100000,479533ms
     */
    @Test
    public void testNormal() {
        List<String> allRead = new ArrayList<>();
        List<String> isRead = new ArrayList<>();
        List<String> noRead = new ArrayList<>();

        int isReadNum = 100000;
        int allReadNum = isReadNum*10;

        for(int i = 0; i < isReadNum; i ++) {
            allRead.add(String.valueOf(i));
            isRead.add(String.valueOf(i));
        }
        for(int i = isReadNum; i < allReadNum; i ++) {
            allRead.add(String.valueOf(i));
        }

        long start = System.currentTimeMillis();
        for(String record : allRead) {
            if(!isRead.contains(record)) {
                noRead.add(record);
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("need Time:" + (end - start) + "ms");
        /*System.out.println("allRead:" + allRead);
        System.out.println("isRead:" + isRead);
        System.out.println("noRead:" + noRead);*/
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
