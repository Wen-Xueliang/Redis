package eng.lab.delayQueen;

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
public class RedisToolForDelayQueenTests extends CommonRedisTool {

    /**
     * 用Map来存储元数据。id作为key,整个消息结构序列化(json/…)之后作为value,放入元消息池中。
     * 将id放入其中(有N个)一个zset有序列表中,以createTime+delay+priority作为score。修改状态为正在延迟中
     * 使用timer实时监控zset有序列表中top 10的数据 。 如果数据score<=当前时间毫秒就取出来,根据topic重新放入一个新的可消费列表(list)中,在zset中删除已经取出来的数据,并修改状态为待消费
     * 客户端获取数据只需要从可消费队列中获取就可以了。并且状态必须为待消费 运行时间需要<=当前时间的 如果不满足 重新放入zset列表中,修改状态为正在延迟。如果满足修改状态为已消费。或者直接删除元数据。
     */

    /**
     *
     * @throws Exception
     */
    @Test
    public void testDelayQueen() throws Exception {

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

    public Date getNextDate(String excDate) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(Long.valueOf(excDate)));
        c.add(Calendar.DAY_OF_MONTH, 1);
        return c.getTime();
    }

}
