package edu.cuit.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 微服务网关启动类
 *
 * 技术说明：
 * - 本服务使用 Spring Cloud Gateway 作为网关（Spring Cloud 2024.0.0 已移除 Zuul 支持）
 * - Gateway 提供了与 Zuul 等效的路由转发和过滤器链功能
 * - 统一入口端口：8080
 */
@SpringBootApplication
@EnableDiscoveryClient
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
