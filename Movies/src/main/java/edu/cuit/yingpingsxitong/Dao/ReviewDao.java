package edu.cuit.yingpingsxitong.Dao;

import edu.cuit.yingpingsxitong.Entity.Review;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
    int countReviews();

    // 回复相关
    List<Review> findRepliesByParentId(Integer parentId);
    List<Review> findTopLevelReviewsByMovieId(Integer movieId);
    void deleteRepliesByParentId(Integer parentId);

    // 点赞相关
    void deleteLikesByReviewId(Integer reviewId);
    int hasUserLiked(@Param("reviewId") Integer reviewId, @Param("userId") Integer userId);
    void insertLike(@Param("reviewId") Integer reviewId, @Param("userId") Integer userId);
    void deleteLike(@Param("reviewId") Integer reviewId, @Param("userId") Integer userId);
    void incrementLikeCount(Integer reviewId);
    void decrementLikeCount(Integer reviewId);
}
