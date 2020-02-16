package cn.nomeatcoder.service.impl;


import cn.nomeatcoder.common.PageInfo;
import cn.nomeatcoder.common.ServerResponse;
import cn.nomeatcoder.common.domain.Shipping;
import cn.nomeatcoder.common.query.ShippingQuery;
import cn.nomeatcoder.dal.mapper.ShippingMapper;
import cn.nomeatcoder.service.ShippingService;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service("shippingService")
public class ShippingServiceImpl implements ShippingService {


	@Resource
	private ShippingMapper shippingMapper;

	@Override
	public ServerResponse add(Integer userId, Shipping shipping) {
		shipping.setUserId(userId);
		long rowCount = shippingMapper.insert(shipping);
		if (rowCount > 0) {
			Map result = Maps.newHashMap();
			result.put("shippingId", shipping.getId());
			return ServerResponse.success("新建地址成功", result);
		}
		return ServerResponse.error("新建地址失败");
	}

	@Override
	public ServerResponse del(Integer userId, Integer shippingId) {
		ShippingQuery query = new ShippingQuery();
		query.setUserId(userId);
		query.setId(shippingId);
		int resultCount = shippingMapper.delete(query);
		if (resultCount > 0) {
			return ServerResponse.success("删除地址成功");
		}
		return ServerResponse.error("删除地址失败");
	}


	@Override
	public ServerResponse update(Integer userId, Shipping shipping) {
		shipping.setUserId(userId);
		int rowCount = shippingMapper.update(shipping);
		if (rowCount > 0) {
			return ServerResponse.success("更新地址成功");
		}
		return ServerResponse.error("更新地址失败");
	}

	@Override
	public ServerResponse select(Integer userId, Integer shippingId) {
		ShippingQuery query = new ShippingQuery();
		query.setUserId(userId);
		query.setId(shippingId);
		Shipping shipping = shippingMapper.get(query);
		if (shipping == null) {
			return ServerResponse.error("无法查询到该地址");
		}
		return ServerResponse.success("查询地址成功", shipping);
	}


	@Override
	public ServerResponse list(Integer userId, int pageNum, int pageSize) {
		ShippingQuery query = new ShippingQuery();
		query.setUserId(userId);
		query.setPageSize(pageSize);
		query.setCurrentPageInt(pageNum);
		List<Shipping> shippingList = shippingMapper.find(query);
		PageInfo pageInfo = new PageInfo();
		pageInfo.init(shippingMapper.count(query), pageNum, pageSize, shippingList);
		return ServerResponse.success(pageInfo);
	}

}
