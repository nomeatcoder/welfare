package cn.nomeatcoder.application;

import cn.nomeatcoder.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import tk.mybatis.spring.annotation.MapperScan;

import javax.annotation.Resource;

/**
 * Hello world!
 */
@RunWith(SpringRunner.class)
@TestPropertySource(
	locations = {"classpath:application-test.properties"}
)
@MapperScan(basePackages = {
	"cn.nomeatcoder.mapper"
})
@Slf4j
public class AppTest {

	@Resource
	private UserMapper userMapper;

	@Test
	public void test() {
		System.out.println("success");
		log.info("-------------success--------------");
	}

	@Test
	public void testMapper(){
		System.out.println(userMapper);
		System.out.println(userMapper.selectAll());
	}
	@SpringBootApplication
	public static class Config{

	}
}
