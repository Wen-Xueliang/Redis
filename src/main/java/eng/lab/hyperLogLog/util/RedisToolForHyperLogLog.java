package eng.lab.hyperLogLog.util;

import redis.clients.jedis.Jedis;

import java.util.Date;
import java.util.Set;

/**
 * Created by wenxueliang on 2018/10/8.
 */
public class RedisToolForHyperLogLog {

    public static void pushQueenIfTime(Jedis jedis, Date excDate) {
        Set<String> set = jedis.zrangeByScore("delayQueen", "1539619200000", String.valueOf(excDate.getTime()));
        System.out.println(set);
        jedis.zremrangeByScore("delayQueen", "1539619200000", String.valueOf(excDate.getTime()));
        for(String str : set) {
            jedis.lpush("sendQueen", str);
        }
    }


}