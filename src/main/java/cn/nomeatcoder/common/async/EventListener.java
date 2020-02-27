package cn.nomeatcoder.common.async;

import cn.nomeatcoder.common.domain.OrderItem;
import cn.nomeatcoder.dal.mapper.ProductMapper;
import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

/**
 * 异步事件监听
 *
 * @author Chenzhe Mao
 * @date 2020-02-28
 */
@Slf4j
@Component
public class EventListener {

	@Resource
	private InternalEventBus internalEventBus;

	@Resource
	private ProductMapper productMapper;

	/**
	 * 注册事件
	 */
	@PostConstruct
	public void init() {
		internalEventBus.register(this);
	}

	/**
	 * 异步调用更新任务状态
	 *
	 * @param orderItemList
	 * @return void
	 */
	@Subscribe
	public void handle(List<OrderItem> orderItemList) {
		try {
			log.info("[EventListener] handler begin, params:{}", orderItemList);
			productMapper.reduceStock(orderItemList);
		} catch (Exception e) {
			log.error("[EventListener] throw exception", e);
		}
	}
}

