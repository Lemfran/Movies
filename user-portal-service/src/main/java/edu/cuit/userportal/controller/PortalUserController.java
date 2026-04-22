package edu.cuit.userportal.controller;

import edu.cuit.userportal.client.UserClient;
import edu.cuit.userportal.client.Result;
import edu.cuit.userportal.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Controller
@SessionAttributes("user")
public class PortalUserController {

    @Autowired
    private UserClient userClient;

    private static final String UPLOAD_DIR = "/Users/fanjinchen/工程实践/Movies/uploads/avatars/";

    @RequestMapping({"/login","/"})
    public String login(Model model) {
        return "login";
    }

    @RequestMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/regist1")
    public String registUser1(@RequestParam("username") String username,
                              @RequestParam("password") String password,
                              @RequestParam("email") String email,
                              Model model) {
        User user = new User(username, password, email);
        userClient.insertUser(user);
        model.addAttribute("user", user);
        return "zcsuccess";
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            model.addAttribute("error", "请先登录");
            return "error";
        }
        Result<User> result = userClient.findUserById(user.getUserId());
        User freshUser = result.getData();
        if (freshUser != null) {
            model.addAttribute("user", freshUser);
        }
        return "profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam("nickname") String nickname,
                                @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
                                @RequestParam("email") String email,
                                HttpSession session,
                                Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            model.addAttribute("error", "请先登录");
            return "error";
        }
        user.setNickname(nickname);
        user.setEmail(email);

        if (avatarFile != null && !avatarFile.isEmpty()) {
            try {
                String originalName = avatarFile.getOriginalFilename();
                String ext = originalName != null && originalName.contains(".")
                        ? originalName.substring(originalName.lastIndexOf("."))
                        : ".jpg";
                String filename = UUID.randomUUID() + ext;
                Path targetPath = Paths.get(UPLOAD_DIR, filename);
                Files.createDirectories(targetPath.getParent());
                Files.write(targetPath, avatarFile.getBytes());
                user.setAvatar("/uploads/avatars/" + filename);
            } catch (IOException e) {
                model.addAttribute("error", "头像上传失败: " + e.getMessage());
                return "error";
            }
        }

        userClient.updateUser(user);
        session.setAttribute("user", user);
        return "redirect:/user/profile";
    }
}
