package edu.cuit.userportal.controller;

import edu.cuit.userportal.client.MovieClient;
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
        if (user != null) {
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
        List<Review> reviews = reviewClient.findAllReviews().getData();
        for(int i=0;i<reviews.size();i++) {
            User user1 = userClient.findUserById(reviews.get(i).getUserId()).getData();
            reviews.get(i).setUsername(user1.getUsername());
            reviews.get(i).setAvatar(user1.getAvatar());
            reviews.get(i).setNickname(user1.getNickname());
            Movie movie1 = movieClient.findMovieById(reviews.get(i).getMovieId()).getData();
            reviews.get(i).setTitle(movie1.getTitle());
            reviews.get(i).setMoviePoster(movie1.getPosterImage());
            reviews.get(i).setReleaseDate(movie1.getReleaseDate() != null ? new SimpleDateFormat("yyyy").format(movie1.getReleaseDate()) : null);
            reviews.get(i).setAverageScore(movie1.getAverageScore());
        }
        model.addAttribute("reviews", reviews);
        model.addAttribute("pageTitle", "影评列表");
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
            if (review.getUserId().equals(user.getUserId())) {
                User user1 = userClient.findUserById(review.getUserId()).getData();
                review.setUsername(user1.getUsername());
                review.setAvatar(user1.getAvatar());
                review.setNickname(user1.getNickname());
                Movie movie1 = movieClient.findMovieById(review.getMovieId()).getData();
                review.setTitle(movie1.getTitle());
                review.setMoviePoster(movie1.getPosterImage());
                review.setReleaseDate(movie1.getReleaseDate() != null ? new SimpleDateFormat("yyyy").format(movie1.getReleaseDate()) : null);
                review.setAverageScore(movie1.getAverageScore());
                myReviews.add(review);
            }
        }
        model.addAttribute("reviews", myReviews);
        model.addAttribute("pageTitle", "我的评论");
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
}
