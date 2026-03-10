package edu.cuit.yingpingsxitong.Controller;

import edu.cuit.yingpingsxitong.Entity.User;
import edu.cuit.yingpingsxitong.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
public class IndexController {
    @Autowired
    private UserService userService;

    @RequestMapping({"/index"})
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        Model model) {
        User user = userService.findUserByUsername(username);
        if (user != null && user.getPassword().equals(password) && user.getPermission() && user.getManager()) {
            model.addAttribute("user", user);
            return "indexmanager";
        } else if (user != null && user.getPassword().equals(password)&& user.getPermission()){
            model.addAttribute("user", user);
            return "index";
        } else if (user != null && user.getPassword().equals(password) && !user.getPermission()){
            model.addAttribute("error", "No Permission");
            return "error";
        }else{
            model.addAttribute("error", "Invalid Username/Password");
            return "error";
        }
    }

}