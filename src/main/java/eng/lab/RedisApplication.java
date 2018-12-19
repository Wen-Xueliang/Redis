package eng.lab;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RedisApplication {

	public static void main(String[] args) throws InterruptedException {

/**
 * 用Map来存储元数据。id作为key,整个消息结构序列化(json/…)之后作为value,放入元消息池中。
 将id放入其中(有N个)一个zset有序列表中,以createTime+delay+priority作为score。修改状态为正在延迟中
 使用timer实时监控zset有序列表中top 10的数据 。 如果数据score<=当前时间毫秒就取出来,根据topic重新放入一个新的可消费列表(list)中,在zset中删除已经取出来的数据,并修改状态为待消费
 客户端获取数据只需要从可消费队列中获取就可以了。并且状态必须为待消费 运行时间需要<=当前时间的 如果不满足 重新放入zset列表中,修改状态为正在延迟。如果满足修改状态为已消费。或者直接删除元数据。
 */

	}
}
