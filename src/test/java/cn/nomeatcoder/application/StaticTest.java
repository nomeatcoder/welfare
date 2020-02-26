package cn.nomeatcoder.application;

import cn.nomeatcoder.common.vo.ProductDetailVo;
import cn.nomeatcoder.utils.GsonUtils;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StaticTest {
	@Test
	public void test(){
		System.out.println(GsonUtils.fromGson2Obj(null, ProductDetailVo.class));
	}
	
}
