package eng.lab.geoHash.util;

import redis.clients.jedis.GeoRadiusResponse;
import redis.clients.jedis.GeoUnit;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.geo.GeoRadiusParam;

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
    public static List<GeoRadiusResponse> queryResturan(Jedis jedis, double longitude, double latitude, double radius) {
        List<GeoRadiusResponse> shenzhen = jedis.georadius("shenzhen-resturant", longitude, latitude, radius, GeoUnit.KM, GeoRadiusParam.geoRadiusParam().withDist());
        return shenzhen;
    }
}