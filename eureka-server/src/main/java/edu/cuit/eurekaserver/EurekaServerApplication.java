package edu.cuit.eurekaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Eureka 服务注册中心
 * 启动后访问 http://localhost:8761 可查看注册中心控制台
 */
@SpringBootApplication
@EnableEurekaServer  // 开启 Eureka Server 功能
public class EurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
