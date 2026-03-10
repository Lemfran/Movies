package edu.cuit.yingpingsxitong.Dao;

import edu.cuit.yingpingsxitong.Entity.Review;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReviewDao {
    void insertReview(Review review);
    Review findReviewById(Integer reviewId);
    List<Review> findReviewsByMovieId(Integer movieId);
    List<Review> findReviewsByUserId(Integer userId);
    void updateReview(Review review);
    void deleteReview(Integer reviewId);
    List<Review> findAllReviews();
}
