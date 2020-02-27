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

	@Value("${spring.redis.sentinel.nodes}")
	private String sentinelNodes;

	@Value("${spring.redis.sentinel.master}")
	private String master;

	@Bean
	public Redisson redisson() {
		Config config = new Config();
		String[] nodes = sentinelNodes.split(",");
		List<String> newNodes = new ArrayList(nodes.length);
		Arrays.stream(nodes).forEach((index) -> newNodes.add(
			index.startsWith("redis://") ? index : "redis://" + index));
		config.useSentinelServers()
			.addSentinelAddress(newNodes.toArray(new String[0]))
			.setMasterName(master)
			.setReadMode(ReadMode.SLAVE);

		return (Redisson) Redisson.create(config);
	}

}
