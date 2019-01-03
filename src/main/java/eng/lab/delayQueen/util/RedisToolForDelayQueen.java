package eng.lab.delayQueen.util;

import com.alibaba.fastjson.JSON;
import eng.lab.delayQueen.entity.Order;
import redis.clients.jedis.Jedis;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

/**
 * Created by wenxueliang on 2018/10/8.
 */
public class RedisToolForDelayQueen {

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

    public static void doNotify(Order order) {
        System.out.println("===notify:" + order.getId() + ", order Time:" + order.getCreateDate());
    }

    public static Date randomDate(String beginDate, String endDate){
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date start = format.parse(beginDate);//构造开始日期 
            Date end = format.parse(endDate);//构造结束日期 
            //getTime()表示返回自 1970 年 1 月 1 日 00:00:00 GMT 以来此 Date 对象表示的毫秒数。 
            if(start.getTime() >= end.getTime()){
                return null;
            }
            long date = random(start.getTime(),end.getTime());
            return new Date(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static long random(long begin,long end){
        long rtn = begin + (long)(Math.random() * (end - begin));
        //如果返回的是开始时间和结束时间，则递归调用本函数查找随机值 
        if(rtn == begin || rtn == end){
            return random(begin,end);
        }
        return rtn;
    }
}