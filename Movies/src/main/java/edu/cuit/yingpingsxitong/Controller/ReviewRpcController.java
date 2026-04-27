package edu.cuit.yingpingsxitong.Controller;

import edu.cuit.yingpingsxitong.Entity.Review;
import edu.cuit.yingpingsxitong.Service.ReviewService;
import edu.cuit.yingpingsxitong.client.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 评论服务RPC控制器 - 提供评论相关的RPC接口
 */
@RestController
@RequestMapping("/rpc/review")
public class ReviewRpcController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping("/insert")
    public Result<Void> insertReview(@RequestBody Review review) {
        reviewService.insertReview(review);
        return Result.success();
    }

    @GetMapping("/findById")
    public Result<Review> findReviewById(@RequestParam("reviewId") Integer reviewId) {
        Review review = reviewService.findReviewById(reviewId);
        return Result.success(review);
    }

    @GetMapping("/findByMovieId")
    public Result<List<Review>> findReviewsByMovieId(@RequestParam("movieId") Integer movieId) {
        List<Review> reviews = reviewService.findReviewsByMovieId(movieId);
        return Result.success(reviews);
    }

    @GetMapping("/findByUserId")
    public Result<List<Review>> findReviewsByUserId(@RequestParam("userId") Integer userId) {
        List<Review> reviews = reviewService.findReviewsByUserId(userId);
        return Result.success(reviews);
    }

    @PostMapping("/update")
    public Result<Void> updateReview(@RequestBody Review review) {
        reviewService.updateReview(review);
        return Result.success();
    }

    @PostMapping("/delete")
    public Result<Void> deleteReview(@RequestParam("reviewId") Integer reviewId) {
        reviewService.deleteReviewCascade(reviewId);
        return Result.success();
    }

    @GetMapping("/findAll")
    public Result<List<Review>> findAllReviews() {
        List<Review> reviews = reviewService.findAllReviews();
        return Result.success(reviews);
    }

    @GetMapping("/count")
    public Result<Integer> countReviews() {
        return Result.success(reviewService.countReviews());
    }

    @GetMapping("/findReplies")
    public Result<List<Review>> findRepliesByParentId(@RequestParam("parentId") Integer parentId) {
        return Result.success(reviewService.findRepliesByParentId(parentId));
    }

    @GetMapping("/findTopLevelByMovieId")
    public Result<List<Review>> findTopLevelReviewsByMovieId(@RequestParam("movieId") Integer movieId) {
        return Result.success(reviewService.findTopLevelReviewsByMovieId(movieId));
    }

    @PostMapping("/toggleLike")
    public Result<Boolean> toggleLike(@RequestParam("reviewId") Integer reviewId, @RequestParam("userId") Integer userId) {
        boolean liked = reviewService.toggleLike(reviewId, userId);
        return Result.success(liked);
    }

    @GetMapping("/hasLiked")
    public Result<Boolean> hasLiked(@RequestParam("reviewId") Integer reviewId, @RequestParam("userId") Integer userId) {
        return Result.success(reviewService.hasUserLiked(reviewId, userId));
    }
}
