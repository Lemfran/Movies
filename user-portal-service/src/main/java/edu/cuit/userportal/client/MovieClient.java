package edu.cuit.userportal.client;

import edu.cuit.userportal.entity.Movie;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@FeignClient(name = "movies-service", contextId = "movieClient")
public interface MovieClient {
    @PostMapping("/rpc/movie/insert")
    Result<Void> insertMovie(@RequestBody Movie movie);

    @GetMapping("/rpc/movie/findById")
    Result<Movie> findMovieById(@RequestParam("movieId") Integer movieId);

    @GetMapping("/rpc/movie/findAll")
    Result<List<Movie>> findAllMovies();

    @GetMapping("/rpc/movie/search")
    Result<List<Movie>> getSearchList(@RequestParam("title") String title);

    @PostMapping("/rpc/movie/update")
    Result<Void> updateMovie(@RequestBody Movie movie);

    @PostMapping("/rpc/movie/delete")
    Result<Void> deleteMovie(@RequestParam("movieId") Integer movieId);

    @PostMapping("/rpc/movie/updateAverageScore")
    Result<Void> updateAverageScore(@RequestParam("movieId") Integer movieId);

    @GetMapping("/rpc/movie/count")
    Result<Integer> countMovies();

    @GetMapping("/rpc/movie/info")
    Result<Map<String, String>> info();
}
