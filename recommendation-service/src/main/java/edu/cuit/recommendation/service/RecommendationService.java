package edu.cuit.recommendation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 推荐服务业务逻辑
 *
 * 技术亮点：
 * - 通过 MovieClientService 调用 movies-service（外部调用，可被 AOP 拦截）
 * - MovieClientService 上配置了 @CircuitBreaker 和 @Retry 实现熔断重试
 * - 当 movies-service 不可用时，返回降级数据
 *
 * 服务调用关系：
 * recommendation-service ──> MovieClientService ──HTTP──> movies-service
 *        ↓                      ↓                         ↓
 *   AOP 代理拦截          熔断/重试/降级            返回电影数据
 */
@Service
public class RecommendationService {

    @Autowired
    private MovieClientService movieClientService;

    /**
     * 热门推荐：按平均评分降序，取前 limit 部
     */
    public List<Map<String, Object>> getHotRecommendations(int limit) {
        return movieClientService.fetchAllMovies().stream()
            .filter(m -> m.get("fallback") == null)
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
        return movieClientService.fetchAllMovies().stream()
            .filter(m -> m.get("fallback") == null)
            .filter(m -> getDoubleValue(m.get("averageScore")) >= 4.0)
            .sorted((m1, m2) -> Double.compare(
                getDoubleValue(m2.get("averageScore")),
                getDoubleValue(m1.get("averageScore"))))
            .collect(Collectors.toList());
    }

    /**
     * 个性化推荐：随机打乱取前 limit 部（模拟个性化）
     */
    public List<Map<String, Object>> getPersonalizedRecommendations(int limit) {
        List<Map<String, Object>> copy = new ArrayList<>(movieClientService.fetchAllMovies());
        copy.removeIf(m -> m.get("fallback") != null);
        Collections.shuffle(copy);
        return copy.stream().limit(limit).collect(Collectors.toList());
    }

    /**
     * 获取 movies-service 服务信息
     */
    public Map<String, Object> getMoviesServiceInfo() {
        return movieClientService.getMoviesServiceInfo();
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
