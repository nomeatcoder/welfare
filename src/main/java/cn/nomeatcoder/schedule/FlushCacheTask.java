package cn.nomeatcoder.schedule;

import cn.nomeatcoder.common.Const;
import cn.nomeatcoder.common.MyCache;
import cn.nomeatcoder.common.vo.IndexVo;
import cn.nomeatcoder.service.CategoryService;
import cn.nomeatcoder.utils.GsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

import static cn.nomeatcoder.common.MyCache.FLUSH_INDEX_KEY;

@Slf4j
@Component
public class FlushCacheTask {

	@Resource
	private CategoryService categoryService;

	@Resource
	private Redisson redisson;

	@Resource
	private MyCache myCache;

	@Scheduled(cron="0 0/5 * * * ? ")
	public void flushIndexVo() throws InterruptedException {
		log.info("[flushIndexVo] begin");
		long begin = System.currentTimeMillis();
		RLock lock = redisson.getLock(FLUSH_INDEX_KEY);
		boolean hasLock = lock.tryLock(Const.REDIS_EXPIRE_TIME, TimeUnit.SECONDS);
		try {
			if (hasLock) {
				log.info("[flushIndexVo] 获取分布式锁成功, key:{}", FLUSH_INDEX_KEY);
				IndexVo indexVo = categoryService.getIndexVo();
				myCache.setKey(MyCache.INDEX_INFO_KEY, GsonUtils.toJson(indexVo));
			} else {
				log.info("[flushIndexVo] 获取分布式锁失败, key:{}", FLUSH_INDEX_KEY);
			}
		} catch (Exception e) {
			log.error("[flushIndexVo] throw exception", e);
		} finally {
			if (hasLock) {
				lock.unlock();
				log.info("[flushIndexVo] 分布式锁解锁, key:{}", FLUSH_INDEX_KEY);
			}
		}
		log.info("[flushIndexVo] end. cost time:{}ms", System.currentTimeMillis() - begin);
	}

}
