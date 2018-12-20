package eng.lab.common;

import org.junit.Before;
import redis.clients.jedis.Jedis;

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
}
