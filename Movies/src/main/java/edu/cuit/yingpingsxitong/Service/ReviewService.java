package edu.cuit.yingpingsxitong.Service;

import edu.cuit.yingpingsxitong.Entity.Review;
import edu.cuit.yingpingsxitong.Dao.ReviewDao;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {
    private final ReviewDao reviewDao;

    @Autowired
    public ReviewService(SqlSessionTemplate sqlSessionTemplate) {
        this.reviewDao = sqlSessionTemplate.getMapper(ReviewDao.class);
    }

    public void insertReview(Review review) {
        reviewDao.insertReview(review);
    }

    public Review findReviewById(Integer reviewId) {
        return reviewDao.findReviewById(reviewId);
    }

    public List<Review> findReviewsByMovieId(Integer movieId){
        return reviewDao.findReviewsByMovieId(movieId);
    }

    public List<Review> findReviewsByUserId(Integer userId){
        return reviewDao.findReviewsByUserId(userId);
    }

    public void updateReview(Review review) {
        reviewDao.updateReview(review);
    }

    public void deleteReview(Integer reviewId) {
        reviewDao.deleteReview(reviewId);
    }

    public List<Review> findAllReviews() {
        return reviewDao.findAllReviews();
    }

    public int countReviews() {
        return reviewDao.countReviews();
    }

    public List<Review> findRepliesByParentId(Integer parentId) {
        return reviewDao.findRepliesByParentId(parentId);
    }

    public List<Review> findTopLevelReviewsByMovieId(Integer movieId) {
        return reviewDao.findTopLevelReviewsByMovieId(movieId);
    }

    public void deleteReviewCascade(Integer reviewId) {
        // 1. 获取该评论的所有回复ID
        List<Review> replies = reviewDao.findRepliesByParentId(reviewId);
        // 2. 删除回复的点赞记录
        for (Review reply : replies) {
            reviewDao.deleteLikesByReviewId(reply.getReviewId());
        }
        // 3. 删除回复
        reviewDao.deleteRepliesByParentId(reviewId);
        // 4. 删除评论本身的点赞记录
        reviewDao.deleteLikesByReviewId(reviewId);
        // 5. 删除评论
        reviewDao.deleteReview(reviewId);
    }

    public boolean toggleLike(Integer reviewId, Integer userId) {
        int liked = reviewDao.hasUserLiked(reviewId, userId);
        if (liked > 0) {
            reviewDao.deleteLike(reviewId, userId);
            reviewDao.decrementLikeCount(reviewId);
            return false;
        } else {
            reviewDao.insertLike(reviewId, userId);
            reviewDao.incrementLikeCount(reviewId);
            return true;
        }
    }

    public boolean hasUserLiked(Integer reviewId, Integer userId) {
        return reviewDao.hasUserLiked(reviewId, userId) > 0;
    }
}