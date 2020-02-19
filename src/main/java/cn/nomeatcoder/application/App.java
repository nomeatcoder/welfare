package cn.nomeatcoder.application;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Hello world!
 *
 */
@SpringBootApplication(scanBasePackages = {
    "cn.nomeatcoder.controller",
    "cn.nomeatcoder.service",
    "cn.nomeatcoder.common",
})
@MapperScan(basePackages = {
    "cn.nomeatcoder.dal.mapper"
})
@EnableTransactionManagement
//TODO 添加事务
public class App {
    public static void main( String[] args ) {
        SpringApplication.run(App.class, args);
//        log.info("---------------------started-----------------------");
    }
}
