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
}