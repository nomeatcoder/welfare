package cn.nomeatcoder.common.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderVo {

	private Long orderNo;
	private BigDecimal useIntegral;
	private BigDecimal remain;
	private BigDecimal payment;
	private Integer paymentType;
	private String paymentTypeDesc;
	private Integer postage;
	private Integer status;
	private String statusDesc;
	private String paymentTime;
	private String sendTime;
	private String endTime;
	private String closeTime;
	private String createTime;
	private List<OrderItemVo> orderItemVoList;
	private String imageHost;
	private Integer shippingId;
	private String receiverName;
	private ShippingVo shippingVo;

}
