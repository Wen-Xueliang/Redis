package eng.lab.common;

import org.junit.Before;
import redis.clients.jedis.Jedis;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by wenxueliang on 2018/12/20.
 */
public class CommonRedisTool {


    public final static String IP = "172.29.1.122";
    public final static int PORT = 6379;

    @Before
    public void pingRedis() throws Exception {

        Jedis jedis = new Jedis(IP, PORT);
        if(!"PONG".equals(jedis.ping())) {
            throw new Exception("ping error!");
        }
    }

    public Date getNextDate(String excDate) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(Long.valueOf(excDate)));
        c.add(Calendar.DAY_OF_MONTH, 1);
        return c.getTime();
    }
}
