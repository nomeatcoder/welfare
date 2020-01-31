package cn.nomeatcoder.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@MapperScan(basePackages = {
    "cn.nomeatcoder.mapper"
})
public class App {
    public static void main( String[] args ) {
        SpringApplication.run(App.class, args);
    }
}
