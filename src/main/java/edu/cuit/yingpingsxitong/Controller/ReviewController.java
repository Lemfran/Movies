package edu.cuit.yingpingsxitong.Controller;

import edu.cuit.yingpingsxitong.Entity.Movie;
import edu.cuit.yingpingsxitong.Entity.Review;
import edu.cuit.yingpingsxitong.Entity.User;
import edu.cuit.yingpingsxitong.Service.MovieService;
import edu.cuit.yingpingsxitong.Service.ReviewService;
import edu.cuit.yingpingsxitong.Service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.Date;
import java.util.List;

@Controller

public class ReviewController {
    @Autowired
    private MovieService movieService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserService userService;


    @RequestMapping("/reviews")
    public String review(@RequestParam("userId") Integer userId,Model model) {
        User user = userService.findUserById(userId);
        List<Review> reviews = reviewService.findAllReviews();
        User user1 = new User();
        Movie movie1 = new Movie();
        for(int i=0;i<reviewService.findAllReviews().size();i++) {
            user1 = userService.findUserById(reviewService.findAllReviews().get(i).getUserId());
            reviews.get(i).setUsername(user1.getUsername());
            movie1 = movieService.findMovieById(reviewService.findAllReviews().get(i).getMovieId());
            reviews.get(i).setTitle(movie1.getTitle());
        }
        model.addAttribute("reviews", reviews);
        model.addAttribute("user", user);
        return "reviews";
    }

    @RequestMapping("/addreview")
    public String addreview(@RequestParam("movieId") Integer movieId,@RequestParam("userId") Integer userId, Model model) {
        Movie movie = movieService.findMovieById(movieId);
        User user = userService.findUserById(userId);
        model.addAttribute("movie", movie);
        model.addAttribute("user", user);
        return "addreview";
    }

    @RequestMapping("/submitreview")
    public String submitreview(@RequestParam("movieId") Integer movieId,@RequestParam("userId") Integer userId,@RequestParam("score") Integer score,@RequestParam("content") String content, Model model) {
        Movie movie = movieService.findMovieById(movieId);
        User user = userService.findUserById(userId);
        Date createdAt= new Date();
        Review review = new Review(movieId,userId,content,score,createdAt);
        reviewService.insertReview(review);
        movieService.updateAverageScore(movieId);
        model.addAttribute("review", review);
        model.addAttribute("user", user);
        return "rvsuccess";
    }

}
