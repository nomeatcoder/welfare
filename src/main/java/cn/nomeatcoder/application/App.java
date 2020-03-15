package cn.nomeatcoder.application;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Hello world!
 *
 */
@SpringBootApplication(scanBasePackages = {
    "cn.nomeatcoder.controller",
    "cn.nomeatcoder.service",
    "cn.nomeatcoder.common",
    "cn.nomeatcoder.config",
    "cn.nomeatcoder.schedule",
})
@MapperScan(basePackages = {
    "cn.nomeatcoder.dal.mapper"
})
@EnableTransactionManagement
@EnableScheduling
@Slf4j
public class App {
    public static void main( String[] args ) {
        log.debug("debug");
        log.info("debug");
        log.error("debug");
        log.warn("debug");
        SpringApplication.run(App.class, args);
//        log.info("---------------------started-----------------------");
    }
}
