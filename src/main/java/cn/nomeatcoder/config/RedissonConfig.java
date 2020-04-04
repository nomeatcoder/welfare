package cn.nomeatcoder.config;

import org.redisson.Redisson;
import org.redisson.config.Config;
import org.redisson.config.ReadMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class RedissonConfig {

	@Value("${spring.redis.host}")
	private String host;

	@Value("${spring.redis.port}")
	private Integer port;

	@Value("${spring.redis.password}")
	private String password;

	@Value("${spring.redis.database}")
	private Integer database;

	@Bean
	public Redisson redisson() {
		Config config = new Config();
		String address = "redis://"+host+":"+port;
		config.useSingleServer()
			.setAddress(address)
			.setDatabase(database)
			.setPassword(password);

		return (Redisson) Redisson.create(config);
	}

}
