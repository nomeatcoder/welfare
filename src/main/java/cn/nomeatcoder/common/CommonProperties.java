package cn.nomeatcoder.common;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class CommonProperties {
	@Value("${password.salt}")
	private String salt;
}
