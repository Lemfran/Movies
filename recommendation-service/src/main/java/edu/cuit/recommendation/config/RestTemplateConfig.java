package edu.cuit.recommendation.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate 配置类
 * 
 * @LoadBalanced 注解的作用：
 * 1. 使 RestTemplate 具备服务发现能力
 * 2. 可以用服务名（如 http://movies-service）替代具体 IP+端口
 * 3. 自动实现负载均衡（如果有多个实例）
 * 
 * 使用示例：
 * restTemplate.getForObject("http://movies-service/api/movies", Map.class);
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    @LoadBalanced  // 关键注解：赋予负载均衡能力
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
