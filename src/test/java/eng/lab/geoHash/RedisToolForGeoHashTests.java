package eng.lab.geoHash;

import com.alibaba.fastjson.JSON;
import eng.lab.common.CommonRedisTool;
import eng.lab.delayQueen.entity.Order;
import eng.lab.delayQueen.util.RedisToolForDelayQueen;
import eng.lab.geoHash.util.RedisToolForGeoHash;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.GeoRadiusResponse;
import redis.clients.jedis.Jedis;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by wenxueliang on 2018/12/18.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisToolForGeoHashTests extends CommonRedisTool {

    /**
     * 地图后台如何根据自己所在位置查询来查询附近餐馆的呢
     */
    @Test
    public void testQueryResturan() {

        initData();

        Jedis jedis = new Jedis(IP, PORT);
        List<GeoRadiusResponse> geoRadiusResponses = RedisToolForGeoHash.queryResturan(jedis, randomLonLat(113.46, 114.37), randomLonLat(22.27, 22.52), 10);
        printMap(geoRadiusResponses);
    }

    private void printMap(List<GeoRadiusResponse> geoRadiusResponses) {
        System.out.println("list:");
        for(GeoRadiusResponse geoRadiusResponse : geoRadiusResponses) {
            System.out.println("member:" + geoRadiusResponse.getMemberByString() + "; distance : " + geoRadiusResponse.getDistance());
        }
    }

    private void initData() {
        Jedis jedis = new Jedis(IP, PORT);

        final int RESTURANT_NUM = 50;

        jedis.del("shenzhen-resturant");
        for(int i = 0; i < RESTURANT_NUM; i ++) {
            jedis.geoadd("shenzhen-resturant", randomLonLat(113.46, 114.37), randomLonLat(22.27, 22.52), "resturant" + i);
        }
        jedis.close();
    }

    public double randomLonLat(double min, double max) {
        Random random = new Random();
        BigDecimal db = new BigDecimal(Math.random() * (max - min) + min);
        return db.setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

}
