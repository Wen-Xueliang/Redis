package eng.lab.bitMap;

import eng.lab.common.CommonRedisTool;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;

import java.text.ParseException;
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

        long initUserNum = 100;
        final int songNum = 10;
        final int dayNum = 5;

        initData(dayNum, initUserNum, songNum);

        printByDay(dayNum, songNum);

        printBySong(dayNum, songNum);

    }

    public void initData(int dayNum, long initUserNum, int songNum) throws ParseException {

        Jedis jedis = new Jedis(IP, PORT);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date start = format.parse("2018-10-15");
        jedis.set("excDate", String.valueOf(start.getTime()));

        for(int day = 1; day < dayNum; day++) {
            Date nextDate = getNextDate(jedis.get("excDate"));
            jedis.set("excDate", String.valueOf(nextDate.getTime()));
            long userNum = initUserNum * day;
            for (int song = 1; song < songNum; song++) {
                for(long user = 1; user < userNum; user++) { //在第day天，song歌被听了userNum次
                    long userId = (long)(Math.random() * userNum);
                    //System.out.println("day:" + format.format(nextDate)+";song:" + song + "userid :" + userId);
                    jedis.setbit("song"+song+":"+format.format(nextDate), userId, true);
                }
            }
        }
        jedis.close();
    }

    public void printByDay(int dayNum, int songNum) throws ParseException {

        Jedis jedis = new Jedis(IP, PORT);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date start = format.parse("2018-10-15");
        jedis.set("excDate", String.valueOf(start.getTime()));
        BitSet all = new BitSet();
        for(int day = 1; day < dayNum; day++) {
            BitSet daySet = new BitSet();
            Date nextDate = getNextDate(jedis.get("excDate"));
            jedis.set("excDate", String.valueOf(nextDate.getTime()));
            for (int song = 1; song < songNum; song++) { //获取第day天song歌的统计
                byte[] loginByte = jedis.get(("song"+song+":"+format.format(nextDate)).getBytes());
                if(null != loginByte) {
                    BitSet bitSet = BitSet.valueOf(loginByte);
                    all.or(bitSet);
                    daySet.or(bitSet);
                }
            }
            System.out.println("day:" + format.format(nextDate)+";count:" + daySet.cardinality());
        }
        jedis.close();
    }

    public void printBySong(int dayNum, int songNum) throws ParseException {

        Jedis jedis = new Jedis(IP, PORT);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date start = format.parse("2018-10-15");
        jedis.set("excDate", String.valueOf(start.getTime()));
        for (int song = 1; song < songNum; song++) {
            BitSet songSet = new BitSet();
            jedis.set("excDate", String.valueOf(start.getTime()));
            for(int day = 1; day < dayNum; day++) {
                Date nextDate = getNextDate(jedis.get("excDate"));
                jedis.set("excDate", String.valueOf(nextDate.getTime()));
                byte[] loginByte = jedis.get(("song"+song+":"+format.format(nextDate)).getBytes());
                if(null != loginByte) {
                    BitSet bitSet = BitSet.valueOf(loginByte);
                    songSet.or(bitSet);
                }
            }
            System.out.println("song:" + song+";count:" + songSet.cardinality());
        }
        jedis.close();
    }

}
