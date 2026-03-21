package edu.cuit.yingpingsxitong.Controller;

import edu.cuit.yingpingsxitong.Entity.Review;
import edu.cuit.yingpingsxitong.Entity.User;
import edu.cuit.yingpingsxitong.Service.ReviewService;
import edu.cuit.yingpingsxitong.Service.UserService;
import jakarta.servlet.http.HttpSession;
import edu.cuit.yingpingsxitong.Entity.Movie;
import edu.cuit.yingpingsxitong.Service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@SessionAttributes("movies")
public class MovieListController {
    @Autowired
    private MovieService movieService;
    @Autowired
    private UserService userService;
    @Autowired
    private ReviewService reviewService;

    private User getUserFromSession(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            model.addAttribute("user", user);
        }
        return user;
    }

    @RequestMapping("/search")
    public String searchMovies(@RequestParam("title") String title, HttpSession session, Model model) {
        // 根据title搜索电影
        List<Movie> movies = movieService.getSearchList(title);
        model.addAttribute("movies", movies);
        getUserFromSession(session, model);
        return "movielist"; // 返回到Thymeleaf模板
    }

    @GetMapping("/movielist")
    public String listMovies(HttpSession session, Model model) {
        List<Movie> movies;
        movies = movieService.findAllMovies();
        model.addAttribute("movies", movies);
        getUserFromSession(session, model);
        return "movielist"; // 返回到Thymeleaf模板的名称
    }

    @GetMapping("/getmovie")
    public String getmovie(@RequestParam("movieId") Integer movieId, HttpSession session, Model model) {
        Movie movie = movieService.findMovieById(movieId);
        List<Review> reviews =reviewService.findReviewsByMovieId(movieId);
        User user1 = new User();
        for(int i=0;i<reviewService.findReviewsByMovieId(movieId).size();i++) {
            user1 = userService.findUserById(reviewService.findReviewsByMovieId(movieId).get(i).getUserId());
            reviews.get(i).setUsername(user1.getUsername());
        }
        if (movie != null) {
            model.addAttribute("movie", movie);
            model.addAttribute("reviews", reviews);
            getUserFromSession(session, model);
            return "review";
        }
        return "error";
    }
}
