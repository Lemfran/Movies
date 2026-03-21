package edu.cuit.yingpingsxitong.Controller;

import edu.cuit.yingpingsxitong.Dao.ReviewDao;
import edu.cuit.yingpingsxitong.Entity.Log;
import edu.cuit.yingpingsxitong.Entity.Movie;
import edu.cuit.yingpingsxitong.Entity.Review;
import edu.cuit.yingpingsxitong.Entity.User;
import edu.cuit.yingpingsxitong.Service.LogService;
import edu.cuit.yingpingsxitong.Service.MovieService;
import edu.cuit.yingpingsxitong.Service.ReviewService;
import edu.cuit.yingpingsxitong.Service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class ManagerController {
    @Autowired
    private UserService userService;
    @Autowired
    private MovieService movieService;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private LogService logService;

    private User getUserFromSession(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            model.addAttribute("user", user);
        }
        return user;
    }

    @RequestMapping("/manager")
    public String manager(HttpSession session, Model model) {
        User user = getUserFromSession(session, model);
        if (user == null) {
            model.addAttribute("error", "请先登录");
            return "error";
        }
        List<User> users = new ArrayList<>();
        List<User> userList=userService.findAllUsers();
        for(User u:userList){
            if(!u.getManager())
                users.add(u);
        }
        model.addAttribute("users",users);
        return "manager";
    }

    @RequestMapping("/userList")
    public String userList(HttpSession session, Model model) {
        User user = getUserFromSession(session, model);
        if (user == null) {
            model.addAttribute("error", "请先登录");
            return "error";
        }
        List<User> users = new ArrayList<>();
        List<User> userList=userService.findAllUsers();
        for(User u:userList){
            if(!u.getManager())
                users.add(u);
        }
        model.addAttribute("users",users);
        return "userList";
    }

    @RequestMapping("/updatePermission")
    public String updatePermission(@RequestParam("upuserId") Integer upuserId, @RequestParam("permission") Boolean permission, HttpSession session, Model model) {
        User user = getUserFromSession(session, model);
        if (user == null) {
            model.addAttribute("error", "请先登录");
            return "error";
        }
        userService.updatePermission(upuserId,!permission);
        return "updatesuccess";
    }

    @RequestMapping("/updateManager")
    public String updateManager(@RequestParam("upuserId") Integer upuserId, @RequestParam("manager") Boolean manager, HttpSession session, Model model) {
        User user = getUserFromSession(session, model);
        if (user == null) {
            model.addAttribute("error", "请先登录");
            return "error";
        }
        userService.updateManager(upuserId,!manager);
        return "updatesuccess";
    }


    @RequestMapping("/reviewsManage")
    public String reviewsManage(HttpSession session, Model model) {
        User user = getUserFromSession(session, model);
        if (user == null) {
            model.addAttribute("error", "请先登录");
            return "error";
        }
        List<Review> reviews = reviewService.findAllReviews();
        User user1 = new User();
        Movie movie1 = new Movie();
        for(int i=0;i<reviewService.findAllReviews().size();i++) {
            user1 = userService.findUserById(reviewService.findAllReviews().get(i).getUserId());
            reviews.get(i).setUsername(user1.getUsername());
            movie1 = movieService.findMovieById(reviewService.findAllReviews().get(i).getMovieId());
            reviews.get(i).setTitle(movie1.getTitle());
        }
        model.addAttribute("reviews",reviews);
        return "reviewsmanage";
    }

    @RequestMapping("/deleteReview")
    public String deleteReview(@RequestParam("reviewId") Integer reviewId, HttpSession session, Model model) {
        User user = getUserFromSession(session, model);
        if (user == null) {
            model.addAttribute("error", "请先登录");
            return "error";
        }
        Integer movieId =reviewService.findReviewById(reviewId).getMovieId();
        reviewService.deleteReview(reviewId);
        movieService.updateAverageScore(movieId);
        model.addAttribute("review",reviewService.findReviewById(reviewId));
        return "updatesuccess";
    }

    @RequestMapping("/movieManage")
    public String movieManage(HttpSession session, Model model) {
        User user = getUserFromSession(session, model);
        if (user == null) {
            model.addAttribute("error", "请先登录");
            return "error";
        }
        List<Movie> movies;
        movies = movieService.findAllMovies();
        model.addAttribute("movies", movies);
        return "moviemanage";
    }

    @RequestMapping("/msearch")
    public String msearchMovies(@RequestParam("title") String title, HttpSession session, Model model) {
        User user = getUserFromSession(session, model);
        if (user == null) {
            model.addAttribute("error", "请先登录");
            return "error";
        }
        // 根据title搜索电影
        List<Movie> movies = movieService.getSearchList(title);
        model.addAttribute("movies", movies);
        return "moviemanage"; // 返回到Thymeleaf模板
    }

    @RequestMapping("/deleteMovie")
    public String deleteMovie(@RequestParam("movieId") Integer movieId, HttpSession session, Model model) {
        User user = getUserFromSession(session, model);
        if (user == null) {
            model.addAttribute("error", "请先登录");
            return "error";
        }
        movieService.deleteMovie(movieId);
        return "updatesuccess";
    }

    @RequestMapping("/editMovie")
    public String editMovie(@RequestParam("movieId") Integer movieId, HttpSession session, Model model) {
        User user = getUserFromSession(session, model);
        if (user == null) {
            model.addAttribute("error", "请先登录");
            return "error";
        }
        model.addAttribute("movie",movieService.findMovieById(movieId));
        return "updatemovie";
    }

    @RequestMapping("/updateMovie")
    public String updateMovie(@RequestParam("movieId") Integer movieId, @RequestParam("title") String title, @RequestParam("description") String description, @RequestParam("releaseDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date releaseDate, @RequestParam("runtime") Integer runtime,
                              @RequestParam("posterImage")  String posterImage, HttpSession session, Model model) {
        User user = getUserFromSession(session, model);
        if (user == null) {
            model.addAttribute("error", "请先登录");
            return "error";
        }
        Movie movie = new Movie(title,description,releaseDate,runtime,posterImage);
        movie.setMovieId(movieId);
        movieService.updateMovie(movie);
        return "updatesuccess";
    }

    @RequestMapping("/addMovie")
    public String addMovie(HttpSession session, Model model) {
        User user = getUserFromSession(session, model);
        if (user == null) {
            model.addAttribute("error", "请先登录");
            return "error";
        }
        return "addmovie";
    }

    @RequestMapping("/submitMovie")
    public String submitMovie(@RequestParam("title") String title, @RequestParam("description") String description, @RequestParam("releaseDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date releaseDate, @RequestParam("runtime") Integer runtime,
                              @RequestParam("posterImage") String posterImage,
                              HttpSession session, Model model) {
        User user = getUserFromSession(session, model);
        if (user == null) {
            model.addAttribute("error", "请先登录");
            return "error";
        }
        Movie movie = new Movie(title,description,releaseDate,runtime,posterImage);
        movieService.insertMovie(movie);
        return "updatesuccess";
    }

    @RequestMapping("/logList")
    public String logList(HttpSession session, Model model) {
        User user = getUserFromSession(session, model);
        if (user == null) {
            model.addAttribute("error", "请先登录");
            return "error";
        }
        List<Log> logs = logService.findAllLog();
        model.addAttribute("logs",logs);
        return "loglist";
    }
}
