package cn.nomeatcoder.common;

import java.lang.annotation.*;

/**
 * 该注解表示不需要登录
 *
 * @author Chenzhe Mao
 * @date 2020-02-23
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface IgnoreLogin {
}

