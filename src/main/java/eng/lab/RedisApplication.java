package eng.lab;

import eng.lab.util.RedisTool;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import redis.clients.jedis.Jedis;

import java.util.UUID;
import java.util.concurrent.*;

@SpringBootApplication
public class RedisApplication {

	public static void main(String[] args) throws InterruptedException {

/**
 * 用Map来存储元数据。id作为key,整个消息结构序列化(json/…)之后作为value,放入元消息池中。
 将id放入其中(有N个)一个zset有序列表中,以createTime+delay+priority作为score。修改状态为正在延迟中
 使用timer实时监控zset有序列表中top 10的数据 。 如果数据score<=当前时间毫秒就取出来,根据topic重新放入一个新的可消费列表(list)中,在zset中删除已经取出来的数据,并修改状态为待消费
 客户端获取数据只需要从可消费队列中获取就可以了。并且状态必须为待消费 运行时间需要<=当前时间的 如果不满足 重新放入zset列表中,修改状态为正在延迟。如果满足修改状态为已消费。或者直接删除元数据。
 */

		Jedis jedis = new Jedis("", 1);

		jedis.ex

/*
		Jedis jedis = new Jedis("45.77.113.171", 6379);
		System.out.println(jedis.ping());

		ExecutorService executorService = Executors.newCachedThreadPool();
		//信号量，此处用于控制并发的线程数
		final Semaphore semaphore = new Semaphore(threadTotal);
		//闭锁，可实现计数器递减
		final CountDownLatch countDownLatch = new CountDownLatch(clientTotal);
		for (int i = 0; i < clientTotal ; i++) {
			executorService.execute(() -> {
				try {
					//执行此方法用于获取执行许可，当总计未释放的许可数不超过200时，
					//允许通行，否则线程阻塞等待，直到获取到许可。
					semaphore.acquire();
					System.out.println(":" + count++);
					//释放许可
					semaphore.release();
					System.out.println("====================================");
				} catch (Exception e) {
					//log.error("exception", e);
					e.printStackTrace();
				}
				//闭锁减一
				System.out.println("+++++++++++++++++++++++++");
				countDownLatch.countDown();
			});
			System.out.println("----------------------");
		}
		countDownLatch.await();//线程阻塞，直到闭锁值为0时，阻塞才释放，继续往下执行
		executorService.shutdown();
		System.out.println("count:{}" + count);*/


		//RedisTool.tryGetDistributedLock(jedis, "DLock", UUID.randomUUID().toString(), 10000);
	}

	public static void wrongGetLock1(Jedis jedis, String lockKey, String requestId, int expireTime) {

		Long result = jedis.setnx(lockKey, requestId);
		if (result == 1) {
			// 若在这里程序突然崩溃，则无法设置过期时间，将发生死锁
			jedis.expire(lockKey, expireTime);
		}

	}



	// 请求总数
	public static int clientTotal = 5000;

	// 同时并发执行的线程数
	public static int threadTotal = 200;

	public static int count = 0;
}
