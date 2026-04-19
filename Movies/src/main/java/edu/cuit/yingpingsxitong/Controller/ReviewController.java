package edu.cuit.yingpingsxitong.Controller;

import edu.cuit.yingpingsxitong.Entity.Movie;
import edu.cuit.yingpingsxitong.Entity.Review;
import edu.cuit.yingpingsxitong.Entity.User;
import edu.cuit.yingpingsxitong.client.MovieClient;
import edu.cuit.yingpingsxitong.client.ReviewClient;
import edu.cuit.yingpingsxitong.client.UserClient;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Date;
import java.util.List;

@Controller
public class ReviewController {
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
            Movie movie1 = movieClient.findMovieById(reviews.get(i).getMovieId()).getData();
            reviews.get(i).setTitle(movie1.getTitle());
        }
        model.addAttribute("reviews", reviews);
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

    @RequestMapping("/submitreview")
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
        model.addAttribute("review", review);
        return "rvsuccess";
    }
}
