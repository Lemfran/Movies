package edu.cuit.yingpingsxitong.client;

import edu.cuit.yingpingsxitong.Entity.Review;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@FeignClient(name = "movies-service", contextId = "reviewClient")
public interface ReviewClient {
    @PostMapping("/rpc/review/insert")
    Result<Void> insertReview(@RequestBody Review review);

    @GetMapping("/rpc/review/findById")
    Result<Review> findReviewById(@RequestParam("reviewId") Integer reviewId);

    @GetMapping("/rpc/review/findByMovieId")
    Result<List<Review>> findReviewsByMovieId(@RequestParam("movieId") Integer movieId);

    @GetMapping("/rpc/review/findByUserId")
    Result<List<Review>> findReviewsByUserId(@RequestParam("userId") Integer userId);

    @PostMapping("/rpc/review/update")
    Result<Void> updateReview(@RequestBody Review review);

    @PostMapping("/rpc/review/delete")
    Result<Void> deleteReview(@RequestParam("reviewId") Integer reviewId);

    @GetMapping("/rpc/review/findAll")
    Result<List<Review>> findAllReviews();

    @GetMapping("/rpc/review/count")
    Result<Integer> countReviews();
}
