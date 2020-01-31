package cn.nomeatcoder.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * Hello world!
 *
 */
@SpringBootApplication(scanBasePackages = {"cn.nomeatcoder.controller"})
@MapperScan(basePackages = {
    "cn.nomeatcoder.mapper"
})
public class App {
    public static void main( String[] args ) {
        SpringApplication.run(App.class, args);
//        log.info("---------------------started-----------------------");
    }
}
