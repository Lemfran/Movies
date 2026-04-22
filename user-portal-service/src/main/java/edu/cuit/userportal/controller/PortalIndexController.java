package edu.cuit.userportal.controller;

import edu.cuit.userportal.client.MovieClient;
import edu.cuit.userportal.client.ReviewClient;
import edu.cuit.userportal.client.UserClient;
import edu.cuit.userportal.client.Result;
import edu.cuit.userportal.entity.Movie;
import edu.cuit.userportal.entity.Review;
import edu.cuit.userportal.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
@SessionAttributes("user")
public class PortalIndexController {

    @Autowired
    private UserClient userClient;

    @Autowired
    private MovieClient movieClient;

    @Autowired
    private ReviewClient reviewClient;

    @PostMapping({"/index"})
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        Model model) {
        Result<User> result = userClient.findUserByUsername(username);
        User user = result.getData();
        if (user != null && user.getPassword().equals(password) && user.getPermission() && !user.getManager()) {
            model.addAttribute("user", user);
            return "redirect:/user/home";
        } else if (user != null && user.getPassword().equals(password) && user.getPermission() && user.getManager()) {
            model.addAttribute("error", "管理员请通过管理端入口登录");
            return "error";
        } else if (user != null && user.getPassword().equals(password) && !user.getPermission()) {
            model.addAttribute("error", "No Permission");
            return "error";
        } else {
            model.addAttribute("error", "Invalid Username/Password");
            return "error";
        }
    }

    @GetMapping({"/home"})
    public String home(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            model.addAttribute("error", "请先登录");
            return "error";
        }
        Result<User> result = userClient.findUserByUsername(user.getUsername());
        User freshUser = result.getData();
        model.addAttribute("appVersion", "1.0.0");
        if (freshUser != null && freshUser.getPermission() && !freshUser.getManager()) {
            model.addAttribute("user", freshUser);
            Integer movieCount = movieClient.countMovies().getData();
            model.addAttribute("movieCount", movieCount != null ? movieCount : 0);
            model.addAttribute("userCount", userClient.countUsers().getData());
            model.addAttribute("reviewCount", reviewClient.countReviews().getData());

            // 热门电影（按评分排序取前5）
            List<Movie> allMovies = movieClient.findAllMovies().getData();
            if (allMovies != null) {
                allMovies.sort((a, b) -> Double.compare(b.getAverageScore(), a.getAverageScore()));
                int topCount = Math.min(5, allMovies.size());
                model.addAttribute("topMovies", allMovies.subList(0, topCount));
            }

            // 最新影评（取最近6条）
            List<Review> allReviews = reviewClient.findAllReviews().getData();
            if (allReviews != null) {
                allReviews.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
                int recentCount = Math.min(6, allReviews.size());
                List<Review> recentReviews = allReviews.subList(0, recentCount);
                for (Review r : recentReviews) {
                    User u = userClient.findUserById(r.getUserId()).getData();
                    r.setUsername(u.getUsername());
                    r.setAvatar(u.getAvatar());
                    r.setNickname(u.getNickname());
                    Movie m = movieClient.findMovieById(r.getMovieId()).getData();
                    r.setTitle(m.getTitle());
                }
                model.addAttribute("recentReviews", recentReviews);
            }

            // 我的最近影评
            List<Review> myReviews = reviewClient.findReviewsByUserId(freshUser.getUserId()).getData();
            List<Review> recentMyReviews = new java.util.ArrayList<>();
            int myReviewCount = 0;
            double myAvgScore = 0.0;
            if (myReviews != null) {
                myReviewCount = myReviews.size();
                if (myReviewCount > 0) {
                    myAvgScore = myReviews.stream().mapToInt(Review::getScore).average().orElse(0);
                    myReviews.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
                    int myCount = Math.min(3, myReviews.size());
                    recentMyReviews = myReviews.subList(0, myCount);
                    for (Review r : recentMyReviews) {
                        Movie m = movieClient.findMovieById(r.getMovieId()).getData();
                        r.setTitle(m.getTitle());
                    }
                }
            }
            model.addAttribute("myReviews", recentMyReviews);
            model.addAttribute("myReviewCount", myReviewCount);
            model.addAttribute("myAvgScore", myAvgScore);

            return "index";
        } else if (freshUser != null && freshUser.getManager()) {
            model.addAttribute("error", "管理员请通过管理端入口登录");
            return "error";
        } else {
            model.addAttribute("error", "No Permission");
            return "error";
        }
    }
}
