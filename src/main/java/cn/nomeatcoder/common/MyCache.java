package cn.nomeatcoder.common;

import cn.nomeatcoder.common.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class MyCache {

	@Resource
	private StringRedisTemplate stringRedisTemplate;

	public static final String TOKEN_PREFIX = "token_";

	public static final String INDEX_INFO_KEY = "welfare_index_info";

	public static final String PRODUCT_DETAIL_KEY = "welfare_product_detail_%s";

	public static final String CREATE_ORDER_KEY = "welfare_create_order_lock_%s";

	public static final String CLOSE_ORDER_KEY = "welfare_close_order_lock_%s";

	public static final String FLUSH_INDEX_KEY = "welfare_close_order_lock_%s";

	public static final int REDIS_EXPIRE_TIME = 60 * 60;

	public void setKey(String key, String value) {
		try {
			stringRedisTemplate.opsForValue().set(key, value, REDIS_EXPIRE_TIME, TimeUnit.SECONDS);
		} catch (Exception e) {
			log.error("写入redis失败, key{}, value:{}", key, value);
			log.error("写入redis失败", e);
			throw new BizException("服务异常");
		}
	}

	public String getKey(String key) {
		try {
			String value = stringRedisTemplate.opsForValue().get(key);
			return value;
		} catch (Exception e) {
			log.error("查询redis失败, key:{}", key);
			log.error("查询redis失败", e);
			throw new BizException("服务异常");
		}
	}
}

