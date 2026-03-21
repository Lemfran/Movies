package edu.cuit.yingpingsxitong.Controller;

import edu.cuit.yingpingsxitong.Entity.User;
import edu.cuit.yingpingsxitong.Service.UserService;
import edu.cuit.yingpingsxitong.Service.MovieService;
import edu.cuit.yingpingsxitong.Service.ReviewService;
import edu.cuit.yingpingsxitong.Service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;

@Controller
@SessionAttributes("user")
public class IndexController {
    @Autowired
    private UserService userService;

    @Autowired
    private MovieService movieService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private LogService logService;

    // POST 请求 - 登录，成功后重定向到 /home
    @PostMapping({"/index"})
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        User user = userService.findUserByUsername(username);
        if (user != null && user.getPassword().equals(password) && user.getPermission() && user.getManager()) {
            model.addAttribute("user", user);
            return "redirect:/home";
        } else if (user != null && user.getPassword().equals(password)&& user.getPermission()){
            model.addAttribute("user", user);
            return "redirect:/home";
        } else if (user != null && user.getPassword().equals(password) && !user.getPermission()){
            model.addAttribute("error", "No Permission");
            return "error";
        }else{
            model.addAttribute("error", "Invalid Username/Password");
            return "error";
        }
    }

    // GET 请求 - 从 Session 获取用户
    @GetMapping({"/home"})
    public String home(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            model.addAttribute("error", "请先登录");
            return "error";
        }
        
        // 重新获取最新用户信息
        User freshUser = userService.findUserByUsername(user.getUsername());
        if (freshUser != null && freshUser.getPermission() && freshUser.getManager()) {
            model.addAttribute("user", freshUser);
            addStatistics(model);
            return "indexmanager";
        } else if (freshUser != null && freshUser.getPermission()){
            model.addAttribute("user", freshUser);
            addStatistics(model);
            return "index";
        } else {
            model.addAttribute("error", "No Permission");
            return "error";
        }
    }

    private void addStatistics(Model model) {
        model.addAttribute("movieCount", movieService.countMovies());
        model.addAttribute("userCount", userService.countUsers());
        model.addAttribute("reviewCount", reviewService.countReviews());
        model.addAttribute("logCount", logService.countLogs());
    }

}
