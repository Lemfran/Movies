package edu.cuit.yingpingsxitong.Controller;

import edu.cuit.yingpingsxitong.Entity.Movie;
import edu.cuit.yingpingsxitong.Service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 电影 REST API 接口（供服务消费者通过服务名调用）
 * 服务名: movies-service，端口: 8081
 */
@RestController
@RequestMapping("/api")
public class MovieRestController {

    @Autowired
    private MovieService movieService;

    // 注入当前服务端口，方便演示负载均衡时识别是哪个实例响应
    @Value("${server.port}")
    private String port;

    /**
     * 获取所有电影列表
     * GET /api/movies
     */
    @GetMapping("/movies")
    public Map<String, Object> getAllMovies() {
        List<Movie> movies = movieService.findAllMovies();
        Map<String, Object> result = new HashMap<>();
        result.put("servicePort", port);          // 标识响应的服务实例端口
        result.put("serviceName", "movies-service");
        result.put("total", movies.size());
        result.put("data", movies);
        return result;
    }

    /**
     * 根据 ID 获取电影详情
     * GET /api/movies/{id}
     */
    @GetMapping("/movies/{id}")
    public Map<String, Object> getMovieById(@PathVariable Integer id) {
        Movie movie = movieService.findMovieById(id);
        Map<String, Object> result = new HashMap<>();
        result.put("servicePort", port);
        result.put("serviceName", "movies-service");
        result.put("data", movie);
        return result;
    }

    /**
     * 根据标题搜索电影（模糊查询）
     * GET /api/movies/search?title=xxx
     */
    @GetMapping("/movies/search")
    public Map<String, Object> searchMovies(@RequestParam String title) {
        List<Movie> movies = movieService.getSearchList(title);
        Map<String, Object> result = new HashMap<>();
        result.put("servicePort", port);
        result.put("serviceName", "movies-service");
        result.put("keyword", title);
        result.put("total", movies.size());
        result.put("data", movies);
        return result;
    }

    /**
     * 服务健康检查接口
     * GET /api/info
     */
    @GetMapping("/info")
    public Map<String, String> info() {
        Map<String, String> info = new HashMap<>();
        info.put("service", "movies-service");
        info.put("port", port);
        info.put("status", "UP");
        info.put("description", "影评系统 - 电影服务提供者");
        return info;
    }
}
