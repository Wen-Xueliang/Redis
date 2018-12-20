package eng.lab.bitMap.util;

import com.alibaba.fastjson.JSON;
import eng.lab.bitMap.entity.Order;
import redis.clients.jedis.Jedis;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

/**
 * Created by wenxueliang on 2018/10/8.
 */
public class RedisToolForBitMap {

    public static void pushQueenIfTime(Jedis jedis, Date excDate) {
        Set<String> set = jedis.zrangeByScore("delayQueen", "1539619200000", String.valueOf(excDate.getTime()));
        System.out.println(set);
        jedis.zremrangeByScore("delayQueen", "1539619200000", String.valueOf(excDate.getTime()));
        for(String str : set) {
            jedis.lpush("sendQueen", str);
        }
    }

    public static Order popQueen(Jedis jedis) {
        int sendQueen = Integer.parseInt(String.valueOf(jedis.llen("sendQueen")));
        if(sendQueen != 0) {
            String popId = jedis.lpop("sendQueen");
            String orderJson = jedis.get(popId);
            Order order = JSON.parseObject(orderJson, Order.class);
            return order;
        }
        return null;
    }
}