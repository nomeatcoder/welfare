package cn.nomeatcoder.schedule;

import cn.nomeatcoder.common.Const;
import cn.nomeatcoder.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class CloseOrderTask {

	private static final String CLOSE_ORDER_KEY = "welfare_close_order";

	@Resource
	private OrderService orderService;

	@Resource
	private Redisson redisson;

	@Scheduled(cron = "0 */1 * * * ?")
	public void closeOrder() throws InterruptedException {
		log.info("[closeOrder] begin");
		long begin = System.currentTimeMillis();
		RLock lock = redisson.getLock(CLOSE_ORDER_KEY);
		boolean hasLock = lock.tryLock(Const.REDIS_EXPIRE_TIME, TimeUnit.SECONDS);
		try {
			if (hasLock) {
				log.info("[closeOrder] 获取分布式锁成功, key:{}", CLOSE_ORDER_KEY);
				orderService.closeOrder(Const.CLOSE_ORDER_TIME);
			} else {
				log.info("[closeOrder] 获取分布式锁失败, key:{}", CLOSE_ORDER_KEY);
			}
		} catch (Exception e) {
			log.error("[closeOrder] throw exception", e);
		} finally {
			if (hasLock) {
				lock.unlock();
				log.info("[closeOrder] 分布式锁解锁, key:{}", CLOSE_ORDER_KEY);
			}
		}
		log.info("[closeOrder] end. cost time:{}ms", System.currentTimeMillis() - begin);
	}

}
