package cn.nomeatcoder.application;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hello world!
 *
 */
@SpringBootApplication(scanBasePackages = {
    "cn.nomeatcoder.controller",
    "cn.nomeatcoder.service",
})
@MapperScan(basePackages = {
    "cn.nomeatcoder.dal.mapper"
})
public class App {
    public static void main( String[] args ) {
        SpringApplication.run(App.class, args);
//        log.info("---------------------started-----------------------");
    }
}
