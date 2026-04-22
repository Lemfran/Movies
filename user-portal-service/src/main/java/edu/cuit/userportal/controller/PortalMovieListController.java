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
import java.util.List;

@Controller
@SessionAttributes("movies")
public class PortalMovieListController {
    @Autowired
    private MovieClient movieClient;
    @Autowired
    private UserClient userClient;
    @Autowired
    private ReviewClient reviewClient;

    private User getUserFromSession(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            model.addAttribute("user", user);
        }
        return user;
    }

    @RequestMapping("/search")
    public String searchMovies(@RequestParam("title") String title, HttpSession session, Model model) {
        List<Movie> movies = movieClient.getSearchList(title).getData();
        model.addAttribute("movies", movies);
        getUserFromSession(session, model);
        return "movielist";
    }

    @GetMapping("/movielist")
    public String listMovies(HttpSession session, Model model) {
        List<Movie> movies = movieClient.findAllMovies().getData();
        model.addAttribute("movies", movies);
        getUserFromSession(session, model);
        return "movielist";
    }

    @GetMapping("/getmovie")
    public String getmovie(@RequestParam("movieId") Integer movieId, HttpSession session, Model model) {
        Movie movie = movieClient.findMovieById(movieId).getData();
        List<Review> reviews = reviewClient.findReviewsByMovieId(movieId).getData();
        for(int i=0;i<reviews.size();i++) {
            User user1 = userClient.findUserById(reviews.get(i).getUserId()).getData();
            reviews.get(i).setUsername(user1.getUsername());
            reviews.get(i).setAvatar(user1.getAvatar());
            reviews.get(i).setNickname(user1.getNickname());
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
