package cn.nomeatcoder.schedule;

import cn.nomeatcoder.common.MyCache;
import cn.nomeatcoder.common.vo.IndexVo;
import cn.nomeatcoder.service.CategoryService;
import cn.nomeatcoder.service.ProductService;
import cn.nomeatcoder.utils.GsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class FlushCacheTask {

	@Resource
	private CategoryService categoryService;

	@Resource
	private MyCache myCache;

	@Scheduled(cron="0 0/5 * * * ? ")
	public void flushIndexVo() {
		log.info("[flushIndexVo] begin");
		long begin = System.currentTimeMillis();
		IndexVo indexVo = categoryService.getIndexVo();
		myCache.setKey(MyCache.INDEX_INFO_KEY, GsonUtils.toJson(indexVo));
		log.info("[flushIndexVo] end. cost time:{}ms", System.currentTimeMillis() - begin);
	}

}
