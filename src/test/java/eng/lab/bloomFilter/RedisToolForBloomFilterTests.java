package eng.lab.bloomFilter;

import com.alibaba.fastjson.JSON;
import eng.lab.common.CommonRedisTool;
import eng.lab.delayQueen.entity.Order;
import eng.lab.delayQueen.util.RedisToolForDelayQueen;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wenxueliang on 2018/12/18.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisToolForBloomFilterTests extends CommonRedisTool {

    /**
     * 1天后自动通知
     * @throws Exception
     */
    @Test
    public void testBloomFilter() throws Exception {
        initData();
    }

    public void initData() {
        JedisAddBf jedis = new JedisAddBf(IP, PORT);
        jedis.bfAdd("2", "2");
    }
}
