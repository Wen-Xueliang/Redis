package eng.lab.scan;

import com.alibaba.fastjson.JSON;
import eng.lab.common.CommonRedisTool;
import eng.lab.delayQueen.entity.Order;
import eng.lab.delayQueen.util.RedisToolForDelayQueen;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.Tuple;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wenxueliang on 2018/12/18.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisToolForScanTests extends CommonRedisTool {

    @Test
    public void doScan() throws InterruptedException {

        Jedis jedis = new Jedis(IP, PORT);
        initData();

        ScanResult<String> scan = jedis.scan("0", new ScanParams().count(1));

        String cursor = scan.getStringCursor();
        do {
            System.out.println(scan.getResult());
            scan = jedis.scan(cursor, new ScanParams().count(1));
            cursor = scan.getStringCursor();
        } while(!"0".equals(cursor));
    }

    @Test
    public void doSScan() throws InterruptedException {

        Jedis jedis = new Jedis(IP, PORT);
        initDataS();

        ScanResult<String> scan = jedis.sscan("sScan","0");
        String cursor = scan.getStringCursor();
        do {
            System.out.println(scan.getResult());
            scan = jedis.sscan("sScan", cursor, new ScanParams().count(1));
            cursor = scan.getStringCursor();
        } while(!"0".equals(cursor));
    }

    @Test
    public void doHScan() throws InterruptedException {

        Jedis jedis = new Jedis(IP, PORT);
        initDataH();

        ScanResult<Map.Entry<String, String>> scan = jedis.hscan("hScan", "0");
        String cursor = scan.getStringCursor();
        do {
            System.out.println(scan.getResult());
            scan = jedis.hscan("hScan", cursor, new ScanParams().count(1));
            cursor = scan.getStringCursor();
        } while(!"0".equals(cursor));
    }

    @Test
    public void doZScan() throws InterruptedException {

        Jedis jedis = new Jedis(IP, PORT);
        initDataZ();

        ScanResult<Tuple> scan = jedis.zscan("zScan", "0");
        String cursor = scan.getStringCursor();
        do {
            System.out.println(scan.getResult());
            scan = jedis.zscan("zScan", cursor, new ScanParams().count(1));
            cursor = scan.getStringCursor();
        } while(!"0".equals(cursor));
    }

    private void initData() {
        Jedis jedis = new Jedis(IP, PORT);
        for(int i = 0; i < 10; i ++) {
            jedis.set("scan : " + i, i + "");
        }
        jedis.close();
    }

    private void initDataS() {
        Jedis jedis = new Jedis(IP, PORT);
        jedis.del("sScan");
        for(int i = 0; i < 10; i ++) {
            jedis.sadd("sScan", i + "");
        }
        jedis.close();
    }

    private void initDataH() {
        Jedis jedis = new Jedis(IP, PORT);
        jedis.del("hScan");
        for(int i = 0; i < 10; i ++) {
            jedis.hset("hScan", "key" + i, "value" + i);
        }
        jedis.close();
    }

    private void initDataZ() {
        Jedis jedis = new Jedis(IP, PORT);
        jedis.del("zScan");
        for(int i = 0; i < 10; i ++) {
            jedis.zadd("zScan", i, "value" + i);
        }
        jedis.close();
    }
}
