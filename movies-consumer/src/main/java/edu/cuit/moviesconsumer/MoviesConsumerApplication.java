package edu.cuit.moviesconsumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 服务消费者启动类
 * 端口: 8082
 * 功能: 注册到 Eureka，通过服务名 "movies-service" 发现并调用电影服务
 */
@SpringBootApplication
@EnableDiscoveryClient  // 开启服务发现
public class MoviesConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoviesConsumerApplication.class, args);
    }
}
