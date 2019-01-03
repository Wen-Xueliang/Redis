package eng.lab.bloomFilter;

import redis.clients.util.SafeEncoder;

/**
 * Created by wenxueliang on 2018/12/27.
 */
public enum CommandAddBf {
    BF_ADD;

    public final byte[] raw;

    CommandAddBf() {
        raw = SafeEncoder.encode(this.name().replace("_", "."));
    }
}
