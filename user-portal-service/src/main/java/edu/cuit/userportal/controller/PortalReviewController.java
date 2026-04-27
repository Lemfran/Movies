package edu.cuit.userportal.controller;

import edu.cuit.userportal.client.MovieClient;
import edu.cuit.userportal.client.Result;
import edu.cuit.userportal.client.ReviewClient;
import edu.cuit.userportal.client.UserClient;
import edu.cuit.userportal.entity.Movie;
import edu.cuit.userportal.entity.Review;
import edu.cuit.userportal.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Controller
public class PortalReviewController {
    @Autowired
    private MovieClient movieClient;
    @Autowired
    private ReviewClient reviewClient;
    @Autowired
    private UserClient userClient;

    private User getUserFromSession(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user != null && model != null) {
            model.addAttribute("user", user);
        }
        return user;
    }

    @RequestMapping("/reviews")
    public String review(HttpSession session, Model model) {
        User user = getUserFromSession(session, model);
        if (user == null) {
            model.addAttribute("error", "请先登录");
            return "error";
        }
        List<Review> allReviews = reviewClient.findAllReviews().getData();
        List<Review> reviews = new java.util.ArrayList<>();
        for (Review review : allReviews) {
            if (review.getParentId() == null) {
                enrichReview(review, user);
                reviews.add(review);
            }
        }
        model.addAttribute("reviews", reviews);
        model.addAttribute("pageTitle", "影评区");
        return "reviews";
    }

    @RequestMapping("/myreviews")
    public String myReviews(HttpSession session, Model model) {
        User user = getUserFromSession(session, model);
        if (user == null) {
            model.addAttribute("error", "请先登录");
            return "error";
        }
        List<Review> allReviews = reviewClient.findAllReviews().getData();
        List<Review> myReviews = new java.util.ArrayList<>();
        for (Review review : allReviews) {
            if (review.getUserId().equals(user.getUserId()) && review.getParentId() == null) {
                enrichReview(review, user);
                myReviews.add(review);
            }
        }
        model.addAttribute("reviews", myReviews);
        model.addAttribute("pageTitle", "我的影评");
        return "reviews";
    }

    @RequestMapping("/addreview")
    public String addreview(@RequestParam("movieId") Integer movieId, HttpSession session, Model model) {
        User user = getUserFromSession(session, model);
        if (user == null) {
            model.addAttribute("error", "请先登录");
            return "error";
        }
        Movie movie = movieClient.findMovieById(movieId).getData();
        model.addAttribute("movie", movie);
        return "addreview";
    }

    @PostMapping("/submitreview")
    public String submitreview(@RequestParam("movieId") Integer movieId, @RequestParam("score") Integer score, @RequestParam("content") String content, HttpSession session, Model model) {
        User user = getUserFromSession(session, model);
        if (user == null) {
            model.addAttribute("error", "请先登录");
            return "error";
        }
        Date createdAt = new Date();
        Review review = new Review(movieId, user.getUserId(), content, score, createdAt);
        reviewClient.insertReview(review);
        movieClient.updateAverageScore(movieId);
        return "redirect:/user/reviewsuccess";
    }

    @GetMapping("/reviewsuccess")
    public String reviewSuccess(HttpSession session, Model model) {
        getUserFromSession(session, model);
        return "rvsuccess";
    }

    @PostMapping("/replyReview")
    public String replyReview(@RequestParam("parentId") Integer parentId,
                              @RequestParam("content") String content,
                              @RequestParam("movieId") Integer movieId,
                              @RequestParam(value = "redirectTo", required = false) String redirectTo,
                              HttpSession session, Model model) {
        User user = getUserFromSession(session, model);
        if (user == null) {
            model.addAttribute("error", "请先登录");
            return "error";
        }
        Review reply = new Review();
        reply.setMovieId(movieId);
        reply.setUserId(user.getUserId());
        reply.setContent(content);
        reply.setScore(null);
        reply.setParentId(parentId);
        reply.setCreatedAt(new Date());
        reviewClient.insertReview(reply);
        if ("reviews".equals(redirectTo)) {
            return "redirect:/user/reviews";
        } else if ("myreviews".equals(redirectTo)) {
            return "redirect:/user/myreviews";
        }
        return "redirect:/user/getmovie?movieId=" + movieId;
    }

    @PostMapping("/likeReview")
    public String likeReview(@RequestParam("reviewId") Integer reviewId,
                             @RequestParam("movieId") Integer movieId,
                             @RequestParam(value = "redirectTo", required = false) String redirectTo,
                             HttpSession session, Model model) {
        User user = getUserFromSession(session, model);
        if (user == null) {
            model.addAttribute("error", "请先登录");
            return "error";
        }
        reviewClient.toggleLike(reviewId, user.getUserId());
        if ("reviews".equals(redirectTo)) {
            return "redirect:/user/reviews";
        } else if ("myreviews".equals(redirectTo)) {
            return "redirect:/user/myreviews";
        }
        return "redirect:/user/getmovie?movieId=" + movieId;
    }

    @PostMapping("/likeReviewAjax")
    @ResponseBody
    public Map<String, Object> likeReviewAjax(@RequestParam("reviewId") Integer reviewId,
                                               HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User user = getUserFromSession(session, null);
        if (user == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }
        try {
            Result<Boolean> toggleResult = reviewClient.toggleLike(reviewId, user.getUserId());
            boolean liked = toggleResult != null && toggleResult.getData() != null ? toggleResult.getData() : false;
            Result<Review> reviewResult = reviewClient.findReviewById(reviewId);
            Review review = reviewResult != null ? reviewResult.getData() : null;
            result.put("success", true);
            result.put("liked", liked);
            Integer likeCount = review != null ? review.getLikeCount() : null;
            result.put("likeCount", likeCount != null ? likeCount : 0);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "操作失败: " + e.getMessage());
        }
        return result;
    }

    private void enrichReview(Review review, User currentUser) {
        if (review == null) return;
        User user1 = userClient.findUserById(review.getUserId()).getData();
        if (user1 != null) {
            review.setUsername(user1.getUsername());
            review.setAvatar(user1.getAvatar());
            review.setNickname(user1.getNickname());
        }
        Movie movie1 = movieClient.findMovieById(review.getMovieId()).getData();
        if (movie1 != null) {
            review.setTitle(movie1.getTitle());
            review.setMoviePoster(movie1.getPosterImage());
            review.setReleaseDate(movie1.getReleaseDate() != null ? new SimpleDateFormat("yyyy").format(movie1.getReleaseDate()) : null);
            review.setAverageScore(movie1.getAverageScore());
        }
        if (currentUser != null && review.getReviewId() != null) {
            review.setLiked(reviewClient.hasLiked(review.getReviewId(), currentUser.getUserId()).getData());
        }
        // 加载回复列表
        List<Review> replies = reviewClient.findRepliesByParentId(review.getReviewId()).getData();
        if (replies != null) {
            for (Review reply : replies) {
                User replyUser = userClient.findUserById(reply.getUserId()).getData();
                if (replyUser != null) {
                    reply.setUsername(replyUser.getUsername());
                    reply.setAvatar(replyUser.getAvatar());
                    reply.setNickname(replyUser.getNickname());
                }
            }
            review.setReplies(replies);
        }
    }
}
