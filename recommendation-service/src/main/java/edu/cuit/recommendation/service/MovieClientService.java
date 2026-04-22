package edu.cuit.recommendation.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 电影数据客户端服务
 *
 * 将外部 HTTP 调用抽离到单独的类中，确保 @CircuitBreaker 和 @Retry
 * 的 AOP 代理能正确拦截（避免同类自调用问题）
 */
@Service
public class MovieClientService {

    @Autowired
    private RestTemplate restTemplate;

    private static final String MOVIES_SERVICE = "http://movies-service";

    /**
     * 从 movies-service 获取全部电影数据
     *
     * 容错保护：
     * - @CircuitBreaker: 失败率达到阈值时熔断，调用降级方法
     * - @Retry: 调用失败时自动重试3次
     */
    @CircuitBreaker(name = "movies-service", fallbackMethod = "fetchAllMoviesFallback")
    @Retry(name = "movies-service")
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> fetchAllMovies() {
        Map<String, Object> response = restTemplate.getForObject(
            MOVIES_SERVICE + "/api/movies", Map.class
        );
        if (response == null || response.get("data") == null) {
            return new ArrayList<>();
        }
        return (List<Map<String, Object>>) response.get("data");
    }

    /**
     * 降级方法：当 movies-service 不可用时返回提示数据
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> fetchAllMoviesFallback(Exception ex) {
        System.out.println("[熔断降级] movies-service 调用失败，返回降级数据。原因: " + ex.getMessage());

        Map<String, Object> fallbackMovie = new HashMap<>();
        fallbackMovie.put("movieId", -1);
        fallbackMovie.put("title", "⚠️ 服务暂时不可用");
        fallbackMovie.put("description", "电影数据服务暂时无法访问，请稍后重试。");
        fallbackMovie.put("averageScore", 0.0);
        fallbackMovie.put("posterImage", "");
        fallbackMovie.put("releaseDate", null);
        fallbackMovie.put("runtime", 0);
        fallbackMovie.put("fallback", true);

        List<Map<String, Object>> fallbackList = new ArrayList<>();
        fallbackList.add(fallbackMovie);
        return fallbackList;
    }

    /**
     * 获取 movies-service 服务信息
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getMoviesServiceInfo() {
        try {
            return restTemplate.getForObject(MOVIES_SERVICE + "/api/info", Map.class);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "无法连接到 movies-service");
            error.put("message", e.getMessage());
            return error;
        }
    }
}
