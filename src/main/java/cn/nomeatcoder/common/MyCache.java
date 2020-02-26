package cn.nomeatcoder.common;

import cn.nomeatcoder.common.error.BizException;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
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

	public static final int REDIS_EXPIRE_TIME = 60 * 60;

	private Cache<String, String> localCache = CacheBuilder.newBuilder()
		.initialCapacity(1000)
		.maximumSize(10000)
		.expireAfterAccess(1, TimeUnit.HOURS)
		.build();

	public void setKey(String key, String value) {
		try {
			stringRedisTemplate.opsForValue().set(key, value, REDIS_EXPIRE_TIME, TimeUnit.SECONDS);
		} catch (Exception e) {
			log.error("写入redis失败", e);
			throw new BizException("服务异常");
		}
		localCache.put(key, value);
	}

	public String getKey(String key) {
		String value = localCache.getIfPresent(key);
		if (value == null) {
			try {
				value = stringRedisTemplate.opsForValue().get(key);
			} catch (Exception e) {
				log.error("查询redis失败", e);
				throw new BizException("服务异常");
			}
			if (value != null) {
				localCache.put(key, value);
			}
		}
		return value;
	}
}

