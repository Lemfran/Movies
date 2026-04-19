package edu.cuit.recommendation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 电影推荐服务启动类
 * 
 * 服务职责：
 * 1. 从 movies-service 获取电影数据和用户评分
 * 2. 基于评分数据计算推荐列表
 * 3. 提供推荐结果给前端展示
 * 
 * 技术亮点：
 * - 通过 Eureka 服务发现调用 movies-service
 * - 使用 @LoadBalanced 实现负载均衡
 * - 演示微服务间的协作关系
 */
@SpringBootApplication
@EnableDiscoveryClient  // 开启服务发现，注册到 Eureka
public class RecommendationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecommendationServiceApplication.class, args);
    }
}
