package cn.nomeatcoder.common.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderProductVo {
    private List<OrderItemVo> orderItemVoList;
    private BigDecimal productTotalPrice;
    private BigDecimal useIntegral;
    private BigDecimal remain;
    private String imageHost;
}
