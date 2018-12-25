package eng.lab.geoHash.util;

import redis.clients.jedis.GeoRadiusResponse;
import redis.clients.jedis.GeoUnit;
import redis.clients.jedis.Jedis;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by wenxueliang on 2018/10/8.
 */
public class RedisToolForGeoHash {

    /**
     *
     * @param jedis
     */
    public static List<GeoRadiusResponse> queryResturan(Jedis jedis, double longitude, double latitude) {
        List<GeoRadiusResponse> shenzhen = jedis.georadius("shenzhen", longitude, latitude, 1000, GeoUnit.KM);
        return shenzhen;

    }
}