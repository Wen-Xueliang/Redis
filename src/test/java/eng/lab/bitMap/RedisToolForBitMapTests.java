package eng.lab.bitMap;

import eng.lab.common.CommonRedisTool;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;

import java.text.SimpleDateFormat;
import java.util.BitSet;
import java.util.Date;

/**
 * Created by wenxueliang on 2018/12/18.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisToolForBitMapTests extends CommonRedisTool {

    /**
     * 统计某一天有多少个用户至少听了app中的一首歌
     */
    @Test
    public void testBitMap() throws Exception {
        Jedis jedis = new Jedis(IP, PORT);

        long initUserNum = 100;
        final int songNum = 10;
        final int dayNum = 5;

        /*SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date start = format.parse("2018-10-15");
        jedis.set("excDate", String.valueOf(start.getTime()));

        for(int day = 1; day < dayNum; day++) {
            Date nextDate = getNextDate(jedis.get("excDate"));
            jedis.set("excDate", String.valueOf(nextDate.getTime()));
            long userNum = initUserNum * day;
            for (int song = 1; song < songNum; song++) {
                for(long user = 1; user < userNum; user++) {
                    long userId = (long)(Math.random() * userNum);
                    //System.out.println("day:" + format.format(nextDate)+";song:" + song + "userid :" + userId);
                    jedis.setbit("song"+song+":"+format.format(nextDate), userId, true);
                }
            }
        }*/
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date start = format.parse("2018-10-15");
        jedis.set("excDate", String.valueOf(start.getTime()));
        BitSet all = new BitSet();
        for(int day = 1; day < dayNum; day++) {
            BitSet daySet = new BitSet();
            Date nextDate = getNextDate(jedis.get("excDate"));
            jedis.set("excDate", String.valueOf(nextDate.getTime()));
            long userNum = initUserNum * day;
            for (int song = 1; song < songNum; song++) {

                byte[] loginByte = jedis.get(("song"+song+":"+format.format(nextDate)).getBytes());
                if(null != loginByte) {
                    BitSet bitSet = BitSet.valueOf(loginByte);
                    all.or(bitSet);
                    daySet.or(bitSet);
                }
            }
            System.out.println("day:" + format.format(nextDate)+";count:" + daySet.cardinality());
        }

        jedis.set("excDate", String.valueOf(start.getTime()));
        for (int song = 1; song < songNum; song++) {
            BitSet songSet = new BitSet();
            jedis.set("excDate", String.valueOf(start.getTime()));
            for(int day = 1; day < dayNum; day++) {
                Date nextDate = getNextDate(jedis.get("excDate"));
                jedis.set("excDate", String.valueOf(nextDate.getTime()));
                long userNum = initUserNum * day;
                byte[] loginByte = jedis.get(("song"+song+":"+format.format(nextDate)).getBytes());
                if(null != loginByte) {
                    BitSet bitSet = BitSet.valueOf(loginByte);
                    songSet.or(bitSet);
                }
            }
            System.out.println("song:" + song+";count:" + songSet.cardinality());
        }

        System.out.println(all.cardinality());


    }

}
