package edu.cuit.moviesconsumer.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate 配置类
 * @LoadBalanced 注解使 RestTemplate 具备负载均衡能力：
 * 可以用服务名（如 http://movies-service/api/movies）替代具体 IP+端口发起请求，
 * Spring Cloud LoadBalancer 会自动解析服务名并进行负载均衡。
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    @LoadBalanced  // 赋予 RestTemplate 负载均衡能力，通过服务名而非 IP 调用
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
