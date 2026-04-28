package edu.cuit.recommendation.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

/**
 * 电影服务 Feign 客户端
 *
 * 通过服务名 movies-service 从 Eureka 发现实例并调用
 */
@FeignClient(name = "movies-service")
public interface MovieClient {

    /**
     * 获取所有电影列表
     * GET /api/movies
     */
    @GetMapping("/api/movies")
    Map<String, Object> getAllMovies();

    /**
     * 获取服务健康信息
     * GET /api/info
     */
    @GetMapping("/api/info")
    Map<String, String> info();
}
