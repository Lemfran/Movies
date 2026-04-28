package edu.cuit.yingpingsxitong.Controller;

import edu.cuit.yingpingsxitong.Entity.User;
import edu.cuit.yingpingsxitong.Service.UserService;
import edu.cuit.yingpingsxitong.client.MovieClient;
import edu.cuit.yingpingsxitong.client.ReviewClient;
import edu.cuit.yingpingsxitong.client.LogClient;
import edu.cuit.yingpingsxitong.client.UserClient;
import edu.cuit.yingpingsxitong.client.Result;
import edu.cuit.yingpingsxitong.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@SessionAttributes("user")
@RefreshScope
public class IndexController {

    @Value("${app.version:1.0.0}")
    private String appVersion;

    @Autowired
    private UserClient userClient;

    @Autowired
    private UserService userService;

    @Autowired
    private MovieClient movieClient;

    @Autowired
    private ReviewClient reviewClient;

    @Autowired
    private LogClient logClient;

    @Autowired
    private JwtUtil jwtUtil;

    // POST 请求 - 登录，成功后重定向到 /home
    @PostMapping({"/index"})
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        Model model,
                        HttpServletResponse response) {
        Result<User> result = userClient.findUserByUsername(username);
        User user = result.getData();
        boolean passwordValid = user != null && userService.validatePassword(password, user.getPassword());
        if (passwordValid && user.getPermission() && user.getManager()) {
            model.addAttribute("user", user);
            // 同时生成 JWT Token 写入 Cookie
            String token = jwtUtil.generateToken(user.getUserId(), user.getUsername());
            Cookie jwtCookie = new Cookie("jwt_token", token);
            jwtCookie.setPath("/");
            jwtCookie.setHttpOnly(true);
            jwtCookie.setMaxAge(86400);
            response.addCookie(jwtCookie);
            return "redirect:/admin/home";
        } else if (passwordValid && user.getPermission() && !user.getManager()) {
            model.addAttribute("error", "普通用户请通过用户端入口访问");
            return "error";
        } else if (passwordValid && !user.getPermission()){
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
        Result<User> result = userClient.findUserByUsername(user.getUsername());
        User freshUser = result.getData();
        model.addAttribute("appVersion", appVersion);
        if (freshUser != null && freshUser.getPermission() && freshUser.getManager()) {
            model.addAttribute("user", freshUser);
            addStatistics(model);
            return "indexmanager";
        } else if (freshUser != null && freshUser.getPermission() && !freshUser.getManager()) {
            model.addAttribute("error", "普通用户请通过用户端入口访问");
            return "error";
        } else {
            model.addAttribute("error", "No Permission");
            return "error";
        }
    }

    private void addStatistics(Model model) {
        model.addAttribute("movieCount", movieClient.countMovies().getData());
        model.addAttribute("userCount", userClient.countUsers().getData());
        model.addAttribute("reviewCount", reviewClient.countReviews().getData());
        model.addAttribute("logCount", logClient.countLogs().getData());
    }

}
