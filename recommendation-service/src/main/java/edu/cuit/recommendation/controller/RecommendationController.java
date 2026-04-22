package edu.cuit.recommendation.controller;

import edu.cuit.recommendation.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 推荐服务控制器
 * 
 * 功能：
 * 1. 展示热门推荐（按评分排序）
 * 2. 展示高分推荐（评分 >= 4.0）
 * 3. 展示个性化推荐
 * 4. 显示服务调用信息（演示 Eureka 服务发现）
 * 
 * 访问地址：http://localhost:8083
 */
@Controller
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Value("${server.port}")
    private String port;

    /**
     * 首页 - 展示所有推荐
     * URL: /
     */
    @GetMapping("/")
    public String index(Model model) {
        // 获取各类推荐
        List<Map<String, Object>> hotMovies = recommendationService.getHotRecommendations(5);
        List<Map<String, Object>> highScoreMovies = recommendationService.getHighScoreRecommendations();
        List<Map<String, Object>> personalizedMovies = recommendationService.getPersonalizedRecommendations(4);

        // 获取被调用的服务信息（确保不为 null）
        Map<String, Object> serviceInfo = recommendationService.getMoviesServiceInfo();
        if (serviceInfo == null) {
            serviceInfo = new HashMap<>();
        }

        // 查询 movies-service 的实例信息（演示服务发现）
        List<ServiceInstance> instances = discoveryClient.getInstances("movies-service");

        model.addAttribute("hotMovies", hotMovies);
        model.addAttribute("highScoreMovies", highScoreMovies);
        model.addAttribute("personalizedMovies", personalizedMovies);
        model.addAttribute("serviceInfo", serviceInfo);
        model.addAttribute("instances", instances);
        model.addAttribute("serviceName", "recommendation-service");
        model.addAttribute("port", port);

        return "index";
    }

    /**
     * 热门推荐页面
     * URL: /hot
     */
    @GetMapping("/hot")
    public String hotRecommendations(Model model) {
        List<Map<String, Object>> movies = recommendationService.getHotRecommendations(10);
        model.addAttribute("movies", movies);
        model.addAttribute("title", "热门推荐");
        model.addAttribute("description", "基于用户评分的最受欢迎电影");
        model.addAttribute("icon", "🔥");
        return "recommendations";
    }

    /**
     * 高分推荐页面
     * URL: /high-score
     */
    @GetMapping("/high-score")
    public String highScoreRecommendations(Model model) {
        List<Map<String, Object>> movies = recommendationService.getHighScoreRecommendations();
        model.addAttribute("movies", movies);
        model.addAttribute("title", "高分推荐");
        model.addAttribute("description", "评分 4.0 以上的优质电影");
        model.addAttribute("icon", "⭐");
        return "recommendations";
    }

    /**
     * 个性化推荐页面
     * URL: /personalized
     */
    @GetMapping("/personalized")
    public String personalizedRecommendations(Model model) {
        List<Map<String, Object>> movies = recommendationService.getPersonalizedRecommendations(8);
        model.addAttribute("movies", movies);
        model.addAttribute("title", "为你推荐");
        model.addAttribute("description", "根据您的观影偏好智能推荐");
        model.addAttribute("icon", "💡");
        return "recommendations";
    }

    /**
     * 服务信息页面（调试用）
     * URL: /service-info
     */
    @GetMapping("/service-info")
    public String serviceInfo(Model model) {
        Map<String, Object> info = recommendationService.getMoviesServiceInfo();
        List<ServiceInstance> instances = discoveryClient.getInstances("movies-service");
        
        model.addAttribute("moviesServiceInfo", info);
        model.addAttribute("instances", instances);
        model.addAttribute("allServices", discoveryClient.getServices());
        
        return "service-info";
    }
}
