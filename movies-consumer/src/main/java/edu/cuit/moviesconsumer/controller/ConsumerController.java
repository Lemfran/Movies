package edu.cuit.moviesconsumer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

/**
 * 服务消费者控制器
 * 通过服务名 "movies-service" 调用电影服务，无需硬编码 IP 和端口。
 */
@Controller
public class ConsumerController {

    // 注入具有负载均衡能力的 RestTemplate
    @Autowired
    private RestTemplate restTemplate;

    // 注入 DiscoveryClient，用于查询注册中心中的服务实例信息
    @Autowired
    private DiscoveryClient discoveryClient;

    // 服务提供者的服务名（对应 movies-service 的 spring.application.name）
    private static final String MOVIES_SERVICE = "http://movies-service";

    /**
     * 首页：展示服务发现信息
     * GET /
     */
    @GetMapping("/")
    public String index(Model model) {
        // 通过 DiscoveryClient 查询 movies-service 的所有实例
        List<ServiceInstance> instances = discoveryClient.getInstances("movies-service");
        model.addAttribute("instances", instances);
        model.addAttribute("allServices", discoveryClient.getServices());
        return "index";
    }

    /**
     * 调用 movies-service 获取全部电影列表
     * GET /movies
     */
    @GetMapping("/movies")
    public String getMovies(Model model) {
        // 使用服务名发起调用，LoadBalancer 自动解析为真实地址
        Map result = restTemplate.getForObject(MOVIES_SERVICE + "/api/movies", Map.class);
        model.addAttribute("result", result);
        model.addAttribute("apiUrl", MOVIES_SERVICE + "/api/movies");
        return "movies";
    }

    /**
     * 调用 movies-service 获取单部电影详情
     * GET /movies/{id}
     */
    @GetMapping("/movies/{id}")
    public String getMovieById(@PathVariable Integer id, Model model) {
        Map result = restTemplate.getForObject(MOVIES_SERVICE + "/api/movies/" + id, Map.class);
        model.addAttribute("result", result);
        model.addAttribute("apiUrl", MOVIES_SERVICE + "/api/movies/" + id);
        return "movie-detail";
    }

    /**
     * 调用 movies-service 搜索电影
     * GET /movies/search?title=xxx
     */
    @GetMapping("/search")
    public String searchMovies(@RequestParam(defaultValue = "") String title, Model model) {
        if (title.isEmpty()) {
            return "search";
        }
        Map result = restTemplate.getForObject(
                MOVIES_SERVICE + "/api/movies/search?title=" + title, Map.class);
        model.addAttribute("result", result);
        model.addAttribute("keyword", title);
        model.addAttribute("apiUrl", MOVIES_SERVICE + "/api/movies/search?title=" + title);
        return "search";
    }

    /**
     * 调用 movies-service 的健康检查接口
     * GET /service-info
     */
    @GetMapping("/service-info")
    @ResponseBody
    public Map serviceInfo() {
        return restTemplate.getForObject(MOVIES_SERVICE + "/api/info", Map.class);
    }
}
