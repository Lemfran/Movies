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
import java.util.List;

@Controller
@SessionAttributes("movies")
public class MovieListController {
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
