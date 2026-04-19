package edu.cuit.recommendation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 推荐服务业务逻辑
 *
 * 优化点：所有推荐方法共用一次 HTTP 调用结果，避免重复请求 movies-service。
 *
 * 服务调用关系：
 * recommendation-service ──HTTP──> movies-service
 *        ↓                              ↓
 *   Eureka 发现地址                  返回电影数据
 */
@Service
public class RecommendationService {

    @Autowired
    private RestTemplate restTemplate;

    private static final String MOVIES_SERVICE = "http://movies-service";

    /**
     * 从 movies-service 获取全部电影数据（核心调用，只调用一次）
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> fetchAllMovies() {
        try {
            Map<String, Object> response = restTemplate.getForObject(
                MOVIES_SERVICE + "/api/movies", Map.class
            );
            if (response == null || response.get("data") == null) {
                return new ArrayList<>();
            }
            return (List<Map<String, Object>>) response.get("data");
        } catch (Exception e) {
            System.err.println("调用 movies-service 失败: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 热门推荐：按平均评分降序，取前 limit 部
     */
    public List<Map<String, Object>> getHotRecommendations(int limit) {
        return fetchAllMovies().stream()
            .sorted((m1, m2) -> Double.compare(
                getDoubleValue(m2.get("averageScore")),
                getDoubleValue(m1.get("averageScore"))))
            .limit(limit)
            .collect(Collectors.toList());
    }

    /**
     * 高分推荐：筛选评分 >= 4.0，按评分降序
     */
    public List<Map<String, Object>> getHighScoreRecommendations() {
        return fetchAllMovies().stream()
            .filter(m -> getDoubleValue(m.get("averageScore")) >= 4.0)
            .sorted((m1, m2) -> Double.compare(
                getDoubleValue(m2.get("averageScore")),
                getDoubleValue(m1.get("averageScore"))))
            .collect(Collectors.toList());
    }

    /**
     * 个性化推荐：随机打乱取前 limit 部（模拟个性化）
     * 注意：先复制列表再 shuffle，避免修改原始数据
     */
    public List<Map<String, Object>> getPersonalizedRecommendations(int limit) {
        List<Map<String, Object>> copy = new ArrayList<>(fetchAllMovies());
        Collections.shuffle(copy);
        return copy.stream().limit(limit).collect(Collectors.toList());
    }

    /**
     * 获取 movies-service 服务信息（用于演示负载均衡：显示响应的实例端口）
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

    /**
     * 辅助方法：安全地将任意类型转为 Double
     */
    private Double getDoubleValue(Object value) {
        if (value == null) return 0.0;
        if (value instanceof Double) return (Double) value;
        if (value instanceof Integer) return ((Integer) value).doubleValue();
        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
