package cn.nomeatcoder.application;

import cn.nomeatcoder.common.ServerResponse;
import cn.nomeatcoder.common.query.UserQuery;
import cn.nomeatcoder.config.RedissonConfig;
import cn.nomeatcoder.dal.mapper.UserMapper;
import cn.nomeatcoder.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@TestPropertySource(
	locations = {"classpath:application-test.properties"}
)
@MapperScan(basePackages = {
	"cn.nomeatcoder.dal.mapper",
})
@Slf4j
public class AppTest {

	@Resource
	private UserMapper userMapper;

	@Resource
	private StringRedisTemplate stringRedisTemplate;

	@Resource
	private Redisson redisson;

	@Resource
	private UserService userService;

	@Test
	public void test() {
		System.out.println("success");
		log.info("-------------success--------------");
	}

	@Test
	public void testMapper(){
		System.out.println(userMapper);
		UserQuery query = new UserQuery();
		query.setPageSize(2L);
		System.out.println(userMapper.find(query));
	}

	@Test
	public void testRedis(){
		System.out.println(stringRedisTemplate);
		stringRedisTemplate.opsForValue().set("test","test");
		System.out.println(stringRedisTemplate.opsForValue().get("test"));
	}

	@Test
	public void testRedisson(){
		System.out.println(redisson);
		RLock testLock = redisson.getLock("testLock");
		testLock.lock(5, TimeUnit.SECONDS);
		testLock.unlock();
	}

	@Test
	public void testLoginSuccess() {
		String username = "admin";
		String password = "admin";
		ServerResponse response = userService.login(username, password);
		Assert.assertTrue(response.isSuccess());
	}

	@SpringBootApplication
	@Import(RedissonConfig.class)
	public static class Config{

	}
}
