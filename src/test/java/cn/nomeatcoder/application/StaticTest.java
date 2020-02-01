package cn.nomeatcoder.application;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StaticTest {
	@Test
	public void test(){
		String str = "redirec";
		String pattern = ".*[\\;'\\&lt;\\&gt;]";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(str);
		if (m.find( )) {
			System.out.println("Found value: " + m.group(0) );
		} else {
			System.out.println("NO MATCH");
		}
	}
}
