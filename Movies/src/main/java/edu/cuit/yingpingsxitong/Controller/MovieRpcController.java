package edu.cuit.yingpingsxitong.Controller;

import edu.cuit.yingpingsxitong.Entity.Movie;
import edu.cuit.yingpingsxitong.Service.MovieService;
import edu.cuit.yingpingsxitong.client.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 电影服务RPC控制器 - 提供电影相关的RPC接口
 */
@RestController
@RequestMapping("/rpc/movie")
public class MovieRpcController {

    @Autowired
    private MovieService movieService;

    @Value("${server.port}")
    private String serverPort;

    @GetMapping("/info")
    public Result<Map<String, String>> info() {
        Map<String, String> map = new HashMap<>();
        map.put("service", "movies-service");
        map.put("port", serverPort);
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return Result.success(map);
    }

    @PostMapping("/insert")
    public Result<Void> insertMovie(@RequestBody Movie movie) {
        movieService.insertMovie(movie);
        return Result.success();
    }

    @GetMapping("/findById")
    public Result<Movie> findMovieById(@RequestParam("movieId") Integer movieId) {
        Movie movie = movieService.findMovieById(movieId);
        return Result.success(movie);
    }

    @GetMapping("/findAll")
    public Result<List<Movie>> findAllMovies() {
        List<Movie> movies = movieService.findAllMovies();
        return Result.success(movies);
    }

    @GetMapping("/search")
    public Result<List<Movie>> getSearchList(@RequestParam("title") String title) {
        List<Movie> movies = movieService.getSearchList(title);
        return Result.success(movies);
    }

    @PostMapping("/update")
    public Result<Void> updateMovie(@RequestBody Movie movie) {
        movieService.updateMovie(movie);
        return Result.success();
    }

    @PostMapping("/delete")
    public Result<Void> deleteMovie(@RequestParam("movieId") Integer movieId) {
        movieService.deleteMovie(movieId);
        return Result.success();
    }

    @PostMapping("/updateAverageScore")
    public Result<Void> updateAverageScore(@RequestParam("movieId") Integer movieId) {
        movieService.updateAverageScore(movieId);
        return Result.success();
    }

    @GetMapping("/count")
    public Result<Integer> countMovies() {
        return Result.success(movieService.countMovies());
    }
}
