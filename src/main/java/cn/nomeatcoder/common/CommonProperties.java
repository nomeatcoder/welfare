package cn.nomeatcoder.common;

import lombok.Data;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class CommonProperties {
	@Value("${password.salt}")
	private String salt;
	//线上域名需要备案,所以本机用内网穿透验证,线上不验证
	@Value("${alipay.callback.url}")
	private String callback;
}
