package edu.cuit.yingpingsxitong.Controller;

import edu.cuit.yingpingsxitong.Entity.Log;
import edu.cuit.yingpingsxitong.Entity.Movie;
import edu.cuit.yingpingsxitong.Entity.Review;
import edu.cuit.yingpingsxitong.Entity.User;
import edu.cuit.yingpingsxitong.client.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Controller
public class ManagerController {
    @Autowired
    private UserClient userClient;
    @Autowired
    private MovieClient movieClient;
    @Autowired
    private ReviewClient reviewClient;
    @Autowired
    private LogClient logClient;

    private static final String UPLOAD_DIR = "/Users/fanjinchen/工程实践/Movies/uploads/posters/";

    private String savePosterFile(MultipartFile posterFile) {
        if (posterFile == null || posterFile.isEmpty()) {
            return null;
        }
        try {
            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String originalName = posterFile.getOriginalFilename();
            String ext = "";
            if (originalName != null && originalName.contains(".")) {
                ext = originalName.substring(originalName.lastIndexOf("."));
            }
            String filename = UUID.randomUUID() + ext;
            Path path = Paths.get(UPLOAD_DIR + filename);
            Files.write(path, posterFile.getBytes());
            return "/uploads/posters/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("海报上传失败", e);
        }
    }

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
        List<User> userList = userClient.findAllUsers().getData();
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
        List<User> userList = userClient.findAllUsers().getData();
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
        userClient.updatePermission(upuserId,!permission);
        return "updatesuccess";
    }

    @RequestMapping("/updateManager")
    public String updateManager(@RequestParam("upuserId") Integer upuserId, @RequestParam("manager") Boolean manager, HttpSession session, Model model) {
        User user = getUserFromSession(session, model);
        if (user == null) {
            model.addAttribute("error", "请先登录");
            return "error";
        }
        userClient.updateManager(upuserId,!manager);
        return "updatesuccess";
    }

    @RequestMapping("/reviewsManage")
    public String reviewsManage(HttpSession session, Model model) {
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
        Integer movieId = reviewClient.findReviewById(reviewId).getData().getMovieId();
        reviewClient.deleteReview(reviewId);
        movieClient.updateAverageScore(movieId);
        return "updatesuccess";
    }

    @RequestMapping("/movieManage")
    public String movieManage(HttpSession session, Model model) {
        User user = getUserFromSession(session, model);
        if (user == null) {
            model.addAttribute("error", "请先登录");
            return "error";
        }
        List<Movie> movies = movieClient.findAllMovies().getData();
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
        List<Movie> movies = movieClient.getSearchList(title).getData();
        model.addAttribute("movies", movies);
        return "moviemanage";
    }

    @RequestMapping("/deleteMovie")
    public String deleteMovie(@RequestParam("movieId") Integer movieId, HttpSession session, Model model) {
        User user = getUserFromSession(session, model);
        if (user == null) {
            model.addAttribute("error", "请先登录");
            return "error";
        }
        movieClient.deleteMovie(movieId);
        return "updatesuccess";
    }

    @RequestMapping("/editMovie")
    public String editMovie(@RequestParam("movieId") Integer movieId, HttpSession session, Model model) {
        User user = getUserFromSession(session, model);
        if (user == null) {
            model.addAttribute("error", "请先登录");
            return "error";
        }
        model.addAttribute("movie", movieClient.findMovieById(movieId).getData());
        return "updatemovie";
    }

    @RequestMapping("/updateMovie")
    public String updateMovie(@RequestParam("movieId") Integer movieId, @RequestParam("title") String title, @RequestParam("description") String description, @RequestParam("releaseDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date releaseDate, @RequestParam("runtime") Integer runtime,
                              @RequestParam(value = "posterImage", required = false) String posterImage, @RequestParam(value = "posterFile", required = false) MultipartFile posterFile, HttpSession session, Model model) {
        User user = getUserFromSession(session, model);
        if (user == null) {
            model.addAttribute("error", "请先登录");
            return "error";
        }
        String savedPoster = savePosterFile(posterFile);
        if (savedPoster != null) {
            posterImage = savedPoster;
        }
        Movie movie = new Movie(title,description,releaseDate,runtime,posterImage);
        movie.setMovieId(movieId);
        movieClient.updateMovie(movie);
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
                              @RequestParam(value = "posterImage", required = false) String posterImage, @RequestParam(value = "posterFile", required = false) MultipartFile posterFile,
                              HttpSession session, Model model) {
        User user = getUserFromSession(session, model);
        if (user == null) {
            model.addAttribute("error", "请先登录");
            return "error";
        }
        String savedPoster = savePosterFile(posterFile);
        if (savedPoster != null) {
            posterImage = savedPoster;
        }
        Movie movie = new Movie(title,description,releaseDate,runtime,posterImage);
        movieClient.insertMovie(movie);
        return "updatesuccess";
    }

    @RequestMapping("/logList")
    public String logList(HttpSession session, Model model) {
        User user = getUserFromSession(session, model);
        if (user == null) {
            model.addAttribute("error", "请先登录");
            return "error";
        }
        List<Log> logs = logClient.findAllLog().getData();
        model.addAttribute("logs",logs);
        return "loglist";
    }
}
