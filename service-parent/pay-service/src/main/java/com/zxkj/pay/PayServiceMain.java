package com.zxkj.pay;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * 支付微服务
 *
 * @author ：yuhui
 * @date ：Created in 2020/8/4 14:59
 */
//@MapperScan(basePackages = {"com.zxkj.pay.mapper"})
@EnableFeignClients(basePackages = {"com.zxkj.*.feign"})
@ComponentScan(basePackages = { "com.zxkj" })
@SpringBootApplication
public class PayServiceMain implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(PayServiceMain.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        System.out.println("PayServiceMain is running!");
    }

}
