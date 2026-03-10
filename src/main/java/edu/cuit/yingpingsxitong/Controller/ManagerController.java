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
@SessionAttributes("userId")
public class ManagerController {
    @Autowired
    private UserService userService;
    @Autowired
    private MovieService movieService;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private LogService logService;

    @RequestMapping("/manager")
    public String manager(@RequestParam("userId") Integer userId , Model model) {
        model.addAttribute("user",userService.findUserById(userId));
        return "manager";
    }

    @RequestMapping("/userList")
    public String userList(@RequestParam("userId") Integer userId ,Model model) {
        List<User> users = new ArrayList<>();
        List<User> userList=userService.findAllUsers();
        for(User u:userList){
            if(!u.getManager())
                users.add(u);
        }
        model.addAttribute("users",users);
        model.addAttribute("user",userService.findUserById(userId));
        return "userList";
    }

    @RequestMapping("/updatePermission")
    public String updatePermission(@RequestParam("upuserId") Integer upuserId,@RequestParam("userId") Integer userId,@RequestParam("permission") Boolean permission,Model model) {
        userService.updatePermission(upuserId,!permission);
        model.addAttribute("user",userService.findUserById(userId));
        return "updatesuccess";
    }

    @RequestMapping("/updateManager")
    public String updateManager(@RequestParam("upuserId") Integer upuserId,@RequestParam("userId") Integer userId,@RequestParam("manager") Boolean manager,Model model) {
        userService.updateManager(upuserId,!manager);
        model.addAttribute("user",userService.findUserById(userId));
        return "updatesuccess";
    }


    @RequestMapping("/reviewsManage")
    public String reviewsManage(@RequestParam("userId") Integer userId,Model model) {
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
        model.addAttribute("user",userService.findUserById(userId));
        return "reviewsmanage";
    }

    @RequestMapping("/deleteReview")
    public String deleteReview(@RequestParam("userId") Integer userId,@RequestParam("reviewId") Integer reviewId,Model model) {
        Integer movieId =reviewService.findReviewById(reviewId).getMovieId();
        reviewService.deleteReview(reviewId);
        movieService.updateAverageScore(movieId);
        model.addAttribute("user",userService.findUserById(userId));
        model.addAttribute("review",reviewService.findReviewById(reviewId));
        return "updatesuccess";
    }

    @RequestMapping("/movieManage")
    public String movieManage(@RequestParam("userId") Integer userId, Model model) {
        List<Movie> movies;
        movies = movieService.findAllMovies();
        User user = userService.findUserById(userId);
        model.addAttribute("movies", movies);
        model.addAttribute("user", user);
        return "moviemanage";
    }

    @RequestMapping("/msearch")
    public String msearchMovies(@RequestParam("userId") Integer userId, @RequestParam("title") String title, Model model) {
        // 根据title搜索电影
        List<Movie> movies = movieService.getSearchList(title);
        model.addAttribute("movies", movies);
        model.addAttribute("user", userService.findUserById(userId));
        return "moviemanage"; // 返回到Thymeleaf模板
    }

    @RequestMapping("/deleteMovie")
    public String deleteMovie(@RequestParam("userId") Integer userId,@RequestParam("movieId") Integer movieId,Model model) {
        movieService.deleteMovie(movieId);
        model.addAttribute("user",userService.findUserById(userId));
        return "updatesuccess";
    }

    @RequestMapping("/editMovie")
    public String editMovie(@RequestParam("userId") Integer userId,@RequestParam("movieId") Integer movieId,Model model) {
        model.addAttribute("user",userService.findUserById(userId));
        model.addAttribute("movie",movieService.findMovieById(movieId));
        return "updatemovie";
    }

    @RequestMapping("/updateMovie")
    public String updateMovie(@RequestParam("userId") Integer userId, @RequestParam("movieId") Integer movieId, @RequestParam("title") String title, @RequestParam("description") String description, @RequestParam("releaseDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date releaseDate, @RequestParam("runtime") Integer runtime,
                              @RequestParam("posterImage")  String posterImage, Model model) {
        Movie movie = new Movie(title,description,releaseDate,runtime,posterImage);
        movie.setMovieId(movieId);
        movieService.updateMovie(movie);
        model.addAttribute("user",userService.findUserById(userId));
        return "updatesuccess";
    }

    @RequestMapping("/addMovie")
    public String addMovie(@RequestParam("userId") Integer userId,Model model) {
        model.addAttribute("user",userService.findUserById(userId));
        return "addmovie";
    }

    @RequestMapping("/submitMovie")
    public String submitMovie(@RequestParam("userId") Integer userId,
                              @RequestParam("title") String title, @RequestParam("description") String description, @RequestParam("releaseDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date releaseDate, @RequestParam("runtime") Integer runtime,
                              @RequestParam("posterImage") String posterImage,
                              Model model) {
        Movie movie = new Movie(title,description,releaseDate,runtime,posterImage);
        movieService.insertMovie(movie);
        model.addAttribute("user",userService.findUserById(userId));
        return "updatesuccess";
    }

    @RequestMapping("/logList")
    public String logList(@RequestParam("userId") Integer userId,Model model) {
        List<Log> logs = logService.findAllLog();
        model.addAttribute("logs",logs);
        model.addAttribute("user",userService.findUserById(userId));
        return "loglist";
    }
}
