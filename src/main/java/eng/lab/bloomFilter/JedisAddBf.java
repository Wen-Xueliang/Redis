package eng.lab.bloomFilter;

import redis.clients.jedis.Client;
import redis.clients.jedis.Jedis;

/**
 * Created by wenxueliang on 2018/12/27.
 */
public class JedisAddBf extends Jedis {
    protected ClientAddBf clientAddBf = null;

    public JedisAddBf(String host, int port) {
        clientAddBf = new ClientAddBf(host, port);
    }

    public String bfAdd(String key, String value) {
        checkIsInMultiOrPipeline();
        clientAddBf.bfAdd(key, value);
        return clientAddBf.getStatusCodeReply();
    }

}
